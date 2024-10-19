package Controller;

import java.util.ArrayList;

import Model.Booking;
import Model.LessonType;
import Model.Location;
import Model.Offering;
import Model.Schedule;

public class OfferingController {
	private ArrayList<Offering> offeringCollection;
	private static OfferingController instance;

	private OfferingController() {
		offeringCollection = new ArrayList<>();
	}

	public static OfferingController getInstance() {
		if (instance == null) {
			instance = new OfferingController();
		}
		return instance;
	}

	public boolean createOffering(Location location, Schedule schedule, int startTime, int endTime, boolean isGroup,
			int capacity, LessonType lessonType) {
		if (!validate(location, schedule, startTime, endTime)) {
			return false; // Return false if an overlapping offering exists
		}

		Offering newOffering = new Offering(lessonType, isGroup, capacity, startTime, endTime, schedule, location);
		schedule.addOffering(newOffering);
		offeringCollection.add(newOffering);
		return true;
	}

	public boolean validate(Location location, Schedule schedule, int startTime, int endTime) {
		return !find(location, schedule, startTime, endTime); // Return true if no conflicts are found
	}

	private boolean find(Location location, Schedule schedule, int startTime, int endTime) {
		for (Offering offering : offeringCollection) {
			if (offering.equals(location, schedule, startTime, endTime)) {
				return true;
			}
		}
		return false; // No matching offering found
	}

	public ArrayList<Offering> getOfferingCollection() {
		return offeringCollection;
	}

	public ArrayList<Offering> find(ArrayList<Location> availableLocations, LessonType specialization) {
		ArrayList<Offering> matchingOfferings = new ArrayList<>();
		for (Offering offering : offeringCollection) {
			// Check if the offering's location is in the availableLocations list and
			// matches the lesson type
			if (availableLocations.contains(offering.getLocation())
					&& offering.getLessonType().equals(specialization)) {
				matchingOfferings.add(offering);
			}
		}
		return matchingOfferings; // Return the list of matching offerings
	}

	public boolean available(Offering offering) {
		return !offering.hasInstructor(); // True if there is no instructor assigned
	}

//FUNCTIONALITIES REGARDING THE INSTRUCTOR AND FINDING THEIR POTENTIAL OFFERINGS
	public ArrayList<Offering> findPotentialOfferings(ArrayList<String> cities, LessonType lessonType) { // Location[]
																											// locations
		ArrayList<Offering> potOfferings = new ArrayList<Offering>();
		// for each offer in the arrayList "offerings" (which serves as the temporary
		// database for all offerings)
		for (Offering offer : offeringCollection) {
			// for each of the locations that the instructor can work in
			for (String city : cities) {
				if (offer.equalsforFindingOfferings(city, lessonType)) {
					potOfferings.add(offer);
				}
			}
		}
		return potOfferings;
	}

}// end of class
