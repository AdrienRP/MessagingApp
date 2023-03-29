package application;

public class Message {
	private String user;
	private String message;
	private boolean deleted;

	public Message(String user, String message) {
		this.user = user;
		this.message = message;
		this.deleted=false;
		// TODO Auto-generated constructor stub
	}
	public String getUser() {
		return this.user;
	}
	
	public String getMessages() {
		return this.message;
	}

}
