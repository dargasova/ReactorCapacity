package gui;

import reactors.Reactor;
import readers.DBReader;
import regions.Regions;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

public class GUI extends JFrame {

    private JButton importButton;
    private JButton goCalculateButton;
    private JButton exitButton;
    private JPanel mainPanel;
    private JTree reactorsTree;
    private Regions regions;
    private Map<String, List<Reactor>> reactors;

    public GUI() throws URISyntaxException {
        setLookAndFeel();
        initializeComponents();
        setupFrame();
        createUIComponents();
        addListeners();
        setVisible(true);
    }

    public static void main(String[] args) throws URISyntaxException {
        new GUI();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeComponents() {
        importButton = new JButton("Выбрать базу данных");
        goCalculateButton = new JButton("Провести расчеты потребления");
        exitButton = new JButton("Выйти из программы");
        mainPanel = new JPanel(new BorderLayout());
        reactorsTree = new JTree(new DefaultMutableTreeNode("Реакторы"));
        reactorsTree.setEnabled(false);
        goCalculateButton.setEnabled(false);
        exitButton.setEnabled(false);
    }

    private void setupFrame() {
        setTitle("Лабораторная работа № 4");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(mainPanel);
    }

    private void createUIComponents() {
        JScrollPane scrollPane = new JScrollPane(reactorsTree);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(buttonPanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();

        Dimension buttonSize = new Dimension(300, 60);
        Color buttonColor = new Color(0, 128, 255);

        styleButton(importButton, buttonSize, buttonColor);
        styleButton(goCalculateButton, buttonSize, buttonColor);
        styleButton(exitButton, buttonSize, buttonColor);

        buttonPanel.add(importButton, gbc);
        buttonPanel.add(goCalculateButton, gbc);
        buttonPanel.add(exitButton, gbc);

        return buttonPanel;
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        return gbc;
    }

    private void styleButton(JButton button, Dimension size, Color color) {
        button.setPreferredSize(size);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
    }

    private void addListeners() {
        importButton.addActionListener(e -> showFileChooser());
        reactorsTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showReactorDetails();
                }
            }
        });
        goCalculateButton.addActionListener(e -> showCalculatorDialog());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Файлы базы данных", "db");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File("./"));
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.getName().toLowerCase().endsWith(".db")) {
                fillTree(file);
            } else {
                showErrorDialog("Выберите файл формата .db");
            }
        }
    }

    private void showReactorDetails() {
        TreePath selectionPath = reactorsTree.getSelectionPath();
        if (selectionPath == null) return;

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
        if (selectedNode.getUserObject() instanceof Reactor reactor) {
            String title = "Реактор " + reactor.getName();
            String message = reactor.getFullDescription();

            JPanel panel = new JPanel(new BorderLayout());
            ImageIcon reactorIcon = new ImageIcon("C:\\Users\\User\\IdeaProjects\\Lab4\\src\\reactor.png");
            Image scaledImage = reactorIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            JLabel iconLabel = new JLabel(scaledIcon);
            panel.add(iconLabel, BorderLayout.WEST);
            JTextArea textArea = new JTextArea(message);
            panel.add(textArea, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(null, panel, title, JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void showCalculatorDialog() {
        ConsumptionCalculationsGUI dialog = new ConsumptionCalculationsGUI(regions, reactors);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void fillTree(File file) {
        try {
            reactors = new TreeMap<>(DBReader.importReactors(file));
            regions = DBReader.importRegions(file);
            populateTree();
            reactorsTree.setEnabled(true);
            goCalculateButton.setEnabled(true);
            exitButton.setEnabled(true);
        } catch (SQLException e) {
            showErrorDialog("Ошибка при импорте базы данных");
        }
    }

    private void populateTree() {
        DefaultTreeModel treeModel = (DefaultTreeModel) reactorsTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Реакторы");

        Map<String, DefaultMutableTreeNode> countryNodes = new HashMap<>();

        for (Map.Entry<String, List<Reactor>> entry : reactors.entrySet()) {
            String country = entry.getValue().getFirst().getCountry();

            DefaultMutableTreeNode countryNode = countryNodes.get(country);

            if (countryNode == null) {
                countryNode = new DefaultMutableTreeNode(country);
                countryNodes.put(country, countryNode);
                root.add(countryNode);
            }

            for (Reactor reactor : entry.getValue()) {
                DefaultMutableTreeNode reactorNode = new DefaultMutableTreeNode(reactor);
                countryNode.add(reactorNode);
            }
        }

        List<DefaultMutableTreeNode> sortedCountryNodes = new ArrayList<>(countryNodes.values());
        sortedCountryNodes.sort(Comparator.comparing(DefaultMutableTreeNode::toString));

        sortedCountryNodes.forEach(root::add);

        treeModel.setRoot(root);
    }


    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}
