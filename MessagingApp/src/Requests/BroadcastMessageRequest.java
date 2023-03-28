package Requests;

public class BroadcastMessageRequest extends Request {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private String user;
	
	public BroadcastMessageRequest(String message, String user) {
		super("BroadcastMessage");
		this.message = message;
		this.user= user;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public String getUser() {
		return this.user;
	}
}
