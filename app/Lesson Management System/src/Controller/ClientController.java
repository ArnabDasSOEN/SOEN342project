package Controller;

import Database.ClientDAO;
import Model.Booking;
import Model.Client;
import Model.Offering;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClientController {
    private static ClientController CCinstance;
    private ArrayList<Client> clientCollection;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ClientDAO clientDAO;

    private ClientController() {
        this.clientCollection = new ArrayList<>();
        this.clientDAO = new ClientDAO();
        loadClientsFromDatabase();
    }

    public static ClientController getInstance() {
        if (CCinstance == null) {
            CCinstance = new ClientController();
        }
        return CCinstance;
    }

    private void loadClientsFromDatabase() {
        lock.writeLock().lock();
        try {
            clientCollection = clientDAO.getAllClients();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Client login(String name, String phoneNumber) {
        lock.readLock().lock();
        try {
            for (Client client : clientCollection) {
                if (client.clientExists(name, phoneNumber)) {
                    return client;
                }
            }
            return clientDAO.getClientByNameAndPhone(name, phoneNumber);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean register(String name, String phoneNumber, int age) {
        lock.writeLock().lock();
        try {
            if (clientDAO.clientExists(name, phoneNumber)) {
                System.out.println("Client already exists: " + name + ", " + phoneNumber);
                return false;
            }
            Client newClient = new Client(name, phoneNumber, age);
            clientDAO.addClient(newClient);
            clientCollection.add(newClient);
            System.out.println("Successfully registered new client: " + name);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean register(String name, String phoneNumber, int age, Client guardian) {
        lock.writeLock().lock();
        try {
            if (clientDAO.clientExists(name, phoneNumber)) {
                System.out.println("Client already exists: " + name + ", " + phoneNumber);
                return false;
            }
            if (guardian.getAge() < 18) {
                System.out.println("Guardian must be an adult.");
                return false;
            }

            Client minorClient = new Client(name, phoneNumber, age);
            minorClient.setGuardian(guardian);
            clientDAO.addClientWithGuardian(minorClient, guardian);
            clientCollection.add(minorClient);
            System.out.println("Successfully registered minor client under guardian: " + guardian.getName());
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int[] checkBookingAvailability(ArrayList<Offering> publicOfferings, Client client) {
        lock.readLock().lock();
        try {
            ArrayList<Booking> clientBookings = client.getBookings();
            int bookingCount = clientBookings.size();

            if (bookingCount == 0) {
                return new int[0];
            }

            int[] indexArr = new int[bookingCount];
            int counterForIndexArr = 0;
            int offeringListIterationIndex = 0;

            for (Offering offering : publicOfferings) {
                if (client.checkBookings(offering)) {
                    indexArr[counterForIndexArr] = offeringListIterationIndex;
                    counterForIndexArr++;
                }
                offeringListIterationIndex++;
            }
            return indexArr;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Client> getAllClients() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(clientCollection);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Client getClientByNameAndPhone(String clientName, String clientPhone) {
        lock.readLock().lock();
        try {
            for (Client client : clientCollection) {
                if (client.getName().equals(clientName) && client.getPhoneNumber().equals(clientPhone)) {
                    return client;
                }
            }
            return clientDAO.getClientByNameAndPhone(clientName, clientPhone);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void deleteClient(Client client) {
        lock.writeLock().lock();
        try {
            if (clientCollection.contains(client)) {
                clientCollection.remove(client);
                clientDAO.deleteClient(client);
                System.out.println("Client deleted successfully: " + client.getName());
            } else {
                System.out.println("Client not found in the system: " + client.getName());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
