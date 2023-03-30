package Requests;

import java.util.ArrayList;

import application.Conversation;

public class GetConversationsRequestResponse extends Request{
	private static final long serialVersionUID = 1L;
	public ArrayList<Conversation> conversations = new ArrayList<>();
	
	public GetConversationsRequestResponse(ArrayList<Conversation> conversations) {
		super("GetConversationsRequestResponse");
		this.conversations = new ArrayList<>(conversations);
	}

	public ArrayList<Conversation> getConversations() {
		return conversations;
	}
	
	
}
