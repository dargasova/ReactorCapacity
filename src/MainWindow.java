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

public class MainWindow extends JFrame {

    private JButton importButton;
    private JPanel panel;
    private JTree reactorsTree;
    private JButton goCalculateButton;
    private Regions regions;
    private Map<String, List<Reactor>> reactors;

    public MainWindow() {
        createUIComponents();
        setContentPane(panel);
        setTitle("Reactors");
        setSize(500, 500);

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

                if (e.getClickCount() != 2) { return; }

                TreePath selectionPath = reactorsTree.getSelectionPath();
                if (selectionPath == null) { return; }

                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                if (selectedNode.getUserObject() instanceof String) { return; }

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
                dialog.pack(); // Устанавливаем размер окна в соответствии с содержимым
                dialog.setLocationRelativeTo(null); // Отображаем окно по центру экрана
                dialog.setVisible(true);
            }
        });

    }

    private void fillTree(File file) {
        try {
            DefaultTreeModel treeModel = (DefaultTreeModel) reactorsTree.getModel();
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Реакторы");
            reactors = new TreeMap<>(DatabaseImporter.importReactors(file));
            regions = DatabaseImporter.importRegions(file);

            // Проходим по словарю и добавляем узлы для каждой страны и их реакторов
            for (Map.Entry<String, List<Reactor>> entry : reactors.entrySet()) {
                String country = entry.getKey();
                List<Reactor> reactorList = entry.getValue();

                // Создаем узел для страны
                DefaultMutableTreeNode countryNode = new DefaultMutableTreeNode(country);

                // Добавляем узел страны к корневому узлу
                root.add(countryNode);

                // Добавляем узлы для каждого реактора в списке реакторов
                for (Reactor reactor : reactorList) {
                    DefaultMutableTreeNode reactorNode = new DefaultMutableTreeNode(reactor);
                    countryNode.add(reactorNode);
                }
            }

            treeModel.setRoot(root);
            reactorsTree.setEnabled(true);
            goCalculateButton.setEnabled(true);
            importButton.setText("Выбран: " + file.getName());
        }

        catch (SQLException e) {
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
        goCalculateButton.setEnabled(false);

        reactorsTree = new JTree(new DefaultMutableTreeNode("Реакторы"));
        reactorsTree.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(reactorsTree);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(importButton, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(goCalculateButton, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        MainWindow window = new MainWindow();
    }
}