package Controller;

import java.util.ArrayList;

import Model.Client;

public class ClientController {
	private ArrayList<Client> clientCollection;

	public ClientController() {
		this.clientCollection = new ArrayList<Client>();
		register("John Doe", "123-456-7890", 30);
		register("Jane Smith", "987-654-3210", 25);
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
				return false;
			}
		}
		Client newClient = new Client(name, phoneNumber, age);
		add(newClient);
		return true;

	}

	private void add(Client client) {
		this.clientCollection.add(client);
	}

}
