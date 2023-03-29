package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Arrays;

import Requests.SetStatusRequest;
import Requests.GetAllUsersRequest;
import Requests.GetAllUsersRequestResponse;
import Requests.BroadcastMessageRequest;
import Requests.BroadcastRequest;
import Requests.LoginRequest;
import Requests.Request;
import Requests.SuccessfulLoginRequest;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Client {
	//DATA MEMBERS
		
		private Socket socket;
		private ObjectOutputStream os;
		private ObjectInputStream is;
		private boolean certification = false;
		private String username;
		ObservableValue<String> test = new SimpleStringProperty("bruh");
		private ObservableValue<String> status = new SimpleStringProperty("online");
		private ObservableList<String> contactListContents; 
		

	//CONSTRUCTOR
		public Client() throws UnknownHostException, IOException, ClassNotFoundException {
			//connect to server
			this.socket = new Socket("127.0.0.1", Server.PORT);

			//create input/ output streams
			this.os = new ObjectOutputStream(socket.getOutputStream());
			this.is = new ObjectInputStream(socket.getInputStream());
			
			this.contactListContents = FXCollections.observableArrayList();
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
					    		System.out.println("login package Received in switch case");
					    		SuccessfulLoginHandler((SuccessfulLoginRequest)incoming);
					    		System.out.println(certification);
					    		
					    		break;
					    	case "BroadcastMessage":
					    		System.out.println("broadcast message received");
					    		broadcastMessageReceived((BroadcastMessageRequest)incoming);
					    		
					    		break;
					    	case "GetAllUsersRequestResponse":				
					    		buildUserList(((GetAllUsersRequestResponse)incoming).getUserList());
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
			this.os.flush();
			System.out.println("test: object sent");
			
		}
		
		public void attemptLogin(String username, String password) throws IOException, Exception {
			// create LoginRequest object
			LoginRequest loginRequest = new LoginRequest(username, password);
			this.username =username;
			
			//send request to server
			this.os.writeObject(loginRequest);
			this.os.flush();
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
			this.os.flush();
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
			this.os.flush();
			
		}
		

		public boolean getCertification() {
			return certification;

		}
		
		//send rq to server asking for list of users & statuses
		public void requestAllUsers() throws IOException {
			GetAllUsersRequest request = new GetAllUsersRequest();
			this.os.writeObject(request);
			this.os.flush();
		}
		
		//take rq from server holding list of users & statuses and update the observable list bound to the listview
		private void buildUserList(ConcurrentHashMap<String,String> userList) {			
			Platform.runLater(() -> {
				contactListContents.removeAll(contactListContents);
				userList.forEach((user,status) -> contactListContents.add(new String(user + " [" + status + "]")));
			});
				
		}

		public void sendStatus(String value) throws IOException {
			SetStatusRequest request = new SetStatusRequest(value);
			this.os.writeObject(request);
			this.os.flush();
			
		}

		public ObservableList<String> getContactListContents() {
			return contactListContents;
		}

		public ObservableValue<String> getStatus() {
			return status;
		}
		
		


}
