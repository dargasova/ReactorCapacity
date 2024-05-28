import reactors.ConsumptionCalculator;
import reactors.Reactor;
import regions.Regions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GroupedCalculatorWindow extends JDialog {
    private JPanel contentPane = new JPanel();
    private JButton buttonOK = new JButton("OK");
    private JButton byCountryButton = new JButton("By Country");
    private JButton byOperatorButton = new JButton("By Operator");
    private JButton byRegionButton = new JButton("By Region");
    private JTable resultTable;
    private ConsumptionCalculator calculator;

    public GroupedCalculatorWindow(Regions regions, Map<String, List<Reactor>> reactors) {
        calculator = new ConsumptionCalculator(reactors, regions);

        setContentPane(contentPane); // Устанавливаем созданный JPanel в качестве contentPane
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        initializeComponents(); // Добавляем инициализацию компонентов GUI
        addListeners();
    }

    private void initializeComponents() {
        contentPane.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        buttonPanel.add(byCountryButton);
        buttonPanel.add(byOperatorButton);
        buttonPanel.add(byRegionButton);

        contentPane.add(buttonPanel, BorderLayout.NORTH);

        resultTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        contentPane.add(buttonOK, BorderLayout.SOUTH);
    }

    private void addCalculateListeners() {
        byCountryButton.addActionListener(e -> {
            Map<String, Map<Integer, Double>> consumptionByCountries = calculator.calculateConsumptionByCountries();
            updateTable(consumptionByCountries, "Страна");
        });

        byOperatorButton.addActionListener(e -> {
            Map<String, Map<Integer, Double>> consumptionByOperators = calculator.calculateConsumptionByOperator();
            updateTable(consumptionByOperators, "Оператор");
        });

        byRegionButton.addActionListener(e -> {
            Map<String, Map<Integer, Double>> consumptionByRegions = calculator.calculateConsumptionByRegions();
            updateTable(consumptionByRegions, "Регион");
        });
    }

    private void updateTable(Map<String, Map<Integer, Double>> data, String header) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{header, "Потребление", "Год"});

        for (String group : data.keySet()) {
            Map<Integer, Double> consumptionByYear = data.get(group);
            for (Integer year : consumptionByYear.keySet()) {
                model.addRow(new Object[]{group, String.format("%1$.2f", consumptionByYear.get(year)), year});
            }
        }

        resultTable.setModel(model);
    }

    private void addListeners() {
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        addCalculateListeners();
    }
}