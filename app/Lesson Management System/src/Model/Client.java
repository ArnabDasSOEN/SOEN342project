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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public ArrayList<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(ArrayList<Booking> bookings) {
		this.bookings = bookings;
	}

	public boolean clientExists(String name, String phoneNumber) {
		return getName().equals(name) && getPhoneNumber().equals(phoneNumber);
	}


	//potential error in the getBookings part of this method. I commented it out becuase i don't think we need to know the bookings to know if its the same client
	//name phone number and age is enough. If they are the same, then its the same client (phone numbers are unique)
	public boolean equals(Client c){
		return this.getName() == c.getName() && this.getPhoneNumber() == c.getPhoneNumber() && this.getAge() == c.getAge();// && this.getBookings() == c.getBookings();
		//although the bookings variable is an object, they should still have the same reference considering that if it's the same client, they would both have the same reference
		//to the same bookings object.
	}

	public boolean checkBookings(Offering of){
		//for each booking
		for (Booking bk : bookings){
			if(bk.getOffering() == of){
				return true;
			}
		}
		//if no matching booking was found, then return false.
		return false;
	}

}
