package Database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {

	public static void resetDatabase() {
	    dropTables();
	    createTables();
	    populateSampleData();
	}


	public static void populateSampleData() {
	    try (Connection conn = DatabaseConnection.connect();
	         Statement stmt = conn.createStatement()) {

	        // Insert Sample Clients
	        String insertClients = """
	                INSERT INTO Client (name, phone_number, age) VALUES
	                ('James Russo', '123-456-7890', 40),
	                ('Lisa Russo', '123-456-7891', 14),
	                ('Emily Green', '123-456-7892', 30);
	                """;
	        stmt.execute(insertClients);

	        // Insert Sample Instructors
	        String insertInstructors = """
	                INSERT INTO Instructor (name, phone_number, specialization, start_date, end_date) VALUES
	                ('John Doe', '111-222-3333', 'YOGA', '2023-01-01', '2023-12-31'),
	                ('Jane Smith', '222-333-4444', 'DANCE', '2023-01-01', '2023-12-31');
	                """;
	        stmt.execute(insertInstructors);

	        // Insert Sample Instructor Cities
	        String insertInstructorCities = """
	                INSERT INTO InstructorCities (instructor_id, city) VALUES
	                (1, 'New York'),
	                (1, 'Los Angeles'),
	                (2, 'Chicago');
	                """;
	        stmt.execute(insertInstructorCities);

	        // Insert Sample Lesson Types (Matching Enum)
	        String insertLessonTypes = """
	                INSERT INTO LessonType (name) VALUES
	                ('YOGA'),
	                ('DANCE'),
	                ('MUSIC'),
	                ('ART');
	                """;
	        stmt.execute(insertLessonTypes);

	        // Insert Sample Types of Spaces (Matching Enum)
	        String insertTypeOfSpace = """
	                INSERT INTO TypeOfSpace (space_type) VALUES
	                ('POOL'),
	                ('ROOM');
	                """;
	        stmt.execute(insertTypeOfSpace);

	        // Insert Sample Locations
	        String insertLocations = """
	                INSERT INTO Location (name, address, city, type_of_space) VALUES
	                ('NYC Yoga Pool', '123 Main St', 'New York', 'POOL'),
	                ('LA Dance Room', '456 Hollywood Blvd', 'Los Angeles', 'ROOM');
	                """;
	        stmt.execute(insertLocations);

	        // Insert Sample Schedules
	        String insertSchedules = """
	                INSERT INTO Schedule (start_date, end_date, day_of_week, start_time, end_time, location_id) VALUES
	                ('2023-09-01', '2023-12-01', 1, '09:00', '20:00', 1), -- Monday Schedule for Yoga Pool
	                ('2023-09-01', '2023-12-01', 1, '09:00', '20:00', 2); -- Monday Schedule for Dance Room
	                """;
	        stmt.execute(insertSchedules);

	        // Insert Sample Overlapping Offerings with Enum Matching Lesson Types
	        String insertOfferings = """
	                INSERT INTO Offering (lesson_type, is_group, availability, capacity, start_time, end_time, has_instructor, instructor_id, schedule_id, location_id) VALUES
	                ('YOGA', 1, 1, 20, '09:00', '11:00', 1, 1, 1, 1),  -- Offering on Monday from 9:00 to 11:00 at Yoga Pool
	                ('YOGA', 1, 1, 15, '10:00', '12:00', 1, 1, 1, 1),  -- Overlapping Offering on Monday from 10:00 to 12:00 at Yoga Pool
	                ('DANCE', 1, 1, 30, '09:30', '11:30', 1, 2, 2, 2), -- Offering on Monday from 9:30 to 11:30 at Dance Room
	                ('DANCE', 1, 1, 25, '10:30', '12:30', 1, 2, 2, 2); -- Overlapping Offering on Monday from 10:30 to 12:30 at Dance Room
	                """;
	        stmt.execute(insertOfferings);

	        // Insert Sample Bookings
	        String insertBookings = """
	                INSERT INTO Booking (booking_date, status, client_id, offering_id) VALUES
	                ('2023-09-10', 1, 1, 1),
	                ('2023-09-10', 1, 2, 2),
	                ('2023-09-10', 1, 1, 3),
	                ('2023-09-10', 1, 2, 4);
	                """;
	        stmt.execute(insertBookings);

	        System.out.println("Sample data with overlapping offerings populated successfully.");

	    } catch (SQLException e) {
	        System.out.println("Error populating sample data: " + e.getMessage());
	    }
	}


    
    private static void dropTables() {
        String dropClientTable = "DROP TABLE IF EXISTS Client;";
        String dropInstructorTable = "DROP TABLE IF EXISTS Instructor;";
        String dropLessonTypeTable = "DROP TABLE IF EXISTS LessonType;";
        String dropTypeOfSpaceTable = "DROP TABLE IF EXISTS TypeOfSpace;";
        String dropLocationTable = "DROP TABLE IF EXISTS Location;";
        String dropScheduleTable = "DROP TABLE IF EXISTS Schedule;";
        String dropOfferingTable = "DROP TABLE IF EXISTS Offering;";
        String dropBookingTable = "DROP TABLE IF EXISTS Booking;";
        String dropInstructorCitiesTable = "DROP TABLE IF EXISTS InstructorCities;";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(dropInstructorCitiesTable);
            stmt.execute(dropBookingTable);
            stmt.execute(dropOfferingTable);
            stmt.execute(dropScheduleTable);
            stmt.execute(dropLocationTable);
            stmt.execute(dropTypeOfSpaceTable);
            stmt.execute(dropLessonTypeTable);
            stmt.execute(dropInstructorTable);
            stmt.execute(dropClientTable);

            System.out.println("Existing tables dropped successfully.");

        } catch (SQLException e) {
            System.out.println("Error dropping tables: " + e.getMessage());
        }
    }

    static void createTables() {
    	String createClientTable = "CREATE TABLE IF NOT EXISTS Client (" +
    	        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
    	        "name TEXT NOT NULL," +
    	        "phone_number TEXT UNIQUE," +
    	        "age INTEGER NOT NULL," +
    	        "guardian_id INTEGER," +
    	        "FOREIGN KEY (guardian_id) REFERENCES Client(id) ON DELETE CASCADE" +
    	        ");";


        String createInstructorTable = "CREATE TABLE IF NOT EXISTS Instructor (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "phone_number TEXT UNIQUE," +
                "specialization TEXT," +
                "start_date TEXT," +
                "end_date TEXT" +
                ");";

        String createInstructorCitiesTable = "CREATE TABLE IF NOT EXISTS InstructorCities (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "instructor_id INTEGER," +
                "city TEXT NOT NULL," +
                "FOREIGN KEY (instructor_id) REFERENCES Instructor(id) ON DELETE CASCADE" +
                ");";

        String createLessonTypeTable = "CREATE TABLE IF NOT EXISTS LessonType (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE" +
                ");";

        String createTypeOfSpaceTable = "CREATE TABLE IF NOT EXISTS TypeOfSpace (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "space_type TEXT UNIQUE" +
                ");";

        String createLocationTable = "CREATE TABLE IF NOT EXISTS Location (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "address TEXT," +
                "city TEXT," +
                "type_of_space TEXT" +
                ");";

        String createScheduleTable = "CREATE TABLE IF NOT EXISTS Schedule (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "start_date TEXT," +
                "end_date TEXT," +
                "day_of_week INTEGER," +
                "start_time TEXT," +
                "end_time TEXT," +
                "location_id INTEGER," +
                "FOREIGN KEY (location_id) REFERENCES Location(id) ON DELETE CASCADE" +
                ");";

        String createOfferingTable = "CREATE TABLE IF NOT EXISTS Offering (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "lesson_type TEXT," +
                "is_group BOOLEAN," +
                "availability BOOLEAN," +
                "capacity INTEGER," +
                "start_time TEXT," +
                "end_time TEXT," +
                "has_instructor BOOLEAN DEFAULT 0," +
                "instructor_id INTEGER," +
                "schedule_id INTEGER," +
                "location_id INTEGER," +
                "FOREIGN KEY (instructor_id) REFERENCES Instructor(id)," +
                "FOREIGN KEY (schedule_id) REFERENCES Schedule(id) ON DELETE CASCADE," +
                "FOREIGN KEY (location_id) REFERENCES Location(id) ON DELETE CASCADE" +
                ");";

        String createBookingTable = "CREATE TABLE IF NOT EXISTS Booking (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "booking_date TEXT," +
                "status BOOLEAN," +
                "client_id INTEGER," +
                "offering_id INTEGER," +
                "FOREIGN KEY (client_id) REFERENCES Client(id) ON DELETE CASCADE," +
                "FOREIGN KEY (offering_id) REFERENCES Offering(id) ON DELETE CASCADE" +
                ");";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createClientTable);
            stmt.execute(createInstructorTable);
            stmt.execute(createInstructorCitiesTable);
            stmt.execute(createLessonTypeTable);
            stmt.execute(createTypeOfSpaceTable);
            stmt.execute(createLocationTable);
            stmt.execute(createScheduleTable);
            stmt.execute(createOfferingTable);
            stmt.execute(createBookingTable);

            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println("Table creation error: " + e.getMessage());
        }
    }
}
