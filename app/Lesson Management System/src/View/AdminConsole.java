package View;

import Controller.LocationController;
import Controller.OfferingController;
import Controller.ScheduleController;
import Model.Schedule;
import Model.TypeOfSpace;
import Model.LessonType;
import Model.Location;
import Model.Offering;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AdminConsole {
	private String username = "1";
	private String password = "1";
	private LocationController locationController = LocationController.getInstance();
	private ScheduleController scheduleController = ScheduleController.getInstance();
	private OfferingController offeringController = OfferingController.getInstance();

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void consoleMenu() {
		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		while (running) {
			displayMenuOptions();
			int choice = getMenuChoice(scanner);

			switch (choice) {
			case 1:
				handleAddLocation(scanner);
				break;
			case 2:
				viewLocations(scanner);
				break;
			case 3:
				System.out.println("Logging out...");
				running = false; // Exit the loop and log out
				break;
			default:
				System.out.println("Invalid option, please try again.");
				break;
			}
		}

		scanner.close();
	}

	private void displayMenuOptions() {
		System.out.println("\n==== Admin Console Menu ====");
		System.out.println("1. Add Location");
		System.out.println("2. View Locations");
		System.out.println("3. Logout");
		System.out.print("Select an option: ");
	}

	private int getMenuChoice(Scanner scanner) {
		int choice = 0;
		while (true) {
			try {
				choice = scanner.nextInt();
				scanner.nextLine(); // Consume the newline character
				break; // Exit loop if input is valid
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Please enter a number corresponding to the menu option.");
				scanner.nextLine(); // Clear the invalid input
			}
		}
		return choice;
	}

	private void handleAddLocation(Scanner scanner) {
		String name = promptUserForInput(scanner, "Enter location name: ");
		String address = promptUserForInput(scanner, "Enter location address: ");
		String city = promptUserForInput(scanner, "Enter location city: ");
		TypeOfSpace typeOfSpace = promptUserForSpaceType(scanner);

		if (typeOfSpace != null) {
			Location result = locationController.createLocation(name, address, city, typeOfSpace);

			if (result != null) {
				System.out.println("Location added successfully.");
				System.out.println("Now add Schedule");
				handleCreateSchedule(scanner, result);
			} else {
				System.out.println("Location already exists or failed to add.");
			}
		}
	}

	private String promptUserForInput(Scanner scanner, String prompt) {
		System.out.print(prompt);
		return scanner.nextLine();
	}

	private TypeOfSpace promptUserForSpaceType(Scanner scanner) {
		System.out.print("Enter type of space (e.g., CLASSROOM, AUDITORIUM): ");
		String type = scanner.nextLine();
		if (isValidTypeOfSpace(type)) {
			return TypeOfSpace.valueOf(type.toUpperCase());
		} else {
			System.out.println("Invalid Type of Space. Please enter one of the following:");
			for (TypeOfSpace types : TypeOfSpace.values()) {
				System.out.println(types);
			}
			return null;
		}
	}

	private void handleCreateSchedule(Scanner scanner, Location location) {
		String startDate = promptUserForInput(scanner, "Enter schedule start date (yyyy-mm-dd): ");
		String endDate = promptUserForInput(scanner, "Enter schedule end date (yyyy-mm-dd): ");
		int dayOfWeek = promptUserForInteger(scanner, "Enter day of the week (1 for Monday, 7 for Sunday): ");
		int startTime = promptUserForInteger(scanner, "Enter start time (0-24): ");
		int endTime = promptUserForInteger(scanner, "Enter end time (0-24): ");

		if (isValidScheduleInput(startDate, endDate, dayOfWeek, startTime, endTime)) {
			Schedule newSchedule = scheduleController.createSchedule(startDate, endDate, dayOfWeek, startTime, endTime,
					location);
			System.out.println(newSchedule != null ? "Schedule created successfully." : "Failed to create schedule.");
		}
	}

	private int promptUserForInteger(Scanner scanner, String prompt) {
		int number = -1;
		while (true) {
			try {
				System.out.print(prompt);
				number = scanner.nextInt();
				scanner.nextLine(); // Consume the newline character
				break; // Exit loop if input is valid
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Please enter a valid number.");
				scanner.nextLine(); // Clear the invalid input
			}
		}
		return number;
	}

	private boolean isValidScheduleInput(String startDate, String endDate, int dayOfWeek, int startTime, int endTime) {
		if (!isValidDateFormat(startDate) || !isValidDateFormat(endDate)) {
			System.out.println("Invalid date format. Please use yyyy-mm-dd.");
			return false;
		}
		if (!areDatesValid(startDate, endDate)) {
			System.out.println("Start date must be before end date.");
			return false;
		}
		if (!isValidTime(startTime) || !isValidTime(endTime)) {
			System.out.println("Start time and end time must be between 0 and 24 hours.");
			return false;
		}
		if (!isValidDayOfWeek(dayOfWeek)) {
			System.out.println("Day of the week must be between 1 (Monday) and 7 (Sunday).");
			return false;
		}
		return true;
	}

	public boolean logIn(String username, String password) {
		return getUsername().equals(username) && getPassword().equals(password);
	}

	private boolean isValidTypeOfSpace(String typeOfSpaceStr) {
		try {
			TypeOfSpace.valueOf(typeOfSpaceStr.toUpperCase());
			return true;
		} catch (IllegalArgumentException e) {
			return false; // Handle invalid TypeOfSpace
		}
	}

	public void viewLocations(Scanner scanner) {
		ArrayList<Location> locations = locationController.getLocationCollection();

		if (locations.isEmpty()) {
			System.out.println("No locations available.");
			return;
		}

		displayLocationList(locations);
		int choice = getLocationChoice(scanner, locations.size());

		if (choice > 0 && choice <= locations.size()) {
			handleLocationOptions(scanner, locations.get(choice - 1));
		} else {
			System.out.println("Invalid option. Please try again.");
		}
	}

	private void displayLocationList(ArrayList<Location> locations) {
		System.out.println("\n==== List of Locations ====");
		for (int i = 0; i < locations.size(); i++) {
			System.out.println((i + 1) + ". " + locations.get(i));
		}
		System.out.println("\nSelect a location by number to view its options, or 0 to go back:");
	}

	private int getLocationChoice(Scanner scanner, int maxChoice) {
		return promptUserForInteger(scanner, "Select a location (1-" + maxChoice + ", 0 to go back): ");
	}

	private void handleLocationOptions(Scanner scanner, Location location) {
		System.out.println("\nSelected Location: " + location);
		System.out.println("1. Add a new schedule to this location");
		System.out.println("2. View all schedules for this location");
		System.out.println("3. Go back to the location list");
		System.out.print("Select an option: ");

		int choice = promptUserForInteger(scanner, "Select an option: ");

		switch (choice) {
		case 1:
			handleCreateSchedule(scanner, location);
			break;
		case 2:
			viewSchedules(scanner, location);
			break;
		case 3:
			return; // Go back to the location list
		default:
			System.out.println("Invalid option. Please try again.");
			break;
		}
	}

	private void viewSchedules(Scanner scanner, Location location) {
		ArrayList<Schedule> schedules = scheduleController.getSchedulesForLocation(location);
		if (schedules.isEmpty()) {
			System.out.println("No schedules available for this location.");
		} else {
			System.out.println("Schedules for " + location.getName() + ":");
			for (int i = 0; i < schedules.size(); i++) {
				System.out.println((i + 1) + ". " + schedules.get(i));
			}

			int choice = promptUserForInteger(scanner,
					"Select a schedule to view offerings (1-" + schedules.size() + ", 0 to go back): ");
			if (choice > 0 && choice <= schedules.size()) {
				handleScheduleOptions(scanner, schedules.get(choice - 1), location);
			} else {
				System.out.println("Going back to location options.");
			}
		}
	}

	private void handleScheduleOptions(Scanner scanner, Schedule schedule, Location location) {
		System.out.println("\nSelected Schedule: " + schedule);
		System.out.println("1. View Offerings");
		System.out.println("2. Create Offering");
		System.out.println("3. Go back to the schedule list");
		System.out.print("Select an option: ");

		int choice = promptUserForInteger(scanner, "Select an option: ");

		switch (choice) {
		case 1:
			viewOfferings(scanner, schedule);
			break;
		case 2:
			createOffering(scanner, schedule, location);
			break;
		case 3:
			return; // Go back to the location list
		default:
			System.out.println("Invalid option. Please try again.");
			break;
		}
	}

	private void viewOfferings(Scanner scanner, Schedule schedule) {
		ArrayList<Offering> offerings = schedule.getOfferings();
		if (offerings.isEmpty()) {
			System.out.println("No offerings available for this schedule.");
		} else {
			System.out.println("Offerings for " + schedule + ":");
			for (Offering offering : offerings) {
				System.out.println(offering);
			}
		}
	}

	// Updated createOffering method in AdminConsole class
	private void createOffering(Scanner scanner, Schedule schedule, Location location) {
		int startTime = promptUserForInteger(scanner, "Enter start time (0-24): ");
		int endTime = promptUserForInteger(scanner, "Enter end time (0-24): ");

		boolean isGroup = promptUserForBoolean(scanner, "Is this a group offering? (true/false): ");
		int capacity = promptUserForInteger(scanner, "Enter capacity for this offering: ");
		LessonType lessonType = promptUserForLessonType(scanner); // You need to implement this method

		OfferingController offeringController = OfferingController.getInstance(); // Ensure you get the instance

		// Create the offering using the controller
		boolean success = offeringController.createOffering(location, schedule, startTime, endTime, isGroup, capacity,
				lessonType);

		if (success) {
			System.out.println("Offering created successfully.");
		} else {
			System.out.println("Failed to create offering due to conflicts or invalid data.");
		}
	}

	// New helper method to get boolean input
	private boolean promptUserForBoolean(Scanner scanner, String prompt) {
		while (true) {
			System.out.print(prompt);
			String input = scanner.nextLine().trim();
			if (input.equalsIgnoreCase("true")) {
				return true;
			} else if (input.equalsIgnoreCase("false")) {
				return false;
			} else {
				System.out.println("Invalid input. Please enter 'true' or 'false'.");
			}
		}
	}

	// New helper method to get LessonType
	private LessonType promptUserForLessonType(Scanner scanner) {
		System.out.print("Enter lesson type (e.g., MATH, SCIENCE): ");
		String type = scanner.nextLine();
		try {
			return LessonType.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid Lesson Type. Please enter a valid type.");
			return promptUserForLessonType(scanner); // Retry until valid
		}
	}

	private boolean isValidDateFormat(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(date);
			return true;
		} catch (ParseException e) {
			return false; // Invalid date format
		}
	}

	private boolean areDatesValid(String startDate, String endDate) {
		return startDate.compareTo(endDate) < 0; // Returns true if startDate is before endDate
	}

	private boolean isValidTime(int time) {
		return time >= 0 && time <= 24; // Valid if time is between 0 and 24
	}

	private boolean isValidDayOfWeek(int dayOfWeek) {
		return dayOfWeek >= 1 && dayOfWeek <= 7; // Valid if dayOfWeek is between 1 and 7
	}
}
