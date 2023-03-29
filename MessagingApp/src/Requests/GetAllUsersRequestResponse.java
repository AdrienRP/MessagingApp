package Requests;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class GetAllUsersRequestResponse extends Request {
	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<String,String> userList;
	
	public GetAllUsersRequestResponse(ConcurrentHashMap<String, String> userList) {
		super("GetAllUsersRequestResponse");
		this.userList = new ConcurrentHashMap<>(userList);
	}
	
	public ConcurrentHashMap<String,String> getUserList() {
		return userList;
	}
}
