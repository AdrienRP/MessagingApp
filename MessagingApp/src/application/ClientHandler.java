package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Requests.BroadcastMessageRequest;
import Requests.BroadcastRequest;
import Requests.LoginRequest;
import Requests.MessageRequest;
import Requests.Request;
import Requests.SuccessfulLoginRequest;

public class ClientHandler implements Runnable {
	private Socket client;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	public static ArrayList<ClientHandler> clientList = new ArrayList<>();
	public String username;
	private String status;

	
	
	
	
	public ClientHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		in = new ObjectInputStream(client.getInputStream());
		out = new ObjectOutputStream(client.getOutputStream());
		
		clientList.add(this);
		//initiailize status to online once connected 
		this.status="Online";
		System.out.println("New Client Connected created:");
		
	}


	public void run() {
		
		try {
		    for(Request request = (Request)in.readObject();; request = (Request)in.readObject())
		    {
		         //listen for requests
		    	switch(request.getType()) {
		    	case "LoginRequest":
		    		System.out.println("login request");
		    		LoginRequest loginRequest = (LoginRequest)request;
		    		System.out.println("attempting to match credentials");
		    		attemptLogin(loginRequest);
		    
		    		break;
		    		//listen for broadcast request
		    	case "BroadcastRequest":
		    		System.out.println("Broadcasting new messasge");
		    		BroadcastRequest broadcastRequest = (BroadcastRequest) request;
		    		sendBroadcast(broadcastRequest);
		    	
		    		break;
		    	case "GetStatusRequest":
		    		getStatusRequest();
		    		break;
		    		
		    	case "SetStatusRequest":
		    		setStatusRequest(request);
		    		break;
		    	case "MessageRequest":
		    		MessageRequest messageRequest = (MessageRequest) request;
		    		sendMessageRequest(messageRequest);
		    	}
		    	
		    		

		    	
		    }
		} catch(Exception ex)
		{
		    //EOF found
		}
	
	}
	
	public void sendResponse() throws IOException {
		Request response = new Request("Youre request has been sorted");
		out.writeObject(response);
		
	}
	
	public void getStatusRequest() throws IOException {
		Request response = new Request("GetStatus",this.status);
		out.writeObject(response);
		System.out.println("GetStatusRequest Sorted");
		
	}
	public void setStatusRequest(Request request) throws IOException {
		
		this.status=request.getMessage();
		Request response = new Request("GetStatus",this.status);
		out.writeObject(response);
		System.out.println("SetStatusRequest Sorted");
		
	}
	
	public void attemptLogin(LoginRequest loginRequest) throws Exception {

		System.out.println("attempting login");
		
		//check if user exists
		if(Server.lc.get(loginRequest.getUsername()) == null){
			//if user does not exist, return false object
			System.out.println("attempting login invalid user");
			out.writeObject(new SuccessfulLoginRequest(loginRequest.getUsername(), false,"invalid username"));
			out.flush();
			
			
			//usermatch found in hashmap, check if password matches
		} else if(Server.lc.get(loginRequest.getUsername()).contentEquals(loginRequest.getPassword())) {
			//if password match, return true
			System.out.println("attempting login success");
			out.writeObject(new SuccessfulLoginRequest(loginRequest.getUsername(), true,"login successful"));
			out.flush();
			//set username of user to handler
			this.username= loginRequest.getUsername();
			System.out.println("succesfullogin packet sent");
			
		}else {
			//if password does not match, return false
			System.out.println("attempting login invalid pass");

			out.writeObject(new SuccessfulLoginRequest(loginRequest.getUsername(), false,"invalid password"));
			out.flush();
		
		}
		
		
		
	}
	public void sendBroadcast(BroadcastRequest request) throws IOException {
		//for ({type of obeject} {name of each item} : {list})
		BroadcastMessageRequest message = new BroadcastMessageRequest(request.getMessage(), this.username);
			
		for (ClientHandler client: clientList) {
			if(!client.username.equals(this.username)) {
				client.out.writeObject(message);
				client.out.reset();
			}
			
		}
		
	}
	
	public void sendMessageRequest(MessageRequest request) {
		//handle messageRequest
		
	}

}
