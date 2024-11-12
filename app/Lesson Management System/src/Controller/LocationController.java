package Controller;

import Database.LocationDAO;
import Model.Location;
import Model.Schedule;
import Model.TypeOfSpace;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocationController {
    private static LocationController instance;
    private ArrayList<Location> locationCollection;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private LocationDAO locationDAO;

    private LocationController() {
        locationDAO = new LocationDAO();
        locationCollection = locationDAO.getAllLocations(); // Load all locations from the database
    }

    public static LocationController getInstance() {
        if (instance == null) {
            instance = new LocationController();
        }
        return instance;
    }

    // Create a new location and save it to the database
    public Location createLocation(String name, String address, String city, TypeOfSpace typeOfSpace) {
        lock.writeLock().lock();  // Acquire write lock
        try {
            if (locationExists(name, address, city)) {
                return null; // Location already exists
            }

            Location newLocation = new Location(name, address, city, typeOfSpace);
            locationDAO.addLocation(newLocation); // Persist to database
            locationCollection.add(newLocation);
            return newLocation;
        } finally {
            lock.writeLock().unlock();  // Release write lock
        }
    }

    // Check if a location exists either in memory or in the database
    public boolean locationExists(String name, String address, String city) {
        lock.readLock().lock();  // Acquire read lock
        try {
            for (Location location : locationCollection) {
                if (location.equals(name, address, city)) {
                    return true;
                }
            }
            return locationDAO.locationExists(name, address, city); // Check in the database as well
        } finally {
            lock.readLock().unlock();  // Release read lock
        }
    }

    // Add a schedule to a location and persist it to the database
    public boolean addScheduleToLocation(Location location, Schedule schedule) {
        lock.writeLock().lock();  // Acquire write lock
        try {
            if (location != null && schedule != null) {
                if (!isScheduleConflicting(location, schedule)) {
                    location.addSchedule(schedule);
                    locationDAO.addSchedules(location); // Persist schedules to the database
                    return true;
                }
                return false;
            }
            return false;
        } finally {
            lock.writeLock().unlock();  // Release write lock
        }
    }

    // Private method to check if a schedule conflicts with existing ones
    private boolean isScheduleConflicting(Location location, Schedule newSchedule) {
        for (Schedule existingSchedule : location.getSchedules()) {
            if (existingSchedule.getDayOfWeek() == newSchedule.getDayOfWeek() && timeOverlaps(existingSchedule, newSchedule)) {
                return true;
            }
        }
        return false;
    }

    // Method to check for time overlaps in schedules
    private boolean timeOverlaps(Schedule existing, Schedule newSchedule) {
        int existingStart = existing.getStartTime();
        int existingEnd = existing.getEndTime();
        int newStart = newSchedule.getStartTime();
        int newEnd = newSchedule.getEndTime();
        return (newStart < existingEnd && newEnd > existingStart);
    }

    // Retrieve the location collection (from memory)
    public ArrayList<Location> getLocationCollection() {
        lock.readLock().lock();  // Acquire read lock
        try {
            return new ArrayList<>(locationCollection);
        } finally {
            lock.readLock().unlock();  // Release read lock
        }
    }

    // Get a specific location by name
    public Location getLocationByName(String name) {
        lock.readLock().lock();
        try {
            for (Location location : locationCollection) {
                if (location.getName().equalsIgnoreCase(name)) {
                    return location;
                }
            }
            return locationDAO.getLocationByName(name); // Retrieve from the database if not in memory
        } finally {
            lock.readLock().unlock();
        }
    }
}
