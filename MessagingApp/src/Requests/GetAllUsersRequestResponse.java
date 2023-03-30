package Requests;

import java.util.HashMap;

public class GetAllUsersRequestResponse extends Request{
	private static final long serialVersionUID = 1L;
	private HashMap<String,String> userList;
	
	public GetAllUsersRequestResponse(HashMap<String,String> userList) {
		super("GetAllUsersRequestResponse");
		this.userList = new HashMap<>(userList);
	}
	
	public HashMap<String,String> getUserList() {
		return userList;
	}
}
