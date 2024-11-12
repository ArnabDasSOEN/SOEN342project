package Controller;

import Database.InstructorDAO;
import Model.Instructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InstructorController {
    private Instructor loggedInstructor;
    private ArrayList<Instructor> instructorCollection;
    private static InstructorController ICinstance;
    private InstructorDAO instructorDAO;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private InstructorController() {
        this.instructorDAO = new InstructorDAO();
        lock.writeLock().lock();
        try {
            this.instructorCollection = instructorDAO.getAllInstructors();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static InstructorController getInstance() {
        if (ICinstance == null) {
            ICinstance = new InstructorController();
        }
        return ICinstance;
    }

    public Instructor getInstructor() {
        lock.readLock().lock();
        try {
            return this.loggedInstructor;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Instructor login(String name, String phoneNumber) {
        lock.readLock().lock();
        try {
            for (Instructor instructor : instructorCollection) {
                if (instructor.getName().equals(name) && instructor.getPhoneNumber().equals(phoneNumber)) {
                    this.loggedInstructor = instructor;
                    return instructor;
                }
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            Instructor instructorFromDB = instructorDAO.getInstructorByNameAndPhone(name, phoneNumber);
            if (instructorFromDB != null) {
                instructorCollection.add(instructorFromDB);
                this.loggedInstructor = instructorFromDB;
            }
            return instructorFromDB;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean register(String name, String phoneNumber, String startDate, String endDate, String specialization) {
        lock.writeLock().lock();
        try {
            if (instructorDAO.instructorExists(name, phoneNumber)) {
                System.out.println("Instructor already exists: " + name + ", " + phoneNumber);
                return false;
            }

            Instructor newInstructor = new Instructor(name, phoneNumber, startDate, endDate);
            newInstructor.setSpecialization(specialization);
            instructorDAO.addInstructor(newInstructor);
            instructorCollection.add(newInstructor);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Instructor> getAllInstructors() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(instructorCollection);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Instructor getInstructorByNameAndPhone(String instructorName, String instructorPhone) {
        lock.readLock().lock();
        try {
            for (Instructor instructor : instructorCollection) {
                if (instructor.getName().equals(instructorName) && instructor.getPhoneNumber().equals(instructorPhone)) {
                    return instructor;
                }
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            Instructor instructorFromDB = instructorDAO.getInstructorByNameAndPhone(instructorName, instructorPhone);
            if (instructorFromDB != null) {
                instructorCollection.add(instructorFromDB);
            }
            return instructorFromDB;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deleteInstructor(Instructor instructor) {
        lock.writeLock().lock();
        try {
            if (instructorCollection.contains(instructor)) {
                instructorCollection.remove(instructor);
                instructorDAO.deleteInstructor(instructor);
                System.out.println("Instructor deleted successfully: " + instructor.getName());
            } else {
                System.out.println("Instructor not found in the system: " + instructor.getName());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
