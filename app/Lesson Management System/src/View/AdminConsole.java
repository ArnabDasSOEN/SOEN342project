package View;

import Controller.OfferingController;
import Controller.InstructorController;
import Controller.BookingController;
import Controller.LocationController;
import Controller.ScheduleController;
import Controller.ClientController;
import Model.Offering;
import Model.Schedule;
import Model.Instructor;
import Model.LessonType;
import Model.Location;

public class AdminConsole {

	private BookingController BC;
	private ClientController CC;
	private InstructorController IC;
	private LocationController LC;
	private OfferingController OC;
	private ScheduleController SC;

	public BookingController getBC() {
		return BC;
	}

	public void setBC(BookingController bC) {
		BC = bC;
	}

	public ClientController getCC() {
		return CC;
	}

	public void setCC(ClientController cC) {
		CC = cC;
	}

	public InstructorController getIC() {
		return IC;
	}

	public void setIC(InstructorController iC) {
		IC = iC;
	}

	public LocationController getLC() {
		return LC;
	}

	public void setLC(LocationController lC) {
		LC = lC;
	}

	public OfferingController getOC() {
		return OC;
	}

	public void setOC(OfferingController oC) {
		OC = oC;
	}

	public ScheduleController getSC() {
		return SC;
	}

	public void setSC(ScheduleController sC) {
		SC = sC;
	}

	public boolean requestCreateOffering(Location L, Schedule S, int startTime, int endTime, boolean isGroup,
			boolean availability, int capacity, LessonType lessonType) {
		return getOC().createOffering(L, S, startTime, endTime, isGroup, availability, capacity, lessonType);
	}

}
