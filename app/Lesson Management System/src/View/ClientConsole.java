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
	private ClientController CC;

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

	//this method is good, the one after this one is not. I don't think there should be any logic done in the console.
	//return type unsure
	public void viewOfferings(){
		//you keep a reference of this because you need it for the annotation
		ArrayList<Offering> publicOfferings = OC.viewPublicOfferings();
		int[] indexArr = CC.checkBookingAvailability(publicOfferings, this.getLoggedInClient());
		String[] annotatedOfferings = this.annotateOfferingsList(publicOfferings, indexArr);
		for(String s:annotatedOfferings ){
			System.out.println(s);
		}

	}

	//im against this, i think the console shouldn't have any logic. So maybe we just move this whole thing to the offering controller instead?
	public String[] annotateOfferingsList(ArrayList<Offering> publicOfferingsList, int[] indexOfbookedOffers){
		String[] annotatedOfferingList = new String[publicOfferingsList.size()];
		int bookingsIteration = 0;
		//for each offering in the publicOfferingList
		for (int i = 0; i<publicOfferingsList.size(); i++){
			Offering of = publicOfferingsList.get(i);
			if (of.getCapacity() == 0 || indexOfbookedOffers[bookingsIteration] == i){
				annotatedOfferingList[i] = of.toString() + " [unavailable]";
				bookingsIteration++;
			}
			else{
				annotatedOfferingList[i] = of.toString();
			}
		}
		return annotatedOfferingList;
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
				BC.createBooking(of, this.getLoggedInClient() );
			}//end of inner if statement
		}

	}
}
