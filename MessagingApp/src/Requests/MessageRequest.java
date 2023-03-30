package Requests;

public class MessageRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sender;
	private int conversation_ID;
	private String message;
	

	public MessageRequest(String sender, int conversation_ID, String message) {
		super("MessageRequest");
		this.sender=sender;
		this.conversation_ID = conversation_ID;
		this.message=message;

	}
	public String getSender() {
		return this.sender;
	}
	public int getConversation_ID() {
		return this.conversation_ID;
	}
	public String getMessage() {
		return this.message;
	}

}
