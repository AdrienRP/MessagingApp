package application;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import Requests.GetConversationsRequestResponse;
import Requests.GetConversationsRequest;
import Requests.SetStatusRequest;
import Requests.GetAllUsersRequestResponse;
import Requests.GetAllUsersRequest;
import Requests.ConversationUpdateRequest;
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
	private ByteArrayOutputStream bos;
	private ObjectOutputStream outFromServer;
	private ObjectInputStream inFromServer;
	public static ArrayList<ClientHandler> clientList = new ArrayList<>();
	public String username;
	private String status;

	
	
	
	
	public ClientHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		this.in = new ObjectInputStream(client.getInputStream());
		this.out = new ObjectOutputStream(client.getOutputStream());
		
		//this.readFile = new;
		bos = new ByteArrayOutputStream();
		outFromServer = new ObjectOutputStream(bos);
		inFromServer = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		
		
		clientList.add(this);
		//initiailize status to online once connected 
		this.status="Online";
		System.out.println("New Client Connected created:");
		
	}


	public void run() {
		
		//incoming requests from the client
		Thread handleClient = new Thread(() -> {
			try {
				while(true) {
					try {
						Request request = (Request) in.readObject();
						processRequest(request);
					} catch (EOFException e) {
						Thread.sleep(100);
					} catch (SocketException e) {
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		handleClient.start();
		
		//incoming requests from server or other client handlers(other threads)
		Thread handleServer = new Thread(() -> {
			try {
				while(true) {
					try {
						Request request = (Request) inFromServer.readObject();
						processRequest(request);
					} catch (EOFException e) {
						Thread.sleep(100);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		handleServer.start();		
	
	}
	public void closeClient() {
		clientList.remove(this);
		System.out.println(this.username + " has logged off");
		try {
			if(this.in != null) {
				this.in.close();
			}
			if(this.out != null) {
				this.out.close();
			}
			if(this.client != null) {
				this.client.close();
			}
			Server.userList.put(username, "offline");
    		updateStatus();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	public String getUsername() {
		return this.username;
	}
	
	public void processRequest(Request request) throws Exception {
		 //listen for requests
    	switch(request.getType()) {
    	case "LoginRequest":
    		System.out.println("login request");
    		LoginRequest loginRequest = (LoginRequest)request;
    		System.out.println("attempting to match credentials");
    		attemptLogin(loginRequest);
    
    		break;
    	case "ClosedClient":
    		closeClient();
    		
    		break;
    	
    	case "GetStatusRequest":
    		getStatusRequest();
    		break;
    		
    	case "SetStatusRequest":
    		Server.userList.put(username, ((SetStatusRequest) request).getStatus());
    		updateStatus();
    		break;
    	case "MessageRequest":
    		MessageRequest messageRequest = (MessageRequest) request;
    		sendMessageRequest(messageRequest);
    		break;
    		
    	case "NewConvoRequest":
    		makeNewConvo(((NewConvoRequest) request).getGroupName(),((NewConvoRequest) request).getMembers() );
    		break;
    	
    	case "GetAllUsersRequest":
    		GetAllUsersRequestResponse rq = new GetAllUsersRequestResponse(Server.userList);
    		out.writeObject(rq);
    		out.flush();
    		break;
    		
    	case "GetConversationsRequest":
    		GetConversationsRequestResponse rq1 = new GetConversationsRequestResponse(getUsersConversations());
    		out.writeObject(rq1);
    		out.flush();
    		break;
    	}	
	}
	
	private ArrayList<Conversation> getUsersConversations() {
		ArrayList<Conversation> list = new ArrayList<>();
		for(Conversation conv: Server.allConversations) {
			if(conv.getMembers().contains(username)) {
				list.add(conv);
			}
		}
		return list;
	}


	public void sendResponse() throws IOException {
		Request response = new Request("Youre request has been sorted");
		out.writeObject(response);
		
	}
	
	public void getStatusRequest() throws IOException {
		Request response = new Request("GetStatus",this.status);
		out.writeObject(response);
		
	}
	public void setStatusRequest(Request request) throws IOException {
		
		this.status=request.getMessage();
		Request response = new Request("GetStatus",this.status);
		out.writeObject(response);
		
	}
	private void updateStatus() throws IOException {
		for (int i=0; i<clientList.size(); i++) {
			GetAllUsersRequest rq = new GetAllUsersRequest();
			clientList.get(i).serverRequest(rq);
		}
		
	}
	
	//clienthandler or server can call clientlist.get(n).serverRequest(rq) to initiate  the same response as if the client sent the request
	private void serverRequest(Request rq) throws IOException {
		outFromServer.writeObject(rq);//write to this output stream, which writes to bos
		
		//use bos to fill the input stream
		inFromServer = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		
		//wipe stream
		bos = new ByteArrayOutputStream();
		outFromServer = new ObjectOutputStream(bos);
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
			Server.userList.put(username, "online");
			updateStatus();
			System.out.println("succesfullogin packet sent");
			
		}else {
			//if password does not match, return false
			System.out.println("attempting login invalid pass");

			out.writeObject(new SuccessfulLoginRequest(loginRequest.getUsername(), false,"invalid password"));
			out.flush();
		
		}
		
		
		
	}
	



	//make new conversation object, then send conversations updates to all members of the new conversation
	public void makeNewConvo(String groupName, ArrayList<String> members) throws IOException {
		new Conversation(members, groupName);
		//increase file count
		File file = new File("MessagingApp/src/application/Server.txt");
        FileWriter writer = new FileWriter(file);
        int convonum = Conversation.getConvoList().size();
        String num = Integer.toString(convonum);
        writer.write(num);
        writer.close();
        System.out.println(convonum + "number of convos");
        System.out.println("Conversations on server ="+ convonum);
		
		
		for (int i=0; i<clientList.size(); i++) {
			if (members.contains(clientList.get(i).getUsername())) {
				GetConversationsRequest rq = new GetConversationsRequest();
				clientList.get(i).serverRequest(rq);
			}
		}
		
	}
	public void sendMessageRequest(MessageRequest request) throws IOException {
        int id= request.getConversation_ID();
        //find conversation to send message to
        for(Conversation convo: Server.allConversations) {
            if (convo.getConversation_ID() == id) {
                //add message to conversation
                Message message = new Message(request.getSender(), request.getMessage());
                convo.addMessage(message);

                //save convo to database
                String location = "MessagingApp/src/application/"+Integer.toString(convo.getConversation_ID())+".txt";
                File file = new File(location);
                 ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                 oos.writeObject(convo);
                 oos.close();
                 
                 
                 //notify active users of update
                 for(ClientHandler client: clientList) {
                     if(convo.getMembers().contains(client.username)) {
                     	ConversationUpdateRequest rq = new ConversationUpdateRequest(id, message.getUser(), message.getMessages());
             			client.out.writeObject(rq);
             			client.out.flush();
                     	
                     }
                 }

                
            }

        }

    }

}
