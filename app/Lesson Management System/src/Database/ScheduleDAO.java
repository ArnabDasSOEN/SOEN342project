package Database;

import java.sql.*;
import java.util.ArrayList;
import Model.Schedule;
import Model.Location;

public class ScheduleDAO {

    public void addSchedule(Schedule schedule, Location location) {
        // Use consistent column names
        String sql = "INSERT INTO Schedule (start_date, end_date, day_of_week, start_time, end_time, location_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, schedule.getStartDate());
            pstmt.setString(2, schedule.getEndDate());
            pstmt.setInt(3, schedule.getDayOfWeek());
            pstmt.setInt(4, schedule.getStartTime());
            pstmt.setInt(5, schedule.getEndTime());
            pstmt.setInt(6, location.getId());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    schedule.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding schedule: " + e.getMessage());
        }
    }

    public ArrayList<Schedule> getSchedulesForLocation(int locationId) {
        ArrayList<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM Schedule WHERE location_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, locationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Schedule schedule = new Schedule(
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getInt("day_of_week"),
                        rs.getInt("start_time"),
                        rs.getInt("end_time")
                );
                schedule.setId(rs.getInt("id"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving schedules: " + e.getMessage());
        }
        return schedules;
    }

    public Schedule getScheduleById(int id) {
        String sql = "SELECT * FROM Schedule WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
    
            if (rs.next()) {
                String startDate = rs.getString("start_date");
                String endDate = rs.getString("end_date");
                int dayOfWeek = rs.getInt("day_of_week");
                int startTime = rs.getInt("start_time");
                int endTime = rs.getInt("end_time");
    
                Schedule schedule = new Schedule(startDate, endDate, dayOfWeek, startTime, endTime);
                schedule.setId(id); // Ensure Schedule has setId
                return schedule;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving schedule by ID: " + e.getMessage());
        }
        return null;
    }
}
