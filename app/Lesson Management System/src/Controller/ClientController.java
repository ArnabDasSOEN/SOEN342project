package Controller;

import java.util.ArrayList;

import Model.Booking;
import Model.Client;
import Model.Offering;

public class ClientController {
	private static ClientController CCinstance; // Singleton instance
	private ArrayList<Client> clientCollection;

	// Private constructor to prevent instantiation
	private ClientController() {
		this.clientCollection = new ArrayList<Client>();
		register("John Doe", "123-456-7890", 30);
		register("Jane Smith", "987-654-3210", 25);
	}

	// Method to get the singleton instance
	public static ClientController getInstance() {
		if (CCinstance == null) {
			CCinstance = new ClientController();
		}
		return CCinstance;
	}

	public Client login(String name, String phoneNumber) {
		for (Client client : this.clientCollection) {
			if (client.clientExists(name, phoneNumber)) {
				return client;
			}
		}
		return null;
	}

	public boolean register(String name, String phoneNumber, int age) {
		for (Client client : this.clientCollection) {
			if (client.clientExists(name, phoneNumber)) {
				System.out.println("Client already exists: " + name + ", " + phoneNumber);
				return false;
			}
		}
		Client newClient = new Client(name, phoneNumber, age);
		add(newClient);
		System.out.println("Successfully registered new client: " + name); // More informative message
		return true;
	}

	private void add(Client client) {
		this.clientCollection.add(client);
	}


	public int[] checkBookingAvailability(ArrayList<Offering> publicOfferings, Client c){
		
		ArrayList<Booking> clientBookings = c.getBookings();
		int bookingCount = clientBookings.size();
		
		if (bookingCount == 0){
			return new int[0];
		}
		else if(bookingCount < 0){
			//i am not handling this error through an exception, just going to return an array with size 0
			return new int[0];
		}
		else {
			//the values of this array corresponds to the indices in the publicOfferings list in which the client already booked for (hence it needs to be annotated)
			int[] indexArr = new int[bookingCount];
			int counterForIndexArr = 0;
			int offeringListIterationIndex = 0; //the current index of the offering being looked at in the publicOfferings list (param)

			//for each offering, loop through each booking of the client
			for (Offering of: publicOfferings){
					if(c.checkBookings(of)){ //if you find a matching offering inside one of the client's bookings then that means that offering needs to be annotated. Hence, mark it
						indexArr[counterForIndexArr] = offeringListIterationIndex;
						counterForIndexArr++;
					}
					offeringListIterationIndex++;
			}
			return indexArr;
		}
	}


}
