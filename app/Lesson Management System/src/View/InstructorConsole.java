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


	public void takeOffering(Offering offering){
		if(OC.available(offering)){ //if the offering is available (i.e. it has no instructor)
			offering.setInstructor(IC.getInstructor()); //IC.getInstructor returns logged in instructor. Then we assign to respective offering.
			IC.getInstructor().assignOffering(offering);// assign offering to the instructor logged in through the console
		}
		else{
			//error message? success message? we implement or no?
		}
	}
}
