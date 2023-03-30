package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Requests.BroadcastMessageRequest;
import Requests.BroadcastRequest;
import Requests.LoginRequest;
import Requests.MessageRequest;
import Requests.NewConvoRequest;
import Requests.Request;
import Requests.SuccessfulLoginRequest;

public class ClientHandler implements Runnable {
	private Socket client;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private ObjectInputStream readFile;
	private ObjectOutputStream writeFile;
	public static ArrayList<ClientHandler> clientList = new ArrayList<>();
	public String username;
	private String status;

	
	
	
	
	public ClientHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		this.in = new ObjectInputStream(client.getInputStream());
		this.out = new ObjectOutputStream(client.getOutputStream());
		
		//this.readFile = new;
		
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
		    		break;
		    		
		    	case "NewConvoRequest":
		    		NewConvoRequest newConvoRequest = (NewConvoRequest) request;
		    		newConvoRequest(newConvoRequest);
		    	}
		    	
		    		

		    	
		    }
		} catch(Exception ex)
		{
		    //EOF found
		}
	
	}
	public String getUsername() {
		return this.username;
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
	public void newConvoRequest(NewConvoRequest request) throws IOException {
		Conversation conversation = new Conversation(request.getMembers(), request.getGroupName());
		//if conversation successfully created
		if(conversation.isUnique()) {
			//update server.txt for count on server reboot
		    String str = Integer.toString( Conversation.convoList.size());
		    BufferedWriter writer = new BufferedWriter(new FileWriter("MessagingApp/src/application/Server.txt"));
		    writer.write(str);
		    writer.close();
			//add conversation to each user in conversation.
		    ArrayList<String> members = conversation.getMembers();
		    Request newConversation = new Request("NewConversationRequest",Integer.toString(conversation.getConversation_ID()));
		    for(String member: members) {
			   //output conversation to user
		    	for(ClientHandler client: clientList) {
		    		if(members.contains(client.getUsername())){
		    			out.writeObject(newConversation);
		    			out.flush();
		    			break;
		    		}
		    	}
		    	
		   }	
			
		}
	}
	public void sendMessageRequest(MessageRequest request) throws IOException {
		int id= request.getConversation_ID();
		for(Conversation convo: Conversation.convoList) {
			if (convo.getConversation_ID() == id) {
				Message message = new Message(request.getSender(), request.getMessage());
				convo.addMessage(message);
				for(String member: convo.getMembers()) {
					out.writeObject(new Request("NewMessage", Integer.toString(convo.getConversation_ID())));
	    			out.flush();
	    			
					
				}
			}
			
		}
		
	}

}
