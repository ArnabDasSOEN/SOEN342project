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
		String[] annotatedOfferings = annotateOfferingsList(publicOfferings,
				CC.checkBookingAvailability(publicOfferings, loggedInClient));

		outputArea.append("\nAvailable Offerings:\n");
		for (String offering : annotatedOfferings) {
			outputArea.append(offering + "\n");
		}
	}

	private void handleBookOffering() {
	    if (loggedInClient.getAge() < 18) {
	        outputArea.append("Minors require a guardian to make bookings.\n");
	        return;
	    }

	    // Retrieve the list of public offerings
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
		if (BC.checkBookingAvailability(offering, loggedInClient)) {
			outputArea.append("Already booked this offering or it's unavailable.\n");
		} else if (OC.isFull(offering)) {
			outputArea.append("Offering is full.\n");
		} else {
			OC.decrementCapacity(offering);
			BC.createBooking(offering, loggedInClient);
			outputArea.append("Booked successfully.\n");
		}
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

		String indexStr = JOptionPane.showInputDialog(this, "Enter the booking number to cancel:");
		if (indexStr == null)
			return;

		try {
			int index = Integer.parseInt(indexStr) - 1;
			if (index < 0 || index >= bookings.size()) {
				JOptionPane.showMessageDialog(this, "Invalid booking index.");
				return;
			}

			Booking selectedBooking = bookings.get(index);
			BC.cancelBooking(selectedBooking);
			OC.incrementCapacity(selectedBooking.getOffering());
			outputArea.append("Booking canceled successfully.\n");
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Please enter a valid number.");
		}
	}

	private String[] annotateOfferingsList(ArrayList<Offering> publicOfferings, int[] indexOfbookedOffers) {
		String[] annotatedOfferingList = new String[publicOfferings.size()];
		int bookingsIteration = 0;

		for (int i = 0; i < publicOfferings.size(); i++) {
			Offering offering = publicOfferings.get(i);
			if (offering.getCapacity() == 0 || (bookingsIteration < indexOfbookedOffers.length
					&& indexOfbookedOffers[bookingsIteration] == i)) {
				annotatedOfferingList[i] = offering.toString() + " [unavailable]";
				bookingsIteration++;
			} else {
				annotatedOfferingList[i] = offering.toString();
			}
		}
		return annotatedOfferingList;
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
		if (age >= 18) {
			outputArea.append("Only minors can be registered through a guardian.\n");
			return false;
		}
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
			outputArea.append("Registration failed: account already exists.\n");
			return false;
		}
		// TODO Auto-generated method stub
	}
}
