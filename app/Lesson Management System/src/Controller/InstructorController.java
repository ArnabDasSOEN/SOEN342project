package Controller;
import java.util.ArrayList;

import Model.Instructor;

public class InstructorController {
    private Instructor loggedInstructor;
    private ArrayList<Instructor> instructorCollection;

    public Instructor getInstructor(){
        return this.loggedInstructor;
    }

}
