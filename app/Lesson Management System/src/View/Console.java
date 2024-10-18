package View;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console {

	public void ClientLogin(String name, String phoneNumber) {
		ClientConsole CC = new ClientConsole();

		if (CC.logIn(name, phoneNumber)) {
			System.out.println("Account does not exist or Incorrect name/phoneNumber");
		} else {
			System.out.println("Login Successful");
		}

	}

	public void InstructorLogin(String name, String phoneNumber) {
	}

	public void AdminLogin(String username, String password) {
	}

	public void ClientRegister(String name, String phoneNumber) {
	}

	public void InstructorRegister(String name, String phoneNumber) {
	}

	private void handleAdminLogin(Scanner scanner) {
		System.out.print("Enter your username: ");
		String username = scanner.nextLine();
		System.out.print("Enter your password: ");
		String password = scanner.nextLine();
		AdminLogin(username, password);
	}

	private void handleClientLogin(Scanner scanner) {
		System.out.print("Enter your name: ");
		String name = scanner.nextLine();
		System.out.print("Enter your phone number (###-###-####): ");
		String phoneNumber = scanner.nextLine();
		if (validNumber(phoneNumber)) {
			ClientLogin(name, phoneNumber);
		} else {
			System.out.println("Invalid phone number format. Please use ###-###-####.");
		}
	}

	private void handleInstructorLogin(Scanner scanner) {
		System.out.print("Enter your name: ");
		String name = scanner.nextLine();
		System.out.print("Enter your phone number (###-###-####): ");
		String phoneNumber = scanner.nextLine();
		if (validNumber(phoneNumber)) {
			InstructorLogin(name, phoneNumber);
		} else {
			System.out.println("Invalid phone number format. Please use ###-###-####.");
		}
	}

	private void handleClientRegister(Scanner scanner) {
		System.out.print("Enter your name: ");
		String name = scanner.nextLine();
		System.out.print("Enter your phone number (###-###-####): ");
		String phoneNumber = scanner.nextLine();
		if (validNumber(phoneNumber)) {
			ClientRegister(name, phoneNumber);
		} else {
			System.out.println("Invalid phone number format. Please use ###-###-####.");
		}
	}

	private void handleInstructorRegister(Scanner scanner) {
		System.out.print("Enter your name: ");
		String name = scanner.nextLine();
		System.out.print("Enter your phone number (###-###-####): ");
		String phoneNumber = scanner.nextLine();
		if (validNumber(phoneNumber)) {
			InstructorRegister(name, phoneNumber);
		} else {
			System.out.println("Invalid phone number format. Please use ###-###-####.");
		}
	}

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

			int choice = 0;

			try {
				choice = scanner.nextInt();
				scanner.nextLine(); // Consume the newline character

				switch (choice) {
				case 1:
					handleClientLogin(scanner);
					break;
				case 2:
					handleInstructorLogin(scanner);
					break;
				case 3:
					handleAdminLogin(scanner);
					break;
				case 4:
					handleClientRegister(scanner);
					break;
				case 5:
					handleInstructorRegister(scanner);
					break;
				case 6:
					System.out.println("Closing Console...");
					running = false;
					break;
				default:
					System.out.println("Invalid option, please try again.");
					break;
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Please enter a number between 1 and 6.");
				scanner.nextLine(); // Clear the invalid input
			}
		}

		scanner.close();
	}

	public boolean validNumber(String number) {
		String regex = "\\d{3}-\\d{3}-\\d{4}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(number);
		return matcher.matches();
	}
}
