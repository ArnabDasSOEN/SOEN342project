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

}
