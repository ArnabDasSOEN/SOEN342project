package Model;

import java.util.ArrayList;

public class Schedule {

    private String startDate;
    private String endDate;
    private int dayOfWeek;
    private String startTime;
    private String endTime;
    private ArrayList<Offering> offerings;
    
    
    public Schedule(String startDate, String endDate, int dayOfWeek, String startTime, String endTime,
            ArrayList<Offering> offerings) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.offerings = offerings;
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
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public ArrayList<Offering> getOfferings() {
        return offerings;
    }
    public void setOfferings(ArrayList<Offering> offerings) {
        this.offerings = offerings;
    }
    

}
