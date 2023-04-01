package application;
import java.io.Serializable;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	public boolean isDeleted() {
		return deleted;
	}
	
	public void delete() {
		deleted = true;
	}

}
