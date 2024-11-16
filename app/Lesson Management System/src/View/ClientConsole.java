package View;

import Controller.ClientController;
import Controller.BookingController;
import Controller.OfferingController;
import Model.Booking;
import Model.Client;
import Model.Offering;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ClientConsole extends JFrame implements Runnable {
	private Client loggedInClient;
	private OfferingController OC = OfferingController.getInstance();
	private BookingController BC = BookingController.getInstance();
	private ClientController CC = ClientController.getInstance();

	private JTextArea outputArea;
	private JButton viewOfferingsButton, bookOfferingButton, viewBookingsButton, cancelBookingButton, logoutButton,
			registerMinorButton;

	public ClientConsole() {
		setTitle("Client Console");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		outputArea = new JTextArea();
		outputArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(outputArea);
		add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout(1, 5));
		viewOfferingsButton = new JButton("View Offerings");
		bookOfferingButton = new JButton("Book Offering");
		viewBookingsButton = new JButton("View Bookings");
		cancelBookingButton = new JButton("Cancel Booking");
		registerMinorButton = new JButton("Register Minor");
		logoutButton = new JButton("Logout");

		buttonPanel.add(viewOfferingsButton);
		buttonPanel.add(bookOfferingButton);
		buttonPanel.add(viewBookingsButton);
		buttonPanel.add(cancelBookingButton);
		buttonPanel.add(registerMinorButton);
		buttonPanel.add(logoutButton);
		add(buttonPanel, BorderLayout.NORTH);

		viewOfferingsButton.addActionListener(e -> handleViewOfferings());
		bookOfferingButton.addActionListener(e -> handleBookOffering());
		viewBookingsButton.addActionListener(e -> handleViewBookings());
		cancelBookingButton.addActionListener(e -> handleCancelBooking());
		registerMinorButton.addActionListener(e -> handleMinorRegistration());
		logoutButton.addActionListener(e -> dispose());
	}

	@Override
	public void run() {
		setVisible(true);
		outputArea.append("==== Client Console Menu ====\n");

		if (loggedInClient.getAge() < 18) {
			registerMinorButton.setEnabled(false);
			outputArea.append("Only adult clients can register a minor.\n");
		}
	}

	public boolean logIn(String name, String phoneNumber) {
		setLoggedInClient(CC.login(name, phoneNumber));
		return isLoggedIn();
	}

	private void handleViewOfferings() {
		ArrayList<Offering> publicOfferings = OC.viewPublicOfferings();
		ArrayList<Booking> clientBookings = BC.getBookingsByClient(loggedInClient); // Fetch client’s bookings

		// Annotate offerings based on client’s existing bookings
		String[] annotatedOfferings = annotateOfferingsList(publicOfferings, clientBookings);

		outputArea.append("\nAvailable Offerings:\n");
		for (String offering : annotatedOfferings) {
			outputArea.append(offering + "\n");
		}
	}

	private String[] annotateOfferingsList(ArrayList<Offering> publicOfferings, ArrayList<Booking> clientBookings) {
		String[] annotatedOfferingList = new String[publicOfferings.size()];

		for (int i = 0; i < publicOfferings.size(); i++) {
			Offering offering = publicOfferings.get(i);

			// Check if the client has already booked this offering
			boolean isBooked = clientBookings.stream()
					.anyMatch(booking -> booking.getOffering().getId() == offering.getId());

			if (isBooked) {
				annotatedOfferingList[i] = offering.toString() + " [unavailable]";
			} else {
				annotatedOfferingList[i] = offering.toString();
			}
		}
		return annotatedOfferingList;
	}

	private void handleBookOffering() {
		if (loggedInClient.getAge() < 18) {
			outputArea.append("Minors require a guardian to make bookings.\n");
			Client guardian = promptGuardianLogin(loggedInClient.getGuardian());
			if (guardian == null) {
				outputArea.append("Guardian authorization failed. Booking not permitted.\n");
				return;
			}
		}

		ArrayList<Offering> publicOfferings = OC.viewPublicOfferings();
		if (publicOfferings.isEmpty()) {
			outputArea.append("No available offerings to book.\n");
			return;
		}

		// Create a dropdown (JComboBox) for the offerings
		JComboBox<Offering> offeringDropdown = new JComboBox<>(publicOfferings.toArray(new Offering[0]));
		offeringDropdown.setRenderer(new OfferingListCellRenderer());

		int result = JOptionPane.showConfirmDialog(this, offeringDropdown, "Select an Offering to Book",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			Offering selectedOffering = (Offering) offeringDropdown.getSelectedItem();
			if (selectedOffering != null) {
				bookOffering(selectedOffering);
			} else {
				outputArea.append("No offering selected.\n");
			}
		}
	}

	private Client promptGuardianLogin(Client registeredGuardian) {
		String guardianName = JOptionPane.showInputDialog("Enter guardian name:");
		String guardianPhone = JOptionPane.showInputDialog("Enter guardian phone number (###-###-####):");

		// Attempt to log in as the guardian
		Client guardian = CC.login(guardianName, guardianPhone);

		// Check if the logged-in guardian matches the registered guardian
		if (guardian != null && guardian.equals(registeredGuardian)) {
			outputArea.append("Guardian login successful. Proceed with booking.\n");
			return guardian;
		} else {
			outputArea.append("Guardian login failed.\n");
			return null;
		}
	}

	// Custom renderer to display offering details in the dropdown
	private static class OfferingListCellRenderer extends JLabel implements ListCellRenderer<Offering> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Offering> list, Offering offering, int index,
				boolean isSelected, boolean cellHasFocus) {
			setText(offering.toString()); // Customize as needed for displaying offering details
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

	private void bookOffering(Offering offering) {
	    // Check if the client has already booked the offering or if it's unavailable
	    if (BC.checkBookingAvailability(offering, loggedInClient)) {
	        outputArea.append("Already booked this offering or it's unavailable.\n");
	        return;
	    }

	    // Check if the offering is full
	    if (OC.isFull(offering)) {
	        outputArea.append("Offering is full.\n");
	        return;
	    }

	    // Check for conflicting bookings on the same day and time slot
	    int dayOfWeek = offering.getSchedule().getDayOfWeek();
	    boolean hasConflict = BC.hasConflictingBooking(loggedInClient, offering.getStartTime(), offering.getEndTime(), dayOfWeek);

	    if (hasConflict) {
	        outputArea.append("Booking error: The client already has a booking at the same time on this day of the week.\n");
	        return;
	    }

	    // Proceed to create the booking if all checks pass
	    OC.decrementCapacity(offering);
	    BC.createBooking(offering, loggedInClient);
	    outputArea.append("Booked successfully.\n");
	}


	private void handleViewBookings() {
		ArrayList<Booking> bookings = BC.getBookingsByClient(loggedInClient);
		if (bookings.isEmpty()) {
			outputArea.append("No bookings to show.\n");
			return;
		}

		outputArea.append("\nYour Bookings:\n");
		for (int i = 0; i < bookings.size(); i++) {
			outputArea.append((i + 1) + ". " + bookings.get(i).toString() + "\n");
		}
	}

	private void handleCancelBooking() {
		ArrayList<Booking> bookings = BC.getBookingsByClient(loggedInClient);
		if (bookings.isEmpty()) {
			outputArea.append("No bookings to cancel.\n");
			return;
		}

		// Create a dropdown (JComboBox) for the bookings
		JComboBox<Booking> bookingDropdown = new JComboBox<>(bookings.toArray(new Booking[0]));
		bookingDropdown.setRenderer(new BookingListCellRenderer());

		int result = JOptionPane.showConfirmDialog(this, bookingDropdown, "Select a Booking to Cancel",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			Booking selectedBooking = (Booking) bookingDropdown.getSelectedItem();
			if (selectedBooking != null) {
				BC.cancelBooking(selectedBooking); // Cancel the booking
				OC.incrementCapacity(selectedBooking.getOffering()); // Update capacity
				outputArea.append("Booking canceled successfully.\n");

				// Refresh view to update bookings list
				handleViewBookings();
			} else {
				outputArea.append("No booking selected.\n");
			}
		}
	}

	// Custom renderer to display booking details in the dropdown
	private static class BookingListCellRenderer extends JLabel implements ListCellRenderer<Booking> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Booking> list, Booking booking, int index,
				boolean isSelected, boolean cellHasFocus) {
			setText(booking.toString()); // Customize as needed for displaying booking details
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

	private void setLoggedInClient(Client loggedInClient) {
		this.loggedInClient = loggedInClient;
	}

	private boolean isLoggedIn() {
		return loggedInClient != null;
	}

	private void handleMinorRegistration() {
		if (loggedInClient.getAge() < 18) {
			outputArea.append("Only adult clients can register a minor.\n");
			return;
		}

		String name = JOptionPane.showInputDialog(this, "Enter minor's name:");
		String phoneNumber = JOptionPane.showInputDialog(this, "Enter minor's phone number:");
		String ageStr = JOptionPane.showInputDialog(this, "Enter minor's age:");
		int age = Integer.parseInt(ageStr);

		if (registerMinor(name, phoneNumber, age, loggedInClient)) {
			outputArea.append("Minor registered successfully under guardian: " + loggedInClient.getName() + "\n");
		}
	}

	public boolean registerMinor(String name, String phoneNumber, int age, Client guardian) {
		if (CC.register(name, phoneNumber, age, guardian)) {
			outputArea.append("Minor registered successfully under guardian " + guardian.getName() + ".\n");
			return true;
		} else {
			outputArea.append("Registration failed: Minor account already exists.\n");
			return false;
		}
	}

	public boolean registerNormal(String name, String phoneNumber, int age) {
		if (age < 18) {
			outputArea.append("Only Adults can register Normally.\n");
			return false;
		}
		if (CC.register(name, phoneNumber, age)) {
			outputArea.append("Registered Successfully");
			return true;
		} else {
			outputArea.append("Registration failed:  account already exists.\n");
			return false;
		}
		// TODO Auto-generated method stub
	}
}
