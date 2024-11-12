package Database;

import Model.Booking;
import Model.Client;
import Model.Offering;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
	private final ClientDAO clientDAO;
	private final OfferingDAO offeringDAO;

	public BookingDAO(ClientDAO clientDAO, OfferingDAO offeringDAO) {
		this.clientDAO = clientDAO;
		this.offeringDAO = offeringDAO;
	}

	private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
		String bookingDate = rs.getString("booking_date");
		boolean status = rs.getBoolean("status");
		int clientId = rs.getInt("client_id");
		int offeringId = rs.getInt("offering_id");

		Client client = clientDAO.getClientById(clientId);
		Offering offering = offeringDAO.getOfferingById(offeringId);

		Booking booking = new Booking(bookingDate, status, client, offering);
		booking.setId(rs.getInt("id"));
		return booking;
	}

	public void addBooking(Booking booking) {
		String sql = "INSERT INTO Booking (booking_date, status, client_id, offering_id) VALUES (?, ?, ?, ?)";
		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, booking.getBookingDate());
			pstmt.setBoolean(2, booking.isStatus());
			pstmt.setInt(3, booking.getClient().getId());
			pstmt.setInt(4, booking.getOffering().getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error adding booking: " + e.getMessage());
		}
	}

	public List<Booking> getAllBookings() {
		List<Booking> bookings = new ArrayList<>();
		String sql = "SELECT * FROM Booking";

		try (Connection conn = DatabaseConnection.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Booking booking = mapResultSetToBooking(rs, true);
				bookings.add(booking);
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving bookings: " + e.getMessage());
		}
		return bookings;
	}

	// Method with loadClient flag to avoid circular loading
	private Booking mapResultSetToBooking(ResultSet rs, boolean loadClient) throws SQLException {
		String bookingDate = rs.getString("booking_date");
		boolean status = rs.getBoolean("status");
		int clientId = rs.getInt("client_id");
		int offeringId = rs.getInt("offering_id");

		Client client = loadClient ? clientDAO.getClientById(clientId, false) : null;
		Offering offering = offeringDAO.getOfferingById(offeringId);

		Booking booking = new Booking(bookingDate, status, client, offering);
		booking.setId(rs.getInt("id"));
		return booking;
	}

	public ArrayList<Booking> getBookingsByClientId(int clientId, boolean loadClient) {
		String sql = "SELECT * FROM Booking WHERE client_id = ?";
		ArrayList<Booking> bookings = new ArrayList<>();

		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, clientId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Booking booking = mapResultSetToBooking(rs, loadClient);
				bookings.add(booking);
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving bookings for client ID: " + e.getMessage());
		}
		return bookings;
	}

	public void deleteBooking(Booking booking) {
	    String sql = "DELETE FROM Booking WHERE id = ?";
	    try (Connection conn = DatabaseConnection.connect();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, booking.getId());
	        pstmt.executeUpdate();
	        System.out.println("Booking deleted successfully: " + booking.getId());
	    } catch (SQLException e) {
	        System.out.println("Error deleting booking: " + e.getMessage());
	    }
	}

	public ArrayList<Booking> getBookingsByClientId(int clientId) {
	    String sql = "SELECT * FROM Booking WHERE client_id = ?";
	    ArrayList<Booking> bookings = new ArrayList<>();

	    try (Connection conn = DatabaseConnection.connect();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, clientId);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Booking booking = mapResultSetToBooking(rs);
	            bookings.add(booking);
	        }
	    } catch (SQLException e) {
	        System.out.println("Error retrieving bookings for client ID: " + e.getMessage());
	    }
	    return bookings;
	}


	public boolean bookingExists(Offering offering, Client client) {
		String sql = "SELECT COUNT(*) FROM Booking WHERE client_id = ? AND offering_id = ?";
		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, client.getId());
			pstmt.setInt(2, offering.getId());
			ResultSet rs = pstmt.executeQuery();
			return rs.next() && rs.getInt(1) > 0;
		} catch (SQLException e) {
			System.out.println("Error checking booking existence: " + e.getMessage());
			return false;
		}
	}
	
}
