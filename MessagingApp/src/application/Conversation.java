package application;

import java.util.ArrayList;
import java.util.Collections;

public class Conversation {
	private int conversation_ID;
	private ArrayList<String> members;
	private String conversationType;
	private ArrayList<Message> messages;
	private String groupName;
	public static ArrayList<Conversation> convoList = new ArrayList<>();
	private boolean unique;
	
	

	public Conversation(ArrayList<String> members, String groupName) {
		
		//check if conversation between individuals exists
		//sort Members for matching purposes
		ArrayList<String> names = (ArrayList<String>) members.clone();
		Collections.sort(names);
		unique = true;
		
		for(Conversation convo: Conversation.convoList) {
			if(convo.equals(names)) {
				//conversation already exists
				unique= false;
			}
		}
		
		if(unique = true) {
			// set group name if more than 2 members
			if (members.size() <=2) {
				this.conversationType = "Individual";
			}
			else {
				this.conversationType = "Group";
				this.groupName = groupName;
			}
			//add group members
			this.members = members;
			
			//give conversation an ID and add to static list
			this.conversation_ID = Conversation.convoList.size();
			convoList.add(this);

		}
		else {
			System.out.println("Conversation already exists");
		}
		
		
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
		return this.groupName;
	}
	public static ArrayList<Conversation> getConvoList(){
		return Conversation.convoList;
	}
	public boolean isUnique() {
		return this.isUnique();
	}
}
