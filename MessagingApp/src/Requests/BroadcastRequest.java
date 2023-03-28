package Requests;

public class BroadcastRequest extends Request {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;

	public BroadcastRequest(String message) {
		super("BroadcastRequest");
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	

}
