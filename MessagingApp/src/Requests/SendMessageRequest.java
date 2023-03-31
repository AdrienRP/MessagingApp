package Requests;

public class SendMessageRequest extends Request{
	private static final long serialVersionUID = 1L;
	
	private int conversationID;
	private String message;
	
	public SendMessageRequest(int conversationID, String message) {
		super("SendMessageRequest");
		this.conversationID = conversationID;
		this.message = new String(message);
	}

}
