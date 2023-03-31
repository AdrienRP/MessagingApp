package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Conversation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int conversation_ID;
	private ArrayList<String> members;
	private String conversationType;
	private ArrayList<Message> messages = new ArrayList<>();
	private String groupName;
	public static ArrayList<Conversation> convoList = new ArrayList<>();
	private boolean unread = false;
	
	
	

	public Conversation(ArrayList<String> members, String groupName) {
		
		//check if conversation between individuals exists
		//sort Members for matching purposes
		this.members = new ArrayList<>();
		for(String name: members) {
			System.out.println("addmember " + name);
			this.members.add(new String(name));
		}
		Collections.sort(this.members);
		
		// set group name if more than 2 members
		if (members.size() <=2) {
			this.conversationType = "Individual";
		}
		else {
			this.conversationType = "Group";
		}
		
		this.groupName = new String(groupName);
		
		//add group members
		this.members = members;
		
		//give conversation an ID and add to static list
		this.conversation_ID = Conversation.convoList.size();
		convoList.add(this);
		Server.allConversations.add(this);
		
	}
	public int getConversation_ID() {
		return this.conversation_ID;
	}
	
	public ArrayList<String> getMembers(){
		return this.members;
	}
	public String getConversationType() {
		return this.conversationType;
	}
	public ArrayList<Message> getMessages(){
		return this.messages;
	}
	public String getGroupName() {
		return unread? ("*" + this.groupName):this.groupName;
	}
	public static ArrayList<Conversation> getConvoList(){
		return Conversation.convoList;
	}
	public boolean isUnique() {
		return this.isUnique();
	}
	public int getConversationID() {
		return this.conversation_ID;
	}
	
	public void addMessage(Message message) {
		this.messages.add(message);
		
	}
	
	public void setUnread() {
		this.unread = true;
	}
	
	public void setRead() {
		this.unread = false;
	}
	
	public boolean isUnread() {
		return this.unread;
	}
}
