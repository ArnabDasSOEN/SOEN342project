package Controller;

import java.util.ArrayList;

import Model.Booking;
import Model.LessonType;
import Model.Location;
import Model.Offering;
import Model.Schedule;

public class OfferingController {
    private ArrayList<Offering> offerings;

    public boolean validate(Location L, Schedule S, int startTime, int endTime) {
        return find(L, S, startTime, endTime);
    }
    
    private boolean find(Location L, Schedule S, int startTime, int endTime) {
        for (Offering offering : offerings) {
            // Check if the location and schedule match
            if (offering.getSchedule().equals(S) && offering.getLocation().equals(L)) {
                // Check if the time slots overlap
                if ((startTime < offering.getEndTime() && endTime > offering.getStartTime())) {
                    // Overlap found
                    return false;
                }
            }
        }
        // No overlap found
        return true;
    }
    
    public void add(Offering offering) {
        offerings.add(offering);
    }
    
    public boolean createOffering(Location L, Schedule S, int startTime, int endTime, boolean isGroup, boolean availability, int capacity, LessonType lessonType) {
    // Validate the offering doesn't overlap with existing offerings
    if (!validate(L, S, startTime, endTime)) {
        // An overlapping offering exists
        return false;
    }
    
    // Create a new Offering
    Offering newOffering = new Offering(lessonType, isGroup, availability, capacity, startTime, endTime, S, null, new ArrayList<Booking>(), L);
    
    // Add the new offering to the list
    add(newOffering);
    
    return true; // Indicate successful creation
}
    public ArrayList<Offering> find(ArrayList<Location> availableLocations, LessonType specialization) {
    ArrayList<Offering> matchingOfferings = new ArrayList<>();
    for (Offering offering : offerings) {
        // Check if the offering's location is in the availableLocations list and matches the lesson type
        if (availableLocations.contains(offering.getLocation()) && 
            offering.getLessonType().equals(specialization)) {
            matchingOfferings.add(offering);
        }
    }
    return matchingOfferings; // Return the list of matching offerings
}
    public boolean available(Offering offering) {
    return !offering.hasInstructor(); // True if there is no instructor assigned
}

}
