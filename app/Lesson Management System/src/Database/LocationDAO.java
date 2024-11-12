package Database;

import Model.Location;
import Model.Schedule;
import Model.TypeOfSpace;
import java.sql.*;
import java.util.ArrayList;

public class LocationDAO {

	public void addLocation(Location location) {
	    String sql = "INSERT INTO Location (name, address, city, type_of_space) VALUES (?, ?, ?, ?)"; // Matches column name
	    
	    try (Connection conn = DatabaseConnection.connect();
	         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        pstmt.setString(1, location.getName());
	        pstmt.setString(2, location.getAddress());
	        pstmt.setString(3, location.getCity());
	        pstmt.setString(4, location.getTypeOfSpace().toString()); // Convert enum to String for insertion
	        pstmt.executeUpdate();

	        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                location.setId(generatedKeys.getInt(1));
	            }
	        }
	        addSchedules(location);

	    } catch (SQLException e) {
	        System.out.println("Error adding location: " + e.getMessage());
	    }
	}


    // Modified addSchedules to prevent overlapping schedules
    public void addSchedules(Location location) {
        for (Schedule schedule : location.getSchedules()) {
            if (!isScheduleConflicting(location.getId(), schedule)) {
                new ScheduleDAO().addSchedule(schedule, location);
            } else {
                System.out.println("Schedule conflict detected for location: " + location.getName() + " on " +
                        schedule.getStartDate() + ". Schedule not added.");
            }
        }
    }

    // Method to check if a schedule conflicts with existing ones in the database
    private boolean isScheduleConflicting(int locationId, Schedule newSchedule) {
        String sql = "SELECT * FROM Schedule WHERE location_id = ? AND day_of_week = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, locationId);
            pstmt.setInt(2, newSchedule.getDayOfWeek());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int existingStart = rs.getInt("start_time");
                int existingEnd = rs.getInt("end_time");
                int newStart = newSchedule.getStartTime();
                int newEnd = newSchedule.getEndTime();

                if (newStart < existingEnd && newEnd > existingStart) {
                    // Overlap detected
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking schedule conflicts: " + e.getMessage());
        }
        return false; // No conflicts
    }

    public Location getLocationById(int id) {
        String sql = "SELECT * FROM Location WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String address = rs.getString("address");
                String city = rs.getString("city");
                TypeOfSpace typeOfSpace = TypeOfSpace.valueOf(rs.getString("type_of_space"));

                Location location = new Location(name, address, city, typeOfSpace);
                location.setId(id);
                location.setSchedules(new ScheduleDAO().getSchedulesForLocation(id));
                return location;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving location by ID: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Location> getAllLocations() {
        ArrayList<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM Location";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                String address = rs.getString("address");
                String city = rs.getString("city");
                TypeOfSpace typeOfSpace = TypeOfSpace.valueOf(rs.getString("type_of_space"));

                Location location = new Location(name, address, city, typeOfSpace);
                location.setId(rs.getInt("id"));
                location.setSchedules(new ScheduleDAO().getSchedulesForLocation(rs.getInt("id")));
                locations.add(location);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all locations: " + e.getMessage());
        }
        return locations;
    }

    public boolean locationExists(String name, String address, String city) {
        String sql = "SELECT COUNT(*) FROM Location WHERE name = ? AND address = ? AND city = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setString(3, city);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Error checking location existence: " + e.getMessage());
            return false;
        }
    }

    public Location getLocationByName(String name) {
        String sql = "SELECT * FROM Location WHERE name = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String address = rs.getString("address");
                String city = rs.getString("city");
                TypeOfSpace typeOfSpace = TypeOfSpace.valueOf(rs.getString("type_of_space"));

                Location location = new Location(name, address, city, typeOfSpace);
                location.setId(rs.getInt("id"));
                location.setSchedules(new ScheduleDAO().getSchedulesForLocation(rs.getInt("id")));
                return location;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving location by name: " + e.getMessage());
        }
        return null;
    }
}
