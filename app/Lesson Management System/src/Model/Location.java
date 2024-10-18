package Model;

import java.util.ArrayList;

public class Location {
	private String name;
	private String address;
	private String city;
	private TypeOfSpace typeOfSpace;
	private ArrayList<Schedule> schedules;

	public Location(String name, String address, String city, TypeOfSpace typeOfSpace) {
		this.name = name;
		this.address = address;
		this.city = city;
		this.typeOfSpace = typeOfSpace;
		this.schedules = new ArrayList<>();
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

	public boolean equals(String name, String address, String city) {
		return getName().equals(name) && getAddress().equals(address) && getCity().equals(city);
	}

	@Override
	public String toString() {
		return "Location Name: " + name + ", Address: " + address + ", City: " + city + ", Type of Space: "
				+ typeOfSpace;
	}
}
