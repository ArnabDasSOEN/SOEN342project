package Controller;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Model.Instructor;
import Model.LessonType;
import Model.Location;
import Model.Offering;
import Model.Schedule;
import Database.OfferingDAO;

public class OfferingController {
	private static OfferingController instance;
	private OfferingDAO offeringDAO;
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private ArrayList offeringCollection;

	private OfferingController() {
		this.offeringDAO = new OfferingDAO();
	}

	public static OfferingController getInstance() {
		if (instance == null) {
			instance = new OfferingController();
		}
		return instance;
	}

	// Method to create a new offering
	public boolean createOffering(Location location, Schedule schedule, int startTime, int endTime, boolean isGroup,
			int capacity, LessonType lessonType) {
		lock.writeLock().lock(); // Acquire write lock
		try {
			if (!validate(location, schedule, startTime, endTime)) {
				return false; // Return false if an overlapping offering exists
			}

			Offering newOffering = new Offering(lessonType, isGroup, capacity, startTime, endTime, schedule, location);
			schedule.addOffering(newOffering); // Add to schedule
			offeringDAO.addOffering(newOffering); // Persist in the database
			return true;
		} finally {
			lock.writeLock().unlock(); // Release write lock
		}
	}

	// Helper method to validate that no overlapping offerings exist
	private boolean validate(Location location, Schedule schedule, int startTime, int endTime) {
		return !find(location, schedule, startTime, endTime); // Return true if no conflicts are found
	}

	// Private helper method to find overlapping offerings
	private boolean find(Location location, Schedule schedule, int startTime, int endTime) {
		for (Offering offering : offeringDAO.getAllOfferings()) {
			if (offering.equals(location, schedule, startTime, endTime)) {
				return true;
			}
		}
		return false; // No matching offering found
	}

	public ArrayList<Offering> getUnassignedOfferings() {
		return offeringDAO.getAllUnassignedOfferings();
	}

	// Retrieve all offerings
	public ArrayList<Offering> getOfferingCollection() {
		lock.readLock().lock(); // Acquire read lock
		try {
			return new ArrayList<>(offeringDAO.getAllOfferings());
		} finally {
			lock.readLock().unlock(); // Release read lock
		}
	}

	// Find offerings based on location and lesson type
	public ArrayList<Offering> find(ArrayList<Location> availableLocations, LessonType specialization) {
		lock.readLock().lock(); // Acquire read lock
		try {
			ArrayList<Offering> matchingOfferings = new ArrayList<>();
			for (Offering offering : offeringDAO.getAllOfferings()) {
				if (availableLocations.contains(offering.getLocation())
						&& offering.getLessonType().equals(specialization)) {
					matchingOfferings.add(offering);
				}
			}
			return matchingOfferings;
		} finally {
			lock.readLock().unlock(); // Release read lock
		}
	}

	// Check if an offering has an instructor
	public boolean available(Offering offering) {
		lock.readLock().lock(); // Acquire read lock
		try {
			return !offering.hasInstructor(); // True if there is no instructor assigned
		} finally {
			lock.readLock().unlock(); // Release read lock
		}
	}

	// Find potential offerings for instructors based on cities and lesson type
	public ArrayList<Offering> findPotentialOfferings(ArrayList<String> cities, LessonType specialization) {
		lock.readLock().lock(); // Acquire read lock
		try {
			ArrayList<Offering> potentialOfferings = new ArrayList<>();
			for (Offering offer : offeringDAO.getAllOfferings()) {
				// Only include offerings that do not have an instructor assigned
				if (!offer.hasInstructor() && cities.contains(offer.getLocation().getCity())
						&& offer.getLessonType().equals(specialization)) {
					potentialOfferings.add(offer);
				}
			}
			return potentialOfferings;
		} finally {
			lock.readLock().unlock(); // Release read lock
		}
	}

	// View public offerings that have an instructor assigned
	public ArrayList<Offering> viewPublicOfferings() {
		lock.readLock().lock(); // Acquire read lock
		try {
			ArrayList<Offering> publicOfferings = new ArrayList<>();
			for (Offering offering : offeringDAO.getAllOfferings()) {
				if (offering.hasInstructor()) {
					publicOfferings.add(offering);
				}
			}
			return publicOfferings;
		} finally {
			lock.readLock().unlock(); // Release read lock
		}
	}

	// Check if an offering is fully booked
	public boolean isFull(Offering offering) {
		lock.readLock().lock(); // Acquire read lock
		try {
			return !(offering.getCapacity() > 0); // Returns true if capacity is 0 or less
		} finally {
			lock.readLock().unlock(); // Release read lock
		}
	}

	// Decrease the capacity of an offering when a new booking is made
	public void decrementCapacity(Offering offering) {
		lock.writeLock().lock(); // Acquire write lock
		try {
			offering.spotFilled();
			offeringDAO.updateOfferingCapacity(offering.getId(), offering.getCapacity()); // Persist change
		} finally {
			lock.writeLock().unlock(); // Release write lock
		}
	}

	public void incrementCapacity(Offering offering) {
		lock.writeLock().lock(); // Acquire write lock
		try {
			offering.incrementCapacity();
			offeringDAO.updateOfferingCapacity(offering.getId(), offering.getCapacity()); // Persist change
		} finally {
			lock.writeLock().unlock(); // Release write lock
		}
	}

	public void assignInstructorToOffering(Offering offering, Instructor instructor) {
		lock.writeLock().lock(); // Acquire write lock to prevent concurrent modification
		try {
			offering.setInstructor(instructor); // Set the instructor for the offering object
			offeringDAO.assignInstructorToOffering(offering.getId(), instructor.getId()); // Persist the change in DB
		} finally {
			lock.writeLock().unlock(); // Release lock
		}
	}

	public void refreshOfferings() {
		lock.writeLock().lock();
		try {
			// Reload offerings from the database
			this.offeringCollection = new ArrayList<>(offeringDAO.getAllOfferings());
		} finally {
			lock.writeLock().unlock();
		}
	}

}
