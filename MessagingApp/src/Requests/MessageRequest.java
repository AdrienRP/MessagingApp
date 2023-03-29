package Requests;

public class MessageRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sender;
	private String recipient;
	private String message;
	

	public MessageRequest(String sender, String recipient, String message) {
		super("MessageRequest");
		this.sender=sender;
		this.recipient=recipient;
		this.message=message;

	}
	public String getSender() {
		return this.sender;
	}
	public String getRecipient() {
		return this.recipient;
	}
	public String getMessage() {
		return this.message;
	}

}
