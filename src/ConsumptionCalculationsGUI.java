import reactors.ConsumptionCalculator;
import reactors.Reactor;
import regions.Regions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumptionCalculationsGUI extends JDialog {
    private JPanel contentPane;
    private JButton byCountryButton;
    private JButton byOperatorButton;
    private JButton byRegionButton;
    private JTable resultTable;
    private final ConsumptionCalculator calculator;
    private final Regions regions;

    public ConsumptionCalculationsGUI(Regions regions, Map<String, List<Reactor>> reactors) {
        this.regions = regions;
        calculator = new ConsumptionCalculator(reactors);

        initializeComponents();
        setupDialog();
        addListeners();
    }

    private void initializeComponents() {
        contentPane = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        byCountryButton = createStyledButton("Страна");
        leftPanel.add(byCountryButton, gbc);
        byOperatorButton = createStyledButton("Оператор");
        leftPanel.add(byOperatorButton, gbc);
        byRegionButton = createStyledButton("Регион");
        leftPanel.add(byRegionButton, gbc);

        contentPane.add(leftPanel, BorderLayout.WEST);

        resultTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        setContentPane(contentPane);
    }

    private void setupDialog() {
        setModal(true);
        setTitle("Расчет потребления");
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        Dimension buttonSize = new Dimension(200, 40);
        Color buttonColor = new Color(0, 128, 255);

        button.setPreferredSize(buttonSize);
        button.setBackground(buttonColor);
        button.setForeground(Color.WHITE);

        return button;
    }

    private void addListeners() {
        addCalculateListeners();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        contentPane.registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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
            Map<String, Map<Integer, Double>> consumptionByRegions = calculator.calculateConsumptionByRegions(regions);
            updateTable(consumptionByRegions, "Регион");
        });
    }

    private void updateTable(Map<String, Map<Integer, Double>> data, String header) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{header, "Потребление", "Год"});

        data.forEach((group, consumptionByYear) ->
                consumptionByYear.forEach((year, consumption) ->
                        model.addRow(new Object[]{group, String.format("%1$.2f", consumption), year})));

        resultTable.setModel(model);
    }

    public static void main(String[] args) {
        Regions regions = new Regions();
        Map<String, List<Reactor>> reactors = new HashMap<>();
        ConsumptionCalculationsGUI dialog = new ConsumptionCalculationsGUI(regions, reactors);
        dialog.pack();
        dialog.setVisible(true);
    }
}
