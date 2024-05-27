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
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton byCountryButton;
    private JButton byOperatorButton;
    private JButton byRegionButton;
    private JTable resultTable;
    private ConsumptionCalculator calculator;

    public GroupedCalculatorWindow(Regions regions, Map<String, List<Reactor>> reactors) {
        calculator = new ConsumptionCalculator(reactors, regions);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        addListeners();
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
        buttonOK.addActionListener(e -> dispose());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        contentPane.registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        addCalculateListeners();
    }

    private void createUIComponents() {
        contentPane = new JPanel(new BorderLayout(10, 10));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        byCountryButton = new JButton("По стране");
        byOperatorButton = new JButton("По оператору");
        byRegionButton = new JButton("По региону");
        buttonPanel.add(byCountryButton);
        buttonPanel.add(byOperatorButton);
        buttonPanel.add(byRegionButton);

        buttonOK = new JButton("OK");

        resultTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Результаты расчетов"));

        contentPane.add(buttonPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(buttonOK, BorderLayout.SOUTH);
    }
}
