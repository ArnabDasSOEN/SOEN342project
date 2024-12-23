package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Model.LessonType;
import Model.Location;
import Model.Offering;
import Model.Schedule;
import Model.Instructor;

public class OfferingDAO {

	public void addOffering(Offering offering) {
		String sql = "INSERT INTO Offering (lesson_type, is_group, availability, capacity, start_time, end_time, schedule_id, instructor_id, location_id, has_instructor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DatabaseConnection.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			// Lesson type should never be null as it's an enum, but ensure it is set
			if (offering.getLessonType() == null) {
				throw new SQLException("Error: LessonType is null for offering.");
			}
			pstmt.setString(1, offering.getLessonType().toString());

			pstmt.setBoolean(2, offering.isGroup());
			pstmt.setBoolean(3, offering.isAvailability());
			pstmt.setInt(4, offering.getCapacity());
			pstmt.setInt(5, offering.getStartTime());
			pstmt.setInt(6, offering.getEndTime());

			// Schedule ID
			if (offering.getSchedule() != null) {
				pstmt.setInt(7, offering.getSchedule().getId());
			} else {
				throw new SQLException("Error: Schedule is null for offering.");
			}

			// Instructor ID and has_instructor flag
			if (offering.getInstructor() != null) {
				pstmt.setInt(8, offering.getInstructor().getId());
				pstmt.setInt(10, 1); // Set has_instructor to 1 if there’s an instructor
			} else {
				pstmt.setNull(8, java.sql.Types.INTEGER); // No instructor
				pstmt.setInt(10, 0); // Explicitly set has_instructor to 0
			}

			// Location ID
			if (offering.getLocation() != null) {
				pstmt.setInt(9, offering.getLocation().getId());
			} else {
				throw new SQLException("Error: Location is null for offering.");
			}

			pstmt.executeUpdate();

			// Retrieve generated ID for the new offering
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					offering.setId(generatedKeys.getInt(1));
				}
			}
		} catch (SQLException e) {
			System.out.println("Error adding offering: " + e.getMessage());
		}
	}

	public List<Offering> getAllOfferings() {
		List<Offering> offerings = new ArrayList<>();
		String sql = "SELECT * FROM Offering";

		try (Connection conn = DatabaseConnection.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Offering offering = mapResultSetToOffering(rs);
				offering.setId(rs.getInt("id"));
				offerings.add(offering);
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving offerings: " + e.getMessage());
		}
		return offerings;
	}

	private Offering mapResultSetToOffering(ResultSet rs) throws SQLException {
		// Corrected column names
		LessonType lessonType = LessonType.valueOf(rs.getString("lesson_type"));
		boolean isGroup = rs.getBoolean("is_group");
		boolean availability = rs.getBoolean("availability");
		int capacity = rs.getInt("capacity");
		int startTime = rs.getInt("start_time");
		int endTime = rs.getInt("end_time");

		Location location = new LocationDAO().getLocationById(rs.getInt("location_id"));
		Schedule schedule = new ScheduleDAO().getScheduleById(rs.getInt("schedule_id"));
		Instructor instructor = rs.getInt("instructor_id") != 0
				? new InstructorDAO().getInstructorById(rs.getInt("instructor_id"))
				: null;

		Offering offering = new Offering(lessonType, isGroup, capacity, startTime, endTime, schedule, location);
		offering.setAvailability(availability);
		offering.setInstructor(instructor);
		return offering;
	}

	public Offering getOfferingById(int id) {
		String sql = "SELECT * FROM Offering WHERE id = ?";
		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return mapResultSetToOffering(rs);
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving offering by ID: " + e.getMessage());
		}
		return null;
	}

	public void updateOfferingCapacity(int id, int capacity) {
		String sql = "UPDATE Offering SET capacity = ? WHERE id = ?";

		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, capacity);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error updating offering capacity: " + e.getMessage());
		}
	}

	public ArrayList<Offering> getAllUnassignedOfferings() {
		ArrayList<Offering> offerings = new ArrayList<>();
		String sql = "SELECT * FROM Offering WHERE instructor_id IS NULL"; // Only select offerings without an
																			// instructor

		try (Connection conn = DatabaseConnection.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Offering offering = mapResultSetToOffering(rs);
				offering.setId(rs.getInt("id"));
				offerings.add(offering);
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving unassigned offerings: " + e.getMessage());
		}
		return offerings;
	}

	public void assignInstructorToOffering(int offeringId, int instructorId) {
	    String sql = "UPDATE Offering SET instructor_id = ?, availability = ?, has_instructor = ? WHERE id = ?";
	    try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, instructorId); // Set instructor_id
	        pstmt.setBoolean(2, true); // Set availability to true (or adjust based on your logic)
	        pstmt.setBoolean(3, true); // Set has_instructor to true
	        pstmt.setInt(4, offeringId); // The offering to update
	        pstmt.executeUpdate(); // Execute the update
	    } catch (SQLException e) {
	        System.out.println("Error updating offering with instructor: " + e.getMessage());
	    }
	}

	public boolean offeringExistsById(int offeringId) {
		String sql = "SELECT COUNT(*) FROM Offering WHERE id = ?";
		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, offeringId);
			ResultSet rs = pstmt.executeQuery();
			return rs.next() && rs.getInt(1) > 0;
		} catch (SQLException e) {
			System.out.println("Error checking offering existence by ID: " + e.getMessage());
			return false;
		}
	}

}
