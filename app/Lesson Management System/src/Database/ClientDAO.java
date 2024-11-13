package Database;

import Model.Client;
import Model.Booking;
import java.sql.*;
import java.util.ArrayList;

public class ClientDAO {
	private BookingDAO bookingDAO;

	public ClientDAO() {
		// Leave bookingDAO uninitialized in the default constructor
	}

	public ClientDAO(BookingDAO bookingDAO) {
		this.bookingDAO = bookingDAO;
	}

	private BookingDAO getBookingDAO() {
		if (this.bookingDAO == null) {
			this.bookingDAO = new BookingDAO(this, new OfferingDAO());
		}
		return this.bookingDAO;
	}

	public ArrayList<Client> getAllClients() {
		ArrayList<Client> clients = new ArrayList<>();
		String sql = "SELECT * FROM Client";

		try (Connection conn = DatabaseConnection.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Client client = extractClientFromResultSet(rs);
				client.setBookings(getBookingDAO().getBookingsByClientId(client.getId()));
				clients.add(client);
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving clients: " + e.getMessage());
		}
		return clients;
	}

	private Client extractClientFromResultSet(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String name = rs.getString("name");
		String phoneNumber = rs.getString("phone_number");
		int age = rs.getInt("age");
		int guardianId = rs.getInt("guardian_id");

		Client client = new Client(name, phoneNumber, age);
		client.setId(id);

		if (guardianId > 0) {
			Client guardian = getClientById(guardianId);
			client.setGuardian(guardian);
		}

		return client;
	}

	public void deleteClient(Client client) {
		String sql = "DELETE FROM Client WHERE id = ?";

		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, client.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error deleting client: " + e.getMessage());
		}
	}

	public void addClientWithGuardian(Client minorClient, Client guardian) {
		String sql = "INSERT INTO Client (name, phone_number, age, guardian_id) VALUES (?, ?, ?, ?)";
		try (Connection conn = DatabaseConnection.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, minorClient.getName());
			pstmt.setString(2, minorClient.getPhoneNumber());
			pstmt.setInt(3, minorClient.getAge());
			pstmt.setInt(4, guardian.getId());

			int affectedRows = pstmt.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Inserting minor client failed, no rows affected.");
			}

			// Retrieve and set generated ID
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int generatedId = generatedKeys.getInt(1);
					minorClient.setId(generatedId); // Set the generated ID on the minor client object
					System.out.println("Minor client registered with ID: " + generatedId);
				} else {
					throw new SQLException("Inserting minor client failed, no ID obtained.");
				}
			}

		} catch (SQLException e) {
			System.out.println("Error adding minor client with guardian: " + e.getMessage());
		}
	}

	// Check if a client exists by name and phone number
	public boolean clientExists(String name, String phoneNumber) {
		String sql = "SELECT COUNT(*) FROM Client WHERE name = ? AND phone_number = ?";
		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, name);
			pstmt.setString(2, phoneNumber);
			ResultSet rs = pstmt.executeQuery();
			return rs.next() && rs.getInt(1) > 0;
		} catch (SQLException e) {
			System.out.println("Error checking client existence: " + e.getMessage());
			return false;
		}
	}

	public Client getClientById(int id, boolean loadBookings) {
		String sql = "SELECT * FROM Client WHERE id = ?";
		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				Client client = extractClientFromResultSet(rs);
				// Load bookings only if the flag is true
				if (loadBookings) {
					client.setBookings(getBookingDAO().getBookingsByClientId(id, false)); // prevent recursive loading
				}
				return client;
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving client by ID: " + e.getMessage());
		}
		return null;
	}

	public Client getClientById(int id) {
		return getClientById(id, true); // Default behavior loads bookings
	}

	public Client getClientByNameAndPhone(String name, String phoneNumber) {
		String sql = "SELECT * FROM Client WHERE name = ? AND phone_number = ?";
		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, name);
			pstmt.setString(2, phoneNumber);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				Client client = extractClientFromResultSet(rs);
				client.setBookings(getBookingDAO().getBookingsByClientId(client.getId(), false));
				return client;
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving client by name and phone: " + e.getMessage());
		}
		return null;
	}

	// Add a new client without a guardian
	public void addClient(Client newClient) {
		String sql = "INSERT INTO Client (name, phone_number, age) VALUES (?, ?, ?)";

		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, newClient.getName());
			pstmt.setString(2, newClient.getPhoneNumber());
			pstmt.setInt(3, newClient.getAge());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error adding client: " + e.getMessage());
		}
	}

	public boolean clientExistsById(int clientId) {
		String sql = "SELECT COUNT(*) FROM Client WHERE id = ?";
		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, clientId);
			ResultSet rs = pstmt.executeQuery();
			return rs.next() && rs.getInt(1) > 0;
		} catch (SQLException e) {
			System.out.println("Error checking client existence by ID: " + e.getMessage());
			return false;
		}
	}

}
