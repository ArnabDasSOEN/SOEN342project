package Model;

import java.util.ArrayList;

public class Location {
	private int id; // Unique identifier
	private String name;
	private String address;
	private String city;
	private TypeOfSpace typeOfSpace;
	private ArrayList<Schedule> schedules;

	public Location(String name, String address, String city, TypeOfSpace typeOfSpace) {
		this.id = 0;
		this.name = name;
		this.address = address;
		this.city = city;
		this.typeOfSpace = typeOfSpace;
		this.schedules = new ArrayList<>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public TypeOfSpace getTypeOfSpace() {
		return typeOfSpace;
	}

	public void setTypeOfSpace(TypeOfSpace typeOfSpace) {
		this.typeOfSpace = typeOfSpace;
	}

	public void setSchedules(ArrayList<Schedule> schedules) {
		this.schedules = schedules;
	}

	public void addSchedule(Schedule schedule) {
		this.schedules.add(schedule);
	}

	public ArrayList<Schedule> getSchedules() {
		return schedules;
	}

	public boolean equals(Location L) {
		return this.getName().equals(L.getName()) && this.getAddress().equals(L.getAddress()) && this.getCity().equals(L.getCity());
	}

	//i believe we're able to get away with just comparing the "references" since the compiler is smart enough to know that when we're comparing 2 strings, we're comparing their contents.
	public boolean equals(String name, String address, String City) {
		return this.getName().equals(name) && this.getAddress().equals(address) && this.getCity().equals(City);
	}

	@Override
	public String toString() {
		return "Location Name: " + name + ", Address: " + address + ", City: " + city + ", Type of Space: "
				+ typeOfSpace;
	}
}
