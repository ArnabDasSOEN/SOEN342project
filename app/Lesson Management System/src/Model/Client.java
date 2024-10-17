package Model;

import java.util.ArrayList;

public class Client {
    private String name;
    private String phoneNumber;
    private int age;
    private ArrayList<Booking> bookings;
    
    public Client(String name, String phoneNumber, int age) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.bookings = new ArrayList<>();
    }

    
}
