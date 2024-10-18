package Controller;

import java.util.ArrayList;

import Model.Client;

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
}
