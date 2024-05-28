import importers.DatabaseImporter;
import reactors.Reactor;
import regions.Regions;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GUI extends JFrame {

    private JButton importButton;
    private JPanel panel;
    private JTree reactorsTree;
    private JButton goCalculateButton;
    private JButton exitButton; // Добавляем кнопку для выхода

    private Regions regions;
    private Map<String, List<Reactor>> reactors;

    public GUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        createUIComponents();
        setContentPane(panel);
        setTitle("Reactors");
        setSize(900, 900);

        addListeners();

        DefaultTreeModel treeModel = (DefaultTreeModel) reactorsTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Реакторы");
        treeModel.setRoot(root);

        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addListeners() {
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Файлы базы данных sqlite3 (.db)", "db"
                );
                fileChooser.setFileFilter(filter);
                File defaultDirectory = new File("./");
                fileChooser.setCurrentDirectory(defaultDirectory);
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (!(file.getName().toLowerCase().endsWith(".db"))) {
                        JOptionPane.showMessageDialog(
                                null, "Выберите файл формата .db", "Ошибка",
                                JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }
                    fillTree(file);
                }
            }
        });

        reactorsTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() != 2) {
                    return;
                }
                TreePath selectionPath = reactorsTree.getSelectionPath();
                if (selectionPath == null) {
                    return;
                }
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                if (selectedNode.getUserObject() instanceof String) {
                    return;
                }
                Reactor reactor = (Reactor) selectedNode.getUserObject();
                JOptionPane.showMessageDialog(
                        null, reactor.getFullDescription(),
                        "Реактор " + reactor.getName(),
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        goCalculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GroupedCalculatorWindow dialog = new GroupedCalculatorWindow(regions, reactors);
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });

        // Добавляем обработчик для кнопки выхода
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void fillTree(File file) {
        try {
            DefaultTreeModel treeModel = (DefaultTreeModel) reactorsTree.getModel();
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Реакторы");
            reactors = new TreeMap<>(DatabaseImporter.importReactors(file));
            regions = DatabaseImporter.importRegions(file);
            for (Map.Entry<String, List<Reactor>> entry : reactors.entrySet()) {
                String country = entry.getKey();
                List<Reactor> reactorList = entry.getValue();
                DefaultMutableTreeNode countryNode = new DefaultMutableTreeNode(country);
                root.add(countryNode);
                for (Reactor reactor : reactorList) {
                    DefaultMutableTreeNode reactorNode = new DefaultMutableTreeNode(reactor);
                    countryNode.add(reactorNode);
                }
            }
            treeModel.setRoot(root);
            reactorsTree.setEnabled(true);
            goCalculateButton.setEnabled(true);
            importButton.setText("Выбран: " + file.getName());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null, "Ошибка при импорте базы данных", "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void createUIComponents() {
        panel = new JPanel(new BorderLayout());

        importButton = new JButton("Импортировать базу данных");
        goCalculateButton = new JButton("Перейти к агрегированным расчетам");

        // Инициализируем кнопку выхода и устанавливаем ей стиль
        exitButton = new JButton("Выйти из программы");
        exitButton.setBackground(new Color(0, 128, 255));
        exitButton.setForeground(Color.WHITE);
        exitButton.setPreferredSize(new Dimension(400, 60));

        reactorsTree = new JTree(new DefaultMutableTreeNode("Реакторы"));
        JScrollPane scrollPane = new JScrollPane(reactorsTree);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Стилизация кнопок
        Color buttonColor = new Color(0, 128, 255);
        importButton.setBackground(buttonColor);
        importButton.setForeground(Color.WHITE);
        goCalculateButton.setBackground(buttonColor);
        goCalculateButton.setForeground(Color.WHITE);

        // Устанавливаем размер кнопок
        Dimension buttonSize = new Dimension(400, 60);
        importButton.setPreferredSize(buttonSize);
        goCalculateButton.setPreferredSize(buttonSize);

        // Размещаем кнопки в центре панели
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(importButton);
        buttonPanel.add(goCalculateButton);
        buttonPanel.add(exitButton); // Добавляем кнопку выхода

        // Размещаем компоненты на панели с использованием BorderLayout
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        GUI window = new GUI();
    }
}