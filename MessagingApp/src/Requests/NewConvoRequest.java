package Requests;

import java.util.ArrayList;

public class NewConvoRequest extends Request{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<String> members = new ArrayList<>();;
	private String groupName;

	public NewConvoRequest(ArrayList<String> members) {
		super("NewConvoRequest");
		for (String user : members) {
			this.members.add(new String(user));
		}
		this.groupName=null;
			
	}
	
		
	public NewConvoRequest(ArrayList<String> members, String groupName) {
		super("NewConvoRequest");
		for (String user : members) {
			this.members.add(new String(user));
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
