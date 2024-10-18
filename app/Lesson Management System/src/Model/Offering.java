package Model;

import java.util.ArrayList;

public class Offering {
	private LessonType lessonType;
	private boolean isGroup;
	private boolean availability;
	private int capacity;
	private int startTime;
	private int endTime;
	private Schedule schedule;
	private Instructor instructor;
	private ArrayList<Booking> bookings;
	private Location location;

	public Offering(LessonType lessonType, boolean isGroup, boolean availability, int capacity, int startTime,
			int endTime, Schedule schedule, Location location) {
		this.lessonType = lessonType;
		this.isGroup = isGroup;
		this.availability = availability;
		this.capacity = capacity;
		this.startTime = startTime;
		this.endTime = endTime;
		this.schedule = schedule;
		this.instructor = null;
		this.bookings = new ArrayList<Booking>();
		this.location = location;
	}

	public LessonType getLessonType() {
		return lessonType;
	}

	public void setLessonType(LessonType lessonType) {
		this.lessonType = lessonType;
	}

	public boolean isGroup() {
		return isGroup;
	}

	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	public boolean isAvailability() {
		return availability;
	}

	public void setAvailability(boolean availability) {
		this.availability = availability;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
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

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Instructor getInstructor() {
		return instructor;
	}

	public void setInstructor(Instructor instructor) {
		this.instructor = instructor;
	}

	public ArrayList<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(ArrayList<Booking> bookings) {
		this.bookings = bookings;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean hasInstructor() {
		return this.instructor == null;
	}

	public boolean equals(Location l, Schedule s, int startTime, int endTime) {
		// Check if the location and schedule match
		boolean locationAndScheduleMatch = getLocation().equals(l) && getSchedule().getDayOfWeek() == s.getDayOfWeek();

		// Check if the time slots overlap
		boolean timeOverlap = startTime < getEndTime() && endTime > getStartTime();

		// Return true only if both the location and schedule match, and there is a time
		// overlap
		return locationAndScheduleMatch && timeOverlap;
	}

	// for finding potential offerings, the instructor only cares about the location
	// and the type of the lesson.
	public boolean equalsforFindingOfferings(String city, LessonType lstype) { // Location loc
		return getLocation().getCity() == city && getLessonType() == lstype;
	}
}
