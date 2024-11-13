package View;

import Model.Offering;
import Model.Instructor;
import Controller.OfferingController;
import Controller.InstructorController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class InstructorConsole extends JFrame implements Runnable {
	private Instructor loggedIn;
	private OfferingController OC = OfferingController.getInstance();
	private InstructorController IC = InstructorController.getInstance();

	private JTextArea outputArea;
	private JButton viewOfferingsButton, takeOfferingButton, logoutButton;

	public InstructorConsole() {
		setTitle("Instructor Console");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		// Initialize the output area
		outputArea = new JTextArea();
		outputArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(outputArea);
		add(scrollPane, BorderLayout.CENTER);

		// Initialize buttons and layout
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 3));

		viewOfferingsButton = new JButton("View Potential Offerings");
		takeOfferingButton = new JButton("Take Offering");
		logoutButton = new JButton("Logout");

		buttonPanel.add(viewOfferingsButton);
		buttonPanel.add(takeOfferingButton);
		buttonPanel.add(logoutButton);
		add(buttonPanel, BorderLayout.NORTH);

		// Button actions
		viewOfferingsButton.addActionListener(e -> handleViewPotentialOfferings());
		takeOfferingButton.addActionListener(e -> handleTakeOffering());
		logoutButton.addActionListener(e -> dispose());
	}

	@Override
	public void run() {
		setVisible(true);
		outputArea.append("==== Instructor Console Menu ====\n");
	}

	// Log in method
	public boolean logIn(String name, String phoneNumber) {
		setLoggedInInstructor(IC.login(name, phoneNumber));
		if (isLoggedIn()) {
			outputArea.append("Login successful. Welcome, " + loggedIn.getName() + "!\n");
			viewOfferingsButton.setEnabled(true);
			takeOfferingButton.setEnabled(true);
			return true;
		} else {
			outputArea.append("Login failed. Incorrect name or phone number.\n");
			return false;
		}
	}

	private void setLoggedInInstructor(Instructor instructor) {
		this.loggedIn = instructor;
	}

	private boolean isLoggedIn() {
		return loggedIn != null;
	}

	// Display potential offerings that the instructor can take
	private void handleViewPotentialOfferings() {
		ArrayList<Offering> potentialOfferings = viewPotentialOfferings();
		if (potentialOfferings == null) {
			outputArea.append("Please log in first to view potential offerings.\n");
			return;
		}

		outputArea.append("\nPotential Offerings:\n");
		for (int i = 0; i < potentialOfferings.size(); i++) {
			outputArea.append((i + 1) + ". " + potentialOfferings.get(i) + "\n");
		}
	}

	// Allow the instructor to select and take an offering
	private void handleTakeOffering() {
		ArrayList<Offering> potentialOfferings = viewPotentialOfferings();
		if (potentialOfferings == null || potentialOfferings.isEmpty()) {
			outputArea.append("No potential offerings to take.\n");
			return;
		}

		// Create a dropdown (JComboBox) for the offerings
		JComboBox<Offering> offeringDropdown = new JComboBox<>(potentialOfferings.toArray(new Offering[0]));
		offeringDropdown.setRenderer(new OfferingListCellRenderer());

		int result = JOptionPane.showConfirmDialog(this, offeringDropdown, "Select an Offering to Take",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			Offering selectedOffering = (Offering) offeringDropdown.getSelectedItem();
			if (selectedOffering != null) {
				takeOffering(selectedOffering);
			} else {
				outputArea.append("No offering selected.\n");
			}
		}
	}

	// Renderer for displaying offerings in the dropdown list
	private static class OfferingListCellRenderer extends JLabel implements ListCellRenderer<Offering> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Offering> list, Offering offering, int index,
				boolean isSelected, boolean cellHasFocus) {
			setText(offering.toString());
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setOpaque(true);
			return this;
		}
	}

	// Retrieve potential offerings based on instructor's availability and
	// specialization
	public ArrayList<Offering> viewPotentialOfferings() {
		Instructor instructor = IC.getInstructor();
		if (instructor == null) {
			return null; // Return null if no instructor is logged in
		}
		return OC.findPotentialOfferings(instructor.getAvailabilities(), instructor.getSpecialization());
	}

	// Assign the selected offering to the instructor
	public void takeOffering(Offering offering) {
		Instructor instructor = IC.getInstructor();
		if (OC.available(offering) && instructor != null) { // Check if available and instructor is logged in
			OC.assignInstructorToOffering(offering, instructor); // Assign instructor to offering
			offering.setInstructor(instructor); // Set the instructor in memory
			instructor.assignOffering(offering); // Add offering to instructor’s list
			outputArea.append("Offering successfully assigned to you.\n");
		} else {
			outputArea.append("The offering is not available or you are not logged in.\n");
		}
	}

	// Method to register an instructor by collecting necessary details
	public boolean registerInstructor(String name, String phoneNumber, String specialization, String startDate,
			String endDate, String citiesInput) {
		// Parse the cities input into an ArrayList
		ArrayList<String> availabilities = new ArrayList<>(Arrays.asList(citiesInput.split(",")));
		availabilities.replaceAll(String::trim); // Trim whitespace around city names

		// Register the instructor through InstructorController
		boolean success = IC.register(name, phoneNumber, startDate, endDate, specialization, availabilities);
		return success;
	}
}
