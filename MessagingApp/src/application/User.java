package application;

import java.util.ArrayList;

public class User {
	private String username;
	private String password;
	private ArrayList<Integer> userChats;

	public User(String username, String password, ArrayList<Integer> userChats) {
		this.username=username;
		this.password = password;
		this.userChats=userChats;
		
		// TODO Auto-generated constructor stub
	}
	
	public String getUsername() {
		return this.username;
		
	}
	public String getPassword() {
		return this.password;
	}
	public ArrayList<Integer> getUserChats(){
		return this.userChats;
	}

}
