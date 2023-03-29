package Requests;

import java.util.AbstractList;
import java.util.ArrayList;

public class NewConvoRequest extends Request{
	
	private ArrayList<String> members;
	private String groupName;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NewConvoRequest(ArrayList<String> members) {
		super("NewConvoRequest");
		for (String user : members) {
			this.members.add(user);
		}
		this.groupName=null;
			
	}
	
		
	public NewConvoRequest(ArrayList<String> members, String groupName) {
		super("NewConvoRequest");
		for (String user : members) {
			this.members.add(user);
		}
		this.groupName=groupName;

	}
	
	public ArrayList<String> getMembers() {
		return this.members;
	}
	public String getGroupName() {
		return this.groupName;
	}

}
