package Database;

import java.sql.*;
import java.util.ArrayList;
import Model.Instructor;
import Model.LessonType;
import Model.Offering;
import Model.Schedule;
import Model.Location;

public class InstructorDAO {
	private final ScheduleDAO scheduleDAO = new ScheduleDAO();
	private final LocationDAO locationDAO = new LocationDAO();

	public void addInstructor(Instructor instructor) {
		String sql = "INSERT INTO Instructor (name, phone_number, start_date, end_date, specialization) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DatabaseConnection.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, instructor.getName());
			pstmt.setString(2, instructor.getPhoneNumber());
			pstmt.setString(3, instructor.getStartDate());
			pstmt.setString(4, instructor.getEndDate());
			pstmt.setString(5, instructor.getSpecialization().name());
			pstmt.executeUpdate();

			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				int instructorId = generatedKeys.getInt(1);
				instructor.setId(instructorId);
				addCities(instructorId, instructor.getAvailabilities());
			}
		} catch (SQLException e) {
			System.out.println("Error adding instructor: " + e.getMessage());
		}
	}

	private void addCities(int instructorId, ArrayList<String> cities) {
		String sql = "INSERT INTO InstructorCities (instructor_id, city) VALUES (?, ?)";

		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			for (String city : cities) {
				pstmt.setInt(1, instructorId);
				pstmt.setString(2, city);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		} catch (SQLException e) {
			System.out.println("Error adding instructor cities: " + e.getMessage());
		}
	}

	public boolean instructorExists(String name, String phoneNumber) {
		String sql = "SELECT COUNT(*) FROM Instructor WHERE name = ? AND phone_number = ?";
		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, name);
			pstmt.setString(2, phoneNumber);
			ResultSet rs = pstmt.executeQuery();
			return rs.next() && rs.getInt(1) > 0;
		} catch (SQLException e) {
			System.out.println("Error checking instructor existence: " + e.getMessage());
			return false;
		}
	}

	public ArrayList<Instructor> getAllInstructors() {
		ArrayList<Instructor> instructors = new ArrayList<>();
		String sql = "SELECT * FROM Instructor";

		try (Connection conn = DatabaseConnection.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Instructor instructor = new Instructor(rs.getString("name"), rs.getString("phone_number"),
						rs.getString("start_date"), rs.getString("end_date"));
				instructor.setId(rs.getInt("id"));
				instructor.setSpecialization(rs.getString("specialization"));
				instructor.setAvailabilities(getCities(instructor.getId()));
				instructors.add(instructor);
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving instructors: " + e.getMessage());
		}
		return instructors;
	}

	private ArrayList<String> getCities(int instructorId) {
		String sql = "SELECT city FROM InstructorCities WHERE instructor_id = ?";
		ArrayList<String> cities = new ArrayList<>();

		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, instructorId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				cities.add(rs.getString("city"));
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving instructor cities: " + e.getMessage());
		}
		return cities;
	}

	public Instructor getInstructorByNameAndPhone(String name, String phoneNumber) {
		String sql = "SELECT * FROM Instructor WHERE name = ? AND phone_number = ?";

		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, name);
			pstmt.setString(2, phoneNumber);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int instructorId = rs.getInt("id");
				String startDate = rs.getString("start_date");
				String endDate = rs.getString("end_date");
				String specialization = rs.getString("specialization");

				Instructor instructor = new Instructor(name, phoneNumber, startDate, endDate);
				instructor.setId(instructorId);
				instructor.setSpecialization(specialization);
				instructor.setAvailabilities(getCities(instructorId));
				instructor.setOfferings(getOfferings(instructorId));
				return instructor;
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving instructor: " + e.getMessage());
		}
		return null;
	}

	public Instructor getInstructorById(int instructorId) {
		String sql = "SELECT * FROM Instructor WHERE id = ?";

		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, instructorId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String name = rs.getString("name");
				String phoneNumber = rs.getString("phone_number");
				String startDate = rs.getString("start_date");
				String endDate = rs.getString("end_date");
				String specialization = rs.getString("specialization");

				Instructor instructor = new Instructor(name, phoneNumber, startDate, endDate);
				instructor.setId(instructorId);
				instructor.setSpecialization(specialization);

				instructor.setAvailabilities(getCities(instructorId));
				instructor.setOfferings(getOfferings(instructorId));

				return instructor;
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving instructor by ID: " + e.getMessage());
		}
		return null;
	}

	private ArrayList<Offering> getOfferings(int instructorId) {
		ArrayList<Offering> offerings = new ArrayList<>();
		String sql = "SELECT * FROM Offering WHERE instructor_id = ?";

		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, instructorId);
		
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int scheduleId = rs.getInt("schedule_id");
				int locationId = rs.getInt("location_id");

				Schedule schedule = scheduleDAO.getScheduleById(scheduleId);
				Location location = locationDAO.getLocationById(locationId);

				if (schedule != null && location != null) {
					Offering offering = new Offering(LessonType.valueOf(rs.getString("lesson_type")),
							rs.getBoolean("is_group"), rs.getInt("capacity"), rs.getInt("start_time"),
							rs.getInt("end_time"), schedule, location);
					offering.setId(rs.getInt("id"));
					offering.setAvailability(rs.getBoolean("availability"));
					offerings.add(offering);
				}
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving instructor offerings: " + e.getMessage());
		}
		return offerings;
	}

	public void deleteInstructor(Instructor instructor) {
		String sql = "DELETE FROM Instructor WHERE id = ?";

		try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, instructor.getId());
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Instructor deleted successfully: " + instructor.getName());
			} else {
				System.out.println("Instructor not found, deletion unsuccessful.");
			}
		} catch (SQLException e) {
			System.out.println("Error deleting instructor: " + e.getMessage());
		}
	}

}
