package se.iths.rami.webshop;

import javax.swing.*;
import java.awt.*;

public class JOptionPaneOutInputHandler implements OutInputHandler {
    @Override
    public void theme() {
        Color background = new Color(235, 235, 235);
        Color textColor = new Color(35, 45, 60);

        UIManager.put("OptionPane.background", background);
        UIManager.put("Panel.background", background);
        UIManager.put("OptionPane.messageForeground", textColor);
        UIManager.put("Label.foreground", textColor);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", textColor);

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            errorInfo(e.getMessage());
        }
    }

    @Override
    public String menu() {
        return JOptionPane.showInputDialog(null,
                """
                        1. Add product
                        2. Show all products
                        3. Search for a product
                        4. Exit
                        """, "Webshop menu", JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void info(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void errorInfo(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public String[] prompt(String title, String[] label, String[] values) {

        if (label.length != values.length) {
            throw new IllegalArgumentException("Label and values length must be same length");
        }


        JPanel panel = createPanel(label.length, 2);
        JTextField[] fields = new JTextField[label.length];

        for (int i = 0; i < label.length; i++) {
            fields[i] = new JTextField(values[i], 10);

            panel.add(new JLabel(label[i]));
            panel.add(fields[i]);
        }
        int panelWindow = confirmDialog(panel, title);

        if (panelWindow != JOptionPane.OK_OPTION) {
            return null;
        }

        String[] result = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            result[i] = fields[i].getText().trim();
        }

        return result;
    }

    public void showTable(String title, String[] columns, Object[][] data) {
        JTable table = new JTable(data, columns);
        table.setCellSelectionEnabled(true);
        table.setAutoCreateRowSorter(true);
        table.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 200));

        JOptionPane.showMessageDialog(null, scrollPane, title,
                JOptionPane.PLAIN_MESSAGE);
    }

    private JPanel createPanel(int rows, int cols) {
        JPanel panel = new JPanel(new GridLayout(rows, cols, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        return panel;
    }

    private int confirmDialog(JPanel panel, String title) {
        return JOptionPane.showConfirmDialog(null, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

}
