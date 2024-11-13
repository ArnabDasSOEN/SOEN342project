package Controller;

import Database.BookingDAO;
import Database.ClientDAO;
import Database.OfferingDAO;
import Model.Booking;
import Model.Client;
import Model.Offering;
import Model.Schedule;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BookingController {
	private static BookingController BBinstance;
	private ArrayList<Booking> bookingCollection;
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private BookingDAO bookingDAO;
	private OfferingDAO offeringDAO;

	private BookingController() {
		bookingCollection = new ArrayList<>();
		bookingDAO = new BookingDAO(new ClientDAO(bookingDAO), new OfferingDAO());
		offeringDAO = new OfferingDAO(); // Initialize offeringDAO
		loadBookingsFromDatabase();
	}

	public static BookingController getInstance() {
		if (BBinstance == null) {
			BBinstance = new BookingController();
		}
		return BBinstance;
	}

	private void loadBookingsFromDatabase() {
		lock.writeLock().lock();
		try {
			bookingCollection = new ArrayList<>(bookingDAO.getAllBookings());
		} finally {
			lock.writeLock().unlock();
		}
	}

	public ArrayList<Booking> getBookingCollection() {
		lock.readLock().lock();
		try {
			return new ArrayList<>(bookingCollection);
		} finally {
			lock.readLock().unlock();
		}
	}

	public boolean checkBookingAvailability(Offering offering, Client client) {
		lock.readLock().lock();
		try {
			// Check if booking exists in the in-memory collection
			for (Booking booking : bookingCollection) {
				if (booking.exists(offering, client)) {
					return true;
				}
			}
			// Check in database if not found locally
			return bookingDAO.bookingExists(offering, client);
		} finally {
			lock.readLock().unlock();
		}
	}

	public void createBooking(Offering offering, Client loggedInClient, Client guardian) {
		lock.writeLock().lock();
		try {
			if (!validateBookingForClient(offering, loggedInClient, guardian)) {
				return;
			}

			// Proceed with booking creation
			Booking booking = new Booking(getBookingDateString(offering), true, loggedInClient, offering);

			// Log before adding to database
			System.out.println("Attempting to add booking to database...");

			// Add booking to in-memory collection and database
			bookingCollection.add(booking);
			bookingDAO.addBooking(booking);

			// Confirm database addition
			System.out.println("Booking successfully added to database for client: " + loggedInClient.getName());

			Booking confirmedBooking = bookingDAO.getBookingById(booking.getId());
			if (confirmedBooking != null) {
				System.out.println("Booking confirmed in database for client: " + loggedInClient.getName());
			} else {
				System.out.println("Booking failed to persist in database.");
			}
			// Decrease offering capacity and update database
			offering.spotFilled();
			offeringDAO.updateOfferingCapacity(offering.getId(), offering.getCapacity());

			loggedInClient.getBookings().add(booking);
		} catch (Exception e) {
			e.printStackTrace(); // Log any exceptions
			System.out.println("Failed to create booking for client: " + loggedInClient.getName());
		} finally {
			lock.writeLock().unlock();
		}
	}

	// Overloaded method to create booking for adult clients
	public void createBooking(Offering offering, Client loggedInClient) {
		createBooking(offering, loggedInClient, null); // Guardian is null for adult clients
	}

	// Helper method to validate booking conditions for both minors and adults
	private boolean validateBookingForClient(Offering offering, Client client, Client guardian) {

		if (checkBookingAvailability(offering, client)) {
			System.out.println("Booking error: The client has already booked this offering or it is unavailable.");
			return false;
		}
		if (offering.getCapacity() <= 0) {
			System.out.println("Booking error: The offering is fully booked.");
			return false;
		}
		return true;
	}

	// Helper method to generate a booking date string
	private String getBookingDateString(Offering offering) {
		Schedule schedule = offering.getSchedule();
		return "From: " + offering.getStartTime() + " to " + offering.getEndTime() + " on " + schedule.getStartDate();
	}

	public void cancelBooking(Booking selectedBooking) {
		lock.writeLock().lock(); // Acquire write lock
		try {
			// Remove booking from in-memory collection in both controller and client
			bookingCollection.remove(selectedBooking);

			// Ensure it's also removed from the client's in-memory bookings list
			selectedBooking.getClient().getBookings().remove(selectedBooking);

			// Delete booking from database
			bookingDAO.deleteBooking(selectedBooking);

			// Increment the capacity of the associated offering
			OfferingController.getInstance().incrementCapacity(selectedBooking.getOffering());

			System.out.println("Booking canceled successfully for client: " + selectedBooking.getClient().getName());
		} finally {
			lock.writeLock().unlock(); // Release write lock
		}
	}

	public ArrayList<Booking> getBookingsByClient(Client loggedInClient) {
		lock.readLock().lock();
		try {
			// If client’s booking list is already populated, use it directly
			if (!loggedInClient.getBookings().isEmpty()) {
				return new ArrayList<>(loggedInClient.getBookings());
			}

			// Otherwise, retrieve bookings from the database and add to in-memory lists
			ArrayList<Booking> clientBookings = bookingDAO.getBookingsByClientId(loggedInClient.getId());
			loggedInClient.getBookings().addAll(clientBookings); // Sync with in-memory client bookings
			bookingCollection.addAll(clientBookings); // Sync with controller's booking collection

			return clientBookings;
		} finally {
			lock.readLock().unlock();
		}
	}

}
