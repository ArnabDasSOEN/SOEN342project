package View;

import Controller.ClientController;
import Model.Client;

public class ClientConsole {
	private Client loggedInClient;

	public Client getLoggedInClient() {
		return loggedInClient;
	}

	public void setLoggedInClient(Client loggedInClient) {
		this.loggedInClient = loggedInClient;
	}

	private boolean isLoggedIn() {

		return getLoggedInClient() == null;
	}

	public boolean logIn(String name, String phoneNumber) {

		ClientController CController = new ClientController();
		setLoggedInClient(CController.login(name, phoneNumber));

		return isLoggedIn();

	}

}
