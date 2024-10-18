package Controller;

import java.util.ArrayList;
import Model.Location;
import Model.Schedule;
import Model.TypeOfSpace;

public class LocationController {
	private static LocationController instance;
	private ArrayList<Location> locationCollection;

	private LocationController() {
		locationCollection = new ArrayList<>();
	}

	public static LocationController getInstance() {
		if (instance == null) {
			instance = new LocationController();
		}
		return instance;
	}

	public Location createLocation(String name, String address, String city, TypeOfSpace typeOfSpace) {
		if (locationExists(name, address, city)) {
			return null; // Location already exists
		}

		Location newLocation = new Location(name, address, city, typeOfSpace);
		locationCollection.add(newLocation);
		return newLocation; // Location created successfully
	}

	public boolean locationExists(String name, String address, String city) {
		for (Location location : locationCollection) {
			if (location.equals(name, address, city)) {
				return true; // Location exists
			}
		}
		return false; // Location does not exist
	}

	public boolean addScheduleToLocation(Location location, Schedule schedule) {
		if (location != null && schedule != null) {
			if (!isScheduleConflicting(location, schedule)) {
				location.addSchedule(schedule);
				return true; // Schedule added successfully
			}
			return false; // Conflict detected
		}
		return false; // Invalid location or schedule
	}

	private boolean isScheduleConflicting(Location location, Schedule newSchedule) {
		for (Schedule existingSchedule : location.getSchedules()) {
			if (existingSchedule.getDayOfWeek() == newSchedule.getDayOfWeek()
					&& timeOverlaps(existingSchedule, newSchedule)) {
				return true; // Conflict found
			}
		}
		return false; // No conflicts
	}

	private boolean timeOverlaps(Schedule existing, Schedule newSchedule) {
		int existingStart = existing.getStartTime();
		int existingEnd = existing.getEndTime();
		int newStart = newSchedule.getStartTime();
		int newEnd = newSchedule.getEndTime();
		return (newStart < existingEnd && newEnd > existingStart);
	}

	public ArrayList<Location> getLocationCollection() {
		return locationCollection;
	}
}
