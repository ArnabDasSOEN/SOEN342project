package View;

import java.util.ArrayList;
import Controller.OfferingController;
import Controller.InstructorController;
import Model.Offering;
import Model.Instructor;

public class InstructorConsole {

	private OfferingController OC;
	private InstructorController IC;

	public ArrayList<Offering> viewPotentialOfferings() {
		Instructor instructor = IC.getInstructor();
		return OC.findPotentialOfferings(instructor.getCities(), instructor.getSpecialization());
	}

}
