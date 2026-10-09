package se.iths.rami.webshop;

import javax.swing.*;
import java.awt.*;

public class testing {
    public static void main(String[] args) {

        JTextField nameField = new JTextField(15);
        JTextField ageField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Age:"));
        panel.add(ageField);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "User information",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {

            try {
                String name = nameField.getText();
                String ageInput = ageField.getText();

                if (name.isBlank()) {
                    throw new IllegalArgumentException("Name cannot be empty");
                }

                if (ageInput.isBlank()) {
                    throw new IllegalArgumentException("Age cannot be empty");
                }

                int age = Integer.parseInt(ageInput);

                if (age < 0) {
                    throw new IllegalArgumentException("Age cannot be negative");
                }

                JOptionPane.showMessageDialog(
                        null,
                        "Name: " + name + "\nAge: " + age
                );

            } catch (IllegalArgumentException e) {

                JOptionPane.showMessageDialog(
                        null,
                        e.getMessage(),
                        "Invalid input",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}