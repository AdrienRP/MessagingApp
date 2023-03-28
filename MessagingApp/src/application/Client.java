package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import Requests.BroadcastMessageRequest;
import Requests.BroadcastRequest;
import Requests.LoginRequest;
import Requests.Request;
import Requests.SuccessfulLoginRequest;

public class Client {
	//DATA MEMBERS
		private Socket socket;
		private ObjectOutputStream os;
		private ObjectInputStream is;
		private boolean certification = false;
		private String username;
		

	//CONSTRUCTOR
		public Client() throws UnknownHostException, IOException, ClassNotFoundException {
			//connect to server
			this.socket = new Socket("127.0.0.1", Server.PORT);

			//create input/ output streams
			
			this.os = new ObjectOutputStream(socket.getOutputStream());
			
			this.is = new ObjectInputStream(socket.getInputStream()); 


		}

	//METHODS	
		
		public void reciever() {
			//create thread
			new Thread(new Runnable() {
				//thread run method
				public void run() {
					
					try {
					    for(Request incoming = (Request)is.readObject();; incoming = (Request)is.readObject())
					    {
					         //Code
					    	System.out.println("package Received");
					    	
					    	switch(incoming.getType()) {
					    	case "SuccessfulLoginRequest":
					    		System.out.println("package Received in switch case");
					    		SuccessfulLoginHandler((SuccessfulLoginRequest)incoming);
					    		System.out.println(certification);
					    		
					    		break;
					    	case "BroadcastMessage":
					    		System.out.println("broadcast message received");
					    		broadcastMessageReceived((BroadcastMessageRequest)incoming);
					    		
					    		break;
					    		
					    	
					    	}
					    }
					} catch(IOException | ClassNotFoundException ex)
					{
					    //EOF found
					}
					// TODO Auto-generated method stub
					
				}
				//run listener once opened
			}).start();
		}
		
		public void close() throws IOException {
			this.os.close();
			this.socket.close();
		}
		
		public String getUsername() {
			return this.username;
		}
		
		public void testMessage(String hello) throws IOException, ClassNotFoundException {

			Request msg = new Request( hello);
			
			this.os.writeObject(msg);
			this.os.reset();
			System.out.println("test: object sent");
			
		}
		
		public void login(String username, String password) throws IOException, Exception {
			// create LoginRequest object
			LoginRequest loginRequest = new LoginRequest(username, password);
			this.username =username;
			
			//send request to server
			this.os.writeObject(loginRequest);
			this.os.reset();
			//give client time to update certification
			Thread.sleep(1000);
			
		}
		
		public void SuccessfulLoginHandler(SuccessfulLoginRequest request) {
			if(!request.getResult()) {
				this.certification = false;
				
			}else {
				this.certification=true;
			}
			
			
			
		}
		
		public void broadcastMessage(String message) throws IOException {
			BroadcastRequest broadcast = new BroadcastRequest(message);
			this.os.writeObject(broadcast);
			this.os.reset();
			System.out.println("broadcast message sent");
		}
		
		private void broadcastMessageReceived(BroadcastMessageRequest incoming) {
			
			System.out.println(">>>BROADCAST<<<");
			System.out.println(incoming.getUser() + ": " + incoming.getMessage() );
			System.out.println(">>>BROADCAST<<<");
		}
		
		public Socket getSocket() {
			return this.socket;
		}
		
		public void getAllClients() throws IOException{
			Request request = new Request("Client");
			this.os.writeObject(request);
			this.os.reset();
			
		}
		
	//MAIN METHOD
		public static void main(String[] args) throws Exception {
			
			Client client = new Client();
			client.reciever();
			
			//login
			Scanner scanner = new Scanner(System.in);
			
			boolean result = false;
			do {
				System.out.println("enter username : ");
				String username = scanner.nextLine();
				
				System.out.println("enter Password:");
				String password = scanner.nextLine();
				
				client.login(username, password);
			} 
			while(!client.certification);
			System.out.println("login success");
			
			//broadcast message
			Thread.sleep(3000);
			System.out.println("enter broadcast message");
			String message = scanner.nextLine();
			client.broadcastMessage(message);
			


		}
		
		


}
