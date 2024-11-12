package Database;

public class DatabaseInitializer {
    public static void main(String[] args) {
        DatabaseSetup.createTables();
        System.out.println("Database setup complete.");
    }
}
