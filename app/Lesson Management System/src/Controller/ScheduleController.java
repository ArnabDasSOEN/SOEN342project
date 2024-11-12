package Controller;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import Model.Schedule;
import Model.Location;
import Database.ScheduleDAO;

public class ScheduleController {
    private static ScheduleController SCinstance;
    private ScheduleDAO scheduleDAO;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private ScheduleController() {
        this.scheduleDAO = new ScheduleDAO();
    }

    public static ScheduleController getInstance() {
        if (SCinstance == null) {
            SCinstance = new ScheduleController();
        }
        return SCinstance;
    }

    // Creates a new schedule and persists it using the DAO
    public Schedule createSchedule(String startDate, String endDate, int dayOfWeek, int startTime, int endTime,
                                   Location location) {
        lock.writeLock().lock(); // Acquire write lock
        try {
            Schedule newSchedule = new Schedule(startDate, endDate, dayOfWeek, startTime, endTime);
            location.addSchedule(newSchedule); // Associate schedule with the location
            scheduleDAO.addSchedule(newSchedule, location); // Persist in the database
            return newSchedule;
        } finally {
            lock.writeLock().unlock(); // Release write lock
        }
    }

    // Retrieves schedules for a location from the database
    public ArrayList<Schedule> getSchedulesForLocation(Location location) {
        lock.readLock().lock(); // Acquire read lock
        try {
            return scheduleDAO.getSchedulesForLocation(location.getId()); // Get schedules via DAO
        } finally {
            lock.readLock().unlock(); // Release read lock
        }
    }
}
