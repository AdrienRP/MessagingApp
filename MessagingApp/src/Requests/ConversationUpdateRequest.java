package Requests;

public class ConversationUpdateRequest extends Request{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int convo_ID;
	private String sender;

	public ConversationUpdateRequest() {
		// TODO Auto-generated constructor stub
		super("ConversationUpdateRequest");
	}
	public ConversationUpdateRequest(int convo_ID, String sender, String message) {
		// TODO Auto-generated constructor stub
		super("ConversationUpdateRequest", message);
		this.convo_ID=convo_ID;
		this.sender=sender;
		
	}
	public int getID() {
		return this.convo_ID;
	}
	
	public String getSender() {
		return this.sender;
	}


}
