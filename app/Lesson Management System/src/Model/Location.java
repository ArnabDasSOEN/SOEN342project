package Model;

import java.util.ArrayList;

public class Location {
	private String name;
	private String address;
	private String city;
	private TypeOfSpace typeOfSpace;
	private ArrayList<Schedule> schedules;

	public Location(String name, String address, String city, TypeOfSpace typeOfSpace, ArrayList<Schedule> schedules) {
		this.name = name;
		this.address = address;
		this.city = city;
		this.typeOfSpace = typeOfSpace;
		this.schedules = new ArrayList<Schedule>();
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

	public ArrayList<Schedule> getSchedules() {
		return schedules;
	}

	public void setSchedules(ArrayList<Schedule> schedules) {
		this.schedules = schedules;
	}

}
