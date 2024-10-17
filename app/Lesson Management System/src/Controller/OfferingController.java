package Controller;

import java.util.ArrayList;

import Model.Location;
import Model.Offering;
import Model.Schedule;

public class OfferingController {
    private ArrayList<Offering> offerings;

    public boolean validate(Location L, Schedule S, int startTime, int endTime) {
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
    
    

}
