package application;

import java.util.ArrayList;

public class User {
	private String username;
	private ArrayList<Integer> userChats;

	public User(String username, String password, ArrayList<Integer> userChats) {
		this.username=username;
		this.userChats=userChats;
		
		// TODO Auto-generated constructor stub
	}
	
	public String getUsername() {
		return this.username;
		
	}

	public ArrayList<Integer> getUserChats(){
		return this.userChats;
	}

}
