package Model;

import java.util.ArrayList;

public class Instructor {
	private int id; // Unique identifier
	private LessonType specialization;
	private String name;
	private String phoneNumber;
	private String startDate;
	private String endDate;
	private ArrayList<Offering> offerings;
	private ArrayList<String> availabilities;

	public Instructor(String name, String phoneNumber, String startDate, String endDate) {
		this.id = 0;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.startDate = startDate;
		this.endDate = endDate;
		this.offerings = new ArrayList<>(); // Initialize offerings to avoid NullPointerException
		this.availabilities = new ArrayList<>(); // Initialize availabilities if it's used similarly
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LessonType getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization2) {

		this.specialization = LessonType.valueOf(specialization2);
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

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public ArrayList<String> getAvailabilities() {
		return availabilities;
	}

	public void setAvailabilities(ArrayList<String> availabilities) {
		this.availabilities = availabilities;
	}

	// Check if a city is in the instructor's availabilities
	public boolean isCityAvailable(String city) {
		return availabilities.contains(city);
	}

	public ArrayList<Offering> getOfferings() {
		return offerings;
	}

	public void setOfferings(ArrayList<Offering> offerings) {
		this.offerings = offerings;
	}

	public void assignOffering(Offering offering) {
		this.offerings.add(offering);
	}

	public boolean equals(Instructor ins) {
		return this.getSpecialization() == ins.getSpecialization() && this.getName().equalsIgnoreCase(ins.getName())
				&& this.getPhoneNumber().equalsIgnoreCase(ins.getPhoneNumber())
				&& this.getStartDate().equalsIgnoreCase(ins.getStartDate())
				&& this.getEndDate().equalsIgnoreCase(ins.getEndDate())
				&& this.getAvailabilities() == ins.getAvailabilities() && this.getOfferings() == ins.getOfferings();
	}

}// end of class
