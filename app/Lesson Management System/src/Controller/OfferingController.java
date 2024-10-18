package Controller;

import java.util.ArrayList;

import Model.Booking;
import Model.LessonType;
import Model.Location;
import Model.Offering;
import Model.Schedule;

public class OfferingController {
	// temporary "database" containing all offerings
	private ArrayList<Offering> offerings;

	public boolean validate(Location L, Schedule S, int startTime, int endTime) {
		return !find(L, S, startTime, endTime); // Return true if no conflicts are found
	}

	private boolean find(Location L, Schedule S, int startTime, int endTime) {
		for (Offering offering : offerings) {
			// If an offering matches the given location, schedule, and time range, return
			// true
			if (offering.equals(L, S, startTime, endTime)) {
				return true;
			}
		}
		// No matching offering was found, return false
		return false;
	}

	public void add(Offering offering) {
		this.offerings.add(offering);
	}

	public boolean createOffering(Location L, Schedule S, int startTime, int endTime, boolean isGroup,
			boolean availability, int capacity, LessonType lessonType) {
// Return false if an overlapping offering exists
		if (!validate(L, S, startTime, endTime)) {
			return false;
		}

// Create and add the new Offering
		Offering newOffering = new Offering(lessonType, isGroup, availability, capacity, startTime, endTime, S, L);
		add(newOffering);
		return true;
	}

	public ArrayList<Offering> find(ArrayList<Location> availableLocations, LessonType specialization) {
		ArrayList<Offering> matchingOfferings = new ArrayList<>();
		for (Offering offering : offerings) {
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
		for (Offering offer : offerings) {
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
