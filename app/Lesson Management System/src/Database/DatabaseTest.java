package Database;

import java.sql.Connection;

public class DatabaseTest {
    public static void main(String[] args) {
        Connection conn = Database.DatabaseConnection.connect();
        if (conn != null) {
            System.out.println("Database connected successfully!");
        } else {
            System.out.println("Database connection failed.");
        }
    }
}
