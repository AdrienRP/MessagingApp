package application;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

public class Conversation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int conversation_ID;
	private ArrayList<String> members;
	private String conversationType;
	private ArrayList<Message> messages;
	private String groupName;
	private boolean unique;
	public static ArrayList<Conversation> convoList = new ArrayList<>();
	
	
	

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
	
	 public static List<String> getAllConversationsForUser(String username) {
	        List<String> conversationNames = new ArrayList<>();

	        // Read the conversations.txt file
	        try (BufferedReader br = new BufferedReader(new FileReader("/MessagingApp/src/conversations.txt"))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] parts = line.split(";");
	                String conversationName = parts[0];
	                List<String> usersInConversation = Arrays.asList(parts[1].split(","));

	                if (usersInConversation.contains(username)) {
	                    conversationNames.add(conversationName);
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return conversationNames;
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
	public int getConversationID() {
		return this.conversation_ID;
	}
	
	public void addMessage(Message message) {
		this.messages.add(message);
		
	}
}
