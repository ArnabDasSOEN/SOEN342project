package View;

import Controller.ClientController;
import Controller.InstructorController;
import Controller.LocationController;
import Controller.OfferingController;
import Controller.ScheduleController;
import Model.Client;
import Model.Instructor;
import Model.Location;
import Model.Schedule;
import Model.TypeOfSpace;
import Model.LessonType;
import Model.Offering;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminConsole extends JFrame implements Runnable {
    private String username = "admin";
    private String password = "admin";
    private LocationController locationController = LocationController.getInstance();
    private ScheduleController scheduleController = ScheduleController.getInstance();
    private OfferingController offeringController = OfferingController.getInstance();
    private ClientController clientController = ClientController.getInstance();
    private InstructorController instructorController = InstructorController.getInstance();

    private JTextArea outputArea;
    private JButton addButton, viewButton, manageLocationButton, manageAccountsButton, viewOfferingsButton, logoutButton;
    
    public AdminConsole() {
        setTitle("Admin Console");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel for admin actions
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 6));

        addButton = new JButton("Add Location");
        viewButton = new JButton("View Locations");
        manageLocationButton = new JButton("Manage Location");
        manageAccountsButton = new JButton("Manage Accounts");
        viewOfferingsButton = new JButton("View Offerings"); // New button to view all offerings
        logoutButton = new JButton("Logout");

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(manageLocationButton);
        buttonPanel.add(manageAccountsButton);
        buttonPanel.add(viewOfferingsButton); // Add the new button
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button Action Listeners
        addButton.addActionListener(e -> handleAddLocation());
        viewButton.addActionListener(e -> viewLocations());
        manageLocationButton.addActionListener(e -> handleManageLocation());
        manageAccountsButton.addActionListener(e -> handleManageAccounts());
        viewOfferingsButton.addActionListener(e -> viewOfferings()); // Add action for viewing offerings
        logoutButton.addActionListener(e -> dispose());
    }
    @Override
    public void run() {
        setVisible(true);
        displayMessage("==== Admin Console Menu ====");
    }

    private void displayMessage(String message) {
        outputArea.append(message + "\n");
    }

    // Handle adding a new location
    private void handleAddLocation() {
        String name = JOptionPane.showInputDialog(this, "Enter location name:");
        String address = JOptionPane.showInputDialog(this, "Enter location address:");
        String city = JOptionPane.showInputDialog(this, "Enter location city:");
        String type = JOptionPane.showInputDialog(this, "Enter type of space (e.g., CLASSROOM, AUDITORIUM):");

        TypeOfSpace typeOfSpace = isValidTypeOfSpace(type) ? TypeOfSpace.valueOf(type.toUpperCase()) : null;
        if (typeOfSpace != null) {
            Location result = locationController.createLocation(name, address, city, typeOfSpace);
            displayMessage(result != null ? "Location added successfully." : "Location already exists or failed to add.");
        } else {
            displayMessage("Invalid Type of Space. Please enter a valid type.");
        }
    }
    private void viewOfferings() {
        List<Offering> offerings = offeringController.getOfferingCollection();
        if (offerings.isEmpty()) {
            displayMessage("No offerings available.");
        } else {
            StringBuilder offeringList = new StringBuilder("==== List of Offerings ====\n");
            for (Offering offering : offerings) {
                offeringList.append(offering).append("\n");
            }
            displayMessage(offeringList.toString());
        }
    }
    // Handle viewing all locations
    private void viewLocations() {
        List<Location> locations = locationController.getLocationCollection();
        if (locations.isEmpty()) {
            displayMessage("No locations available.");
        } else {
            StringBuilder locationList = new StringBuilder("==== List of Locations ====\n");
            for (Location loc : locations) {
                locationList.append(loc).append("\n");
            }
            displayMessage(locationList.toString());
        }
    }

    // Handle managing a specific location
    private void handleManageLocation() {
        String locationName = JOptionPane.showInputDialog(this, "Enter location name to manage:");
        Location location = locationController.getLocationByName(locationName);

        if (location != null) {
            String[] options = {"View Schedules", "Add Schedule", "Create Offering", "Back"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Manage options for " + location.getName(),
                    "Manage Location",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0 -> viewSchedules(location);
                case 1 -> handleAddSchedule(location);
                case 2 -> handleCreateOffering(location);
                default -> displayMessage("Back to main menu.");
            }
        } else {
            displayMessage("Location not found.");
        }
    }

    // View schedules for a specific location
    private void viewSchedules(Location location) {
        List<Schedule> schedules = location.getSchedules();
        if (schedules.isEmpty()) {
            displayMessage("No schedules available for this location.");
        } else {
            StringBuilder scheduleList = new StringBuilder("==== Schedules for " + location.getName() + " ====\n");
            for (Schedule schedule : schedules) {
                scheduleList.append(schedule).append("\n");
            }
            displayMessage(scheduleList.toString());
        }
    }

    // Add schedule to a specific location
    private void handleAddSchedule(Location location) {
        String startDate = JOptionPane.showInputDialog(this, "Enter schedule start date (yyyy-mm-dd):");
        String endDate = JOptionPane.showInputDialog(this, "Enter schedule end date (yyyy-mm-dd):");
        int dayOfWeek = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter day of the week (1 for Monday, 7 for Sunday):"));
        int startTime = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter start time (0-24):"));
        int endTime = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter end time (0-24):"));

        if (isValidScheduleInput(startDate, endDate, dayOfWeek, startTime, endTime)) {
            Schedule newSchedule = scheduleController.createSchedule(startDate, endDate, dayOfWeek, startTime, endTime, location);
            if (newSchedule != null) {
                locationController.addScheduleToLocation(location, newSchedule);
                displayMessage("Schedule created successfully.");
            } else {
                displayMessage("Failed to create schedule.");
            }
        } else {
            displayMessage("Invalid schedule input.");
        }
    }

    // Create an offering for a specific location
    private void handleCreateOffering(Location location) {
        List<Schedule> schedules = location.getSchedules();

        if (schedules.isEmpty()) {
            displayMessage("Cannot create an offering. No schedules are available for this location.");
            return;
        }

        Schedule selectedSchedule = chooseSchedule(schedules);
        if (selectedSchedule == null) {
            displayMessage("No schedule selected. Offering creation cancelled.");
            return;
        }

        int startTime = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter offering start time (0-24):"));
        int endTime = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter offering end time (0-24):"));
        boolean isGroup = JOptionPane.showConfirmDialog(this, "Is this a group offering?", "Group Offering", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        int capacity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter capacity for this offering:"));
        String lessonTypeStr = JOptionPane.showInputDialog(this, "Enter lesson type (e.g., MATH, SCIENCE):");

        LessonType lessonType = LessonType.valueOf(lessonTypeStr.toUpperCase());

        boolean success = offeringController.createOffering(location, selectedSchedule, startTime, endTime, isGroup, capacity, lessonType);
        displayMessage(success ? "Offering created successfully." : "Failed to create offering.");
    }

    private Schedule chooseSchedule(List<Schedule> schedules) {
        String[] scheduleOptions = schedules.stream().map(Schedule::toString).toArray(String[]::new);

        String selectedOption = (String) JOptionPane.showInputDialog(
                this,
                "Select a schedule for the offering:",
                "Choose Schedule",
                JOptionPane.PLAIN_MESSAGE,
                null,
                scheduleOptions,
                scheduleOptions[0]
        );

        return schedules.stream().filter(s -> s.toString().equals(selectedOption)).findFirst().orElse(null);
    }

    // Manage accounts (view/delete)
    private void handleManageAccounts() {
        String[] options = {"View Clients", "View Instructors", "Delete Client", "Delete Instructor", "Back"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Account Management Options",
                "Manage Accounts",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {
            case 0 -> viewClients();
            case 1 -> viewInstructors();
            case 2 -> deleteClient();
            case 3 -> deleteInstructor();
            default -> displayMessage("Back to main menu.");
        }
    }

    private void viewClients() {
        List<Client> clients = clientController.getAllClients();
        if (clients.isEmpty()) {
            displayMessage("No clients found.");
        } else {
            StringBuilder clientList = new StringBuilder("==== List of Clients ====\n");
            for (Client client : clients) {
                clientList.append(client).append("\n");
            }
            displayMessage(clientList.toString());
        }
    }

    private void viewInstructors() {
        List<Instructor> instructors = instructorController.getAllInstructors();
        if (instructors.isEmpty()) {
            displayMessage("No instructors found.");
        } else {
            StringBuilder instructorList = new StringBuilder("==== List of Instructors ====\n");
            for (Instructor instructor : instructors) {
                instructorList.append(instructor).append("\n");
            }
            displayMessage(instructorList.toString());
        }
    }

    private void deleteClient() {
        String clientName = JOptionPane.showInputDialog(this, "Enter client name to delete:");
        String clientPhone = JOptionPane.showInputDialog(this, "Enter client phone to delete:");

        Client client = clientController.getClientByNameAndPhone(clientName, clientPhone);
        if (client != null) {
            clientController.deleteClient(client);
            displayMessage("Client deleted successfully.");
        } else {
            displayMessage("Client not found.");
        }
    }

    private void deleteInstructor() {
        String instructorName = JOptionPane.showInputDialog(this, "Enter instructor name to delete:");
        String instructorPhone = JOptionPane.showInputDialog(this, "Enter instructor phone to delete:");

        Instructor instructor = instructorController.getInstructorByNameAndPhone(instructorName, instructorPhone);
        if (instructor != null) {
            instructorController.deleteInstructor(instructor);
            displayMessage("Instructor deleted successfully.");
        } else {
            displayMessage("Instructor not found.");
        }
    }

    // Input validation helper methods
    private boolean isValidScheduleInput(String startDate, String endDate, int dayOfWeek, int startTime, int endTime) {
        return isValidDateFormat(startDate) && isValidDateFormat(endDate) && startDate.compareTo(endDate) < 0 &&
               dayOfWeek >= 1 && dayOfWeek <= 7 && startTime >= 0 && startTime <= 24 && endTime >= 0 && endTime <= 24;
    }

    private boolean isValidDateFormat(String date) {
        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidTypeOfSpace(String typeOfSpaceStr) {
        try {
            TypeOfSpace.valueOf(typeOfSpaceStr.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean logIn(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}
