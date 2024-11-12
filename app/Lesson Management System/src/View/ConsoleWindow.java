package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConsoleWindow extends JFrame {
    private JTextArea outputArea;
    private JTextField inputField;
    private JButton submitButton;

    public ConsoleWindow(String title) {
        setTitle(title);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Input Field and Submit Button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        submitButton = new JButton("Submit");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Event for Submit Button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText();
                inputField.setText("");
                onInput(input); // Trigger method to process input
            }
        });
    }

    // Display message in the output area
    public void displayMessage(String message) {
        outputArea.append(message + "\n");
    }

    // Abstract method to be implemented in subclasses
    protected void onInput(String input) {
        // Implemented by subclasses
    }
}
