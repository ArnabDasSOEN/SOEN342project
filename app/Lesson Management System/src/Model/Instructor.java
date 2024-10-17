package Model;

import java.util.ArrayList;

public class Instructor {
private LessonType specialization;
private String name;
private String phoneNumber;
private String startDate;
private String endDate;
private ArrayList<String> cities;
private ArrayList<Offering> offerings;

public Instructor(String name, String phoneNumber, String startDate, String endDate) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.startDate = startDate;
    this.endDate = endDate;
}

public LessonType getSpecialization() {
    return specialization;
}

public void setSpecialization(LessonType specialization) {
    this.specialization = specialization;
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

public ArrayList<String> getCities() {
    return cities;
}

public void setCities(ArrayList<String> cities) {
    this.cities = cities;
}

public ArrayList<Offering> getOfferings() {
    return offerings;
}

public void setOfferings(ArrayList<Offering> offerings) {
    this.offerings = offerings;
}


}
