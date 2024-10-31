package Model;

import java.util.ArrayList;

public class Schedule {
	private String startDate;
	private String endDate;
	private int dayOfWeek;
	private int startTime; // Changed to int
	private int endTime; // Changed to int
	private ArrayList<Offering> offerings;

	public Schedule(String startDate, String endDate, int dayOfWeek, int startTime, int endTime) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.dayOfWeek = dayOfWeek;
		this.startTime = startTime;
		this.endTime = endTime;
		this.offerings = new ArrayList<Offering>();
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

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public ArrayList<Offering> getOfferings() {
		return offerings;
	}

	public void setOfferings(ArrayList<Offering> offerings) {
		this.offerings = offerings;
	}

	public void addOffering(Offering newOffering) {
		this.offerings.add(newOffering);
	}

	@Override
	public String toString() {
		String dayName = getDayName(dayOfWeek); // Convert dayOfWeek to day name
		return "The schedule is available for classes on " + dayName + " from " + startTime + " to " + endTime
				+ " from " + startDate + " to " + endDate + ".";
	}

	// Helper method to convert day of week integer to string
	private String getDayName(int dayOfWeek) {
		switch (dayOfWeek) {
		case 1:
			return "Monday";
		case 2:
			return "Tuesday";
		case 3:
			return "Wednesday";
		case 4:
			return "Thursday";
		case 5:
			return "Friday";
		case 6:
			return "Saturday";
		case 7:
			return "Sunday";
		default:
			return "Unknown day"; // In case of invalid input
		}
	}

	public boolean equals(Schedule sc){
		return this.getStartDate().equalsIgnoreCase(sc.getStartDate()) && this.getEndDate().equalsIgnoreCase(sc.getEndDate()) && this.getDayOfWeek() == sc.getDayOfWeek()
		&& this.getStartTime() == sc.getStartTime() && this.getEndTime() == sc.getEndTime() && this.getOfferings() == sc.getOfferings();
	}


}