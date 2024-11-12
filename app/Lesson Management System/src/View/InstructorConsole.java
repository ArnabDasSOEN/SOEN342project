package View;

import Model.Offering;
import Model.Instructor;
import Controller.OfferingController;
import Controller.InstructorController;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

public class InstructorConsole extends JFrame implements Runnable {
    private OfferingController OC = OfferingController.getInstance();
    private InstructorController IC = InstructorController.getInstance();

    private JTextArea outputArea;
    private JButton viewOfferingsButton, takeOfferingButton, logoutButton;

    public InstructorConsole() {
        setTitle("Instructor Console");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        viewOfferingsButton = new JButton("View Potential Offerings");
        takeOfferingButton = new JButton("Take Offering");
        logoutButton = new JButton("Logout");

        buttonPanel.add(viewOfferingsButton);
        buttonPanel.add(takeOfferingButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.NORTH);

        viewOfferingsButton.addActionListener(e -> handleViewPotentialOfferings());
        takeOfferingButton.addActionListener(e -> handleTakeOffering());
        logoutButton.addActionListener(e -> dispose());
    }

    @Override
    public void run() {
        setVisible(true);
        outputArea.append("==== Instructor Console Menu ====\n");
    }

    private void handleViewPotentialOfferings() {
        ArrayList<Offering> potentialOfferings = viewPotentialOfferings();

        outputArea.append("\nPotential Offerings:\n");
        for (int i = 0; i < potentialOfferings.size(); i++) {
            outputArea.append((i + 1) + ". " + potentialOfferings.get(i) + "\n");
        }
    }

    private void handleTakeOffering() {
        String indexStr = JOptionPane.showInputDialog(this, "Enter the index of the offering you wish to take:");
        if (indexStr == null) return; // User cancelled
        try {
            int index = Integer.parseInt(indexStr) - 1;
            ArrayList<Offering> potentialOfferings = viewPotentialOfferings();

            if (index < 0 || index >= potentialOfferings.size()) {
                JOptionPane.showMessageDialog(this, "Invalid offering index.");
                return;
            }

            Offering selectedOffering = potentialOfferings.get(index);
            takeOffering(selectedOffering);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }

    public ArrayList<Offering> viewPotentialOfferings() {
        Instructor instructor = IC.getInstructor();
        return OC.findPotentialOfferings(instructor.getAvailabilities(), instructor.getSpecialization());
    }

    public void takeOffering(Offering offering) {
        if (OC.available(offering)) {
            offering.setInstructor(IC.getInstructor());
            IC.getInstructor().assignOffering(offering);
            outputArea.append("Offering successfully assigned to you.\n");
        } else {
            outputArea.append("The offering is not available.\n");
        }
    }
}
