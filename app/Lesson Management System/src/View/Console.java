package View;

import javax.swing.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console {

    // Client login
    public void clientLogin(String name, String phoneNumber) {
        ClientConsole clientConsole = new ClientConsole();
        if (clientConsole.logIn(name, phoneNumber)) {
            System.out.println("Login successful. Starting client session...");
            SwingUtilities.invokeLater(clientConsole); // Run client console GUI in a new thread
        } else {
            System.out.println("Account does not exist or incorrect name/phone number.");
        }
    }

    // Instructor login
    public void instructorLogin(String name, String phoneNumber) {
        InstructorConsole instructorConsole = new InstructorConsole();
        boolean loginSuccessful = true; // Placeholder for actual login logic
        if (loginSuccessful) {
            System.out.println("Instructor login successful. Starting instructor session...");
            SwingUtilities.invokeLater(instructorConsole);
        } else {
            System.out.println("Account does not exist or incorrect name/phone number.");
        }
    }

    // Admin login
    public void adminLogin(String username, String password) {
        AdminConsole adminConsole = new AdminConsole();
        if (adminConsole.logIn(username, password)) {
            System.out.println("Admin login successful. Starting admin session...");
            SwingUtilities.invokeLater(adminConsole);
        } else {
            System.out.println("Incorrect username or password.");
        }
    }

    // Register client
    public void clientRegister(String name, String phoneNumber, int age) {
        ClientConsole clientConsole = new ClientConsole();
        if (clientConsole.registerNormal(name, phoneNumber, age)) {
            System.out.println("Successfully registered new client.");
        } else {
            System.out.println("Account already exists.");
        }
    }

    // Placeholder for instructor registration
    public void instructorRegister(String name, String phoneNumber) {
        System.out.println("Instructor registration feature coming soon.");
    }

    // Console menu
    public void consoleMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n==== Console Menu ====");
            System.out.println("1. Client Login");
            System.out.println("2. Instructor Login");
            System.out.println("3. Admin Login");
            System.out.println("4. Client Register");
            System.out.println("5. Instructor Register");
            System.out.println("6. Close Console");
            System.out.print("Select an option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                switch (choice) {
                    case 1 -> handleClientLogin(scanner);
                    case 2 -> handleInstructorLogin(scanner);
                    case 3 -> handleAdminLogin(scanner);
                    case 4 -> handleClientRegister(scanner);
                    case 5 -> handleInstructorRegister(scanner);
                    case 6 -> {
                        System.out.println("Closing console...");
                        running = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
                scanner.nextLine(); // Clear invalid input
            }
        }
        scanner.close();
    }

    // Helper methods
    private void handleClientLogin(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your phone number (###-###-####): ");
        String phoneNumber = scanner.nextLine();
        if (isValidPhoneNumber(phoneNumber)) {
            clientLogin(name, phoneNumber);
        } else {
            System.out.println("Invalid phone number format. Please use ###-###-####.");
        }
    }

    private void handleInstructorLogin(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your phone number (###-###-####): ");
        String phoneNumber = scanner.nextLine();
        if (isValidPhoneNumber(phoneNumber)) {
            instructorLogin(name, phoneNumber);
        } else {
            System.out.println("Invalid phone number format. Please use ###-###-####.");
        }
    }

    private void handleAdminLogin(Scanner scanner) {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        adminLogin(username, password);
    }

    private void handleClientRegister(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your phone number (###-###-####): ");
        String phoneNumber = scanner.nextLine();
        if (!isValidPhoneNumber(phoneNumber)) {
            System.out.println("Invalid phone number format. Please use ###-###-####.");
            return;
        }

        int age = getValidAge(scanner);
        clientRegister(name, phoneNumber, age);
    }

    private void handleInstructorRegister(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your phone number (###-###-####): ");
        String phoneNumber = scanner.nextLine();
        if (isValidPhoneNumber(phoneNumber)) {
            instructorRegister(name, phoneNumber);
        } else {
            System.out.println("Invalid phone number format. Please use ###-###-####.");
        }
    }

    private int getValidAge(Scanner scanner) {
        int age = -1;
        while (age <= 0) {
            System.out.print("Enter your age: ");
            try {
                age = Integer.parseInt(scanner.nextLine());
                if (age <= 0) {
                    System.out.println("Age must be a positive integer.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid age.");
            }
        }
        return age;
    }

    // Validate phone number
    public boolean isValidPhoneNumber(String number) {
        String regex = "\\d{3}-\\d{3}-\\d{4}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }
}
