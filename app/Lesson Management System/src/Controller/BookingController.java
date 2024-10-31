package Controller;

import java.util.ArrayList;
import Model.Booking;
import Model.Offering;
import Model.Client;
import Model.Schedule;

public class BookingController {
    private ArrayList<Booking> bookingCollection;
    
    public ArrayList<Booking> getBookingCollection(){
        return bookingCollection;
    }

    //idk if this is right
    public boolean checkBookingAvailability(Offering of, Client client){
        for (Booking bk : bookingCollection){
           //if the booking already exists. Meaning there is already a booking where the client booked for the same offering and it's the same client, then the booking already exists
           //therefor return true, and prevent the client from booking again.
            if(bk.exists(of, client)){
            return true;
           }
        }
        //means no matching bookings were found. The user can proceed to make the booking.
        return false;
    }

    
    public void createBooking(Offering of, Client c){
        Schedule sch = of.getSchedule();
        String date = "From :" + of.getStartTime() + " to " + of.getEndTime() + " on " + sch.getStartDate(); //do we add the end date as well? is it necessary?
        //unsure of what the status is meant to signify lmao
        Booking bk = new Booking(date,true, c, of);
        this.getBookingCollection().add(bk);
    }
}
