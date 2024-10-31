package View;

import java.util.ArrayList;

import Controller.OfferingController;
import Controller.ClientController;
import Model.Offering;
import Model.Client;
import Controller.BookingController;



public class ClientConsole {
	private Client loggedInClient;
	private OfferingController OC;
	private BookingController BC;

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
		ClientController CController = ClientController.getInstance(); // Use singleton instance
		setLoggedInClient(CController.login(name, phoneNumber));
		return isLoggedIn();
	}

	public boolean register(String name, String phoneNumber, int age) {
		ClientController CController = ClientController.getInstance(); // Use singleton instance
		return CController.register(name, phoneNumber, age);
	}

	public ArrayList<Offering> viewOfferings(){
		return OC.viewPublicOfferings();
	}

	//2nd functionality of  process booking
	public void bookOffering(Offering of){
		if(BC.checkBookingAvailability(of, this.getLoggedInClient())){
			//do nothing since the booking is not available.
		}
		else{//booking is available
			if(OC.isFull(of)){
				//offering is full. Do nothing
			}
			else{//offering is not full
				OC.decrementCapacity(of);
				
			}//end of inner if statement
		}

	}


}
