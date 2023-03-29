package Requests;

import java.util.AbstractList;
import java.util.ArrayList;

public class NewConvoRequest extends Request{
	private int convoID;
	private ArrayList<String> members;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NewConvoRequest(ArrayList<String> members) {
		super("NewConvoRequest");
		for (String user : members) {
			this.members.add(user);
		}
		
		// TODO Auto-generated constructor stub
	}

}
