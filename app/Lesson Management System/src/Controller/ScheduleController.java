package Controller;

import java.util.ArrayList;
import Model.Schedule;
import Model.Location;

public class ScheduleController {
	private static ScheduleController SCinstance;

	private ScheduleController() {
		// No need for a separate schedule collection here
	}

	public static ScheduleController getInstance() {
		if (SCinstance == null) {
			SCinstance = new ScheduleController();
		}
		return SCinstance;
	}

	public Schedule createSchedule(String startDate, String endDate, int dayOfWeek, int startTime, int endTime,
			Location location) {
		Schedule newSchedule = new Schedule(startDate, endDate, dayOfWeek, startTime, endTime);
		location.addSchedule(newSchedule); // Associate schedule with the location
		return newSchedule;
	}

	public ArrayList<Schedule> getSchedulesForLocation(Location location) {
		return location.getSchedules(); // Get schedules directly from the location
	}
}
