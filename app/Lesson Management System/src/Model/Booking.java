package Model;

public class Booking {
	private String bookingDate;
	private boolean status;
	private Client client;
	private Offering offering;

	public Booking(String bookingDate, boolean status, Client client, Offering offering) {
		this.bookingDate = bookingDate;
		this.status = status;
		this.client = client;
		this.offering = offering;
	}

	public String getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Offering getOffering() {
		return offering;
	}

	public void setOffering(Offering offering) {
		this.offering = offering;
	}

	
	//every offering gets displayed on the client console. However, some offerings are unavailable. When the client wants to make a booking, we have to see
	//if the booking already exists (i.e. the offering is already taken)
	//would comparing their references suffice? Since when you create a booking, you create it based on an offering object.
	//every offering get's displayed on the client console. Whihc means the objects are the same no?
	public boolean exists(Offering of, Client client){
		return this.getOffering() == of && this.getClient() == client;
		//alternatively
		//return this.getOffering().equalsForComparingTwoOfferings(of) && this.getClient().equals(client);
	}

}
