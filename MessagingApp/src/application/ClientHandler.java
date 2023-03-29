package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;

import Requests.GetAllUsersRequestResponse;
import Requests.GetAllUsersRequest;
import Requests.BroadcastMessageRequest;
import Requests.BroadcastRequest;
import Requests.LoginRequest;
import Requests.Request;
import Requests.SetStatusRequest;
import Requests.SuccessfulLoginRequest;

public class ClientHandler implements Runnable {
	private Socket client;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	public String username;
	private ByteArrayOutputStream bos;
	private ObjectOutputStream outFromServer;
	private ObjectInputStream inFromServer;
	

	
	
	
	
	public ClientHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		in = new ObjectInputStream(client.getInputStream());
		out = new ObjectOutputStream(client.getOutputStream());
		
		// Create an ObjectOutputStream that writes to a ByteArrayOutputStream
		bos = new ByteArrayOutputStream();
		outFromServer = new ObjectOutputStream(bos);		
		inFromServer = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			
		System.out.println("New Client Connected created:");
		
	}


	public void run() {
		
		//Process incoming requests from the client
		Thread handleClient = new Thread(() -> {
			try {
				while(true) {
					Request request =  (Request) in.readObject();
					processRequest(request);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		//Process incoming requests from the server/other client handlers
		Thread handleServer = new Thread(() -> {
			try {
				while(true) {
					try {
						Request request =  (Request) inFromServer.readObject();
						System.out.println("@" + request.getType());
						processRequest(request);
					} catch (EOFException e) {
						Thread.sleep(100);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		handleClient.start();
		handleServer.start();
	
	}
	
	public void processRequest(Request request) throws Exception {
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
    		BroadcastRequest broadcastRequest = (BroadcastRequest)request;
    		//sendBroadcast(broadcastRequest);
    	
    		break;
    	case "GetAllUsersRequest":
    		System.out.println("asked for all users");
    		sendUserList();
    		break;
    		
    	case "SetStatusRequest":
    		System.out.println("ChangeStatusRequest");
    		Server.userList.put(username, ((SetStatusRequest)request).getStatus());
    		updateStatus();
    		break;
    	}
	}
	
	//create a request to send to client(s) and send it
	public void sendUserList() throws IOException {
		GetAllUsersRequestResponse response = new GetAllUsersRequestResponse(Server.userList);
		out.writeObject(response);
		out.flush();
		
	}

	//push updated list to all connected clients
	private void updateStatus() throws IOException {
		
		for (int i=0; i<Server.clientList.size(); i++) {
	
			GetAllUsersRequest rq = new GetAllUsersRequest();
			Server.clientList.get(i).serverRequest(rq);
			
		}
	}

	//The client handler OR server can use this method to spoof a request coming from a client
	private void serverRequest(Request rq) throws IOException {
		outFromServer.writeObject(rq);//this writes the request object to the server-clienthandler out stream
		
		//This generates the input stream from the above output stream, with a bytearraystream chained in between
		inFromServer = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		
		//wipe stream
		bos = new ByteArrayOutputStream();
		outFromServer = new ObjectOutputStream(bos);
	}


	public void sendResponse() throws IOException {
		Request response = new Request("Youre request has been sorted");
		out.writeObject(response);
		
	}
	public void attemptLogin(LoginRequest loginRequest) throws Exception {

		System.out.println("attempting login");
		
		//check if user exists
		if(Server.loginCredentials.get(loginRequest.getUsername()) == null){
			//if user does not exist, return false object
			System.out.println("attempting login invalid user");
			out.writeObject(new SuccessfulLoginRequest(loginRequest.getUsername(), false,"invalid username"));
			out.flush();
			
			
			//usermatch found in hashmap, check if password matches
		} else if(Server.loginCredentials.get(loginRequest.getUsername()).contentEquals(loginRequest.getPassword())) {
			//if password match, return true
			System.out.println("attempting login success");
			out.writeObject(new SuccessfulLoginRequest(loginRequest.getUsername(), true,"login successful"));
			out.flush();
			//set username of user to handler
			this.username= loginRequest.getUsername();
			Server.userList.put(this.username, "online");//set user that just logged in to online
			updateStatus();
			System.out.println("succesfullogin packet sent");
			
		}else {
			//if password does not match, return false
			System.out.println("attempting login invalid pass");

			out.writeObject(new SuccessfulLoginRequest(loginRequest.getUsername(), false,"invalid password"));
			out.flush();
		
		}
		
		
		
	}
//	public void sendBroadcast(BroadcastRequest request) throws IOException {
//		//for ({type of obeject} {name of each item} : {list})
//		BroadcastMessageRequest message = new BroadcastMessageRequest(request.getMessage(), this.username);
//			
//		for (ClientHandler client: clientList) {
//			if(!client.username.equals(this.username)) {
//				client.out.writeObject(message);
//				client.out.reset();
//			}
//			
//		}
//		
//	}


	public ObjectOutputStream getOutFromServer() {
		return outFromServer;
	}
	

}
