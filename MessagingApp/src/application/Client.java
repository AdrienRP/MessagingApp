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
		
		public void attemptLogin(String username, String password) throws IOException, Exception {
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
		
	//UI METHODS
		public Stage loginScreen() {
			Stage primaryStage = new Stage();
			
			double scaleFactor = 1.8;
			
			primaryStage.setTitle("Login");

	        // Create a GridPane layout
	        GridPane grid = new GridPane();
	        grid.setAlignment(Pos.CENTER);
	        grid.setHgap(10);
	        grid.setVgap(10);
	        grid.setPadding(new Insets(25 * scaleFactor, 25 * scaleFactor, 25 * scaleFactor, 25 * scaleFactor));
	        
	        //Add Company Logo
	        Image logoImage = new Image("logo-social.png");
	        ImageView logoView = new ImageView(logoImage);
	        grid.add(logoView, 0, 0, 2, 1);
	        GridPane.setHalignment(logoView, HPos.CENTER); 
	        
	     
	        // Add Company Name Label
	        Label scenetitle = new Label("Company Name");
	        scenetitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
	        grid.add(scenetitle, 0, 1, 2, 1);
	        GridPane.setHalignment(scenetitle, HPos.CENTER);
	       

	        // Add a label for the username
	        Label userName = new Label("Username:");
	        grid.add(userName, 0, 2);

	        // Add a text field for the username
	        TextField userTextField = new TextField();
	        grid.add(userTextField, 1, 2);

	        // Add a label for the password
	        Label pw = new Label("Password:");
	        grid.add(pw, 0, 3);

	        // Add a password field for the password
	        PasswordField pwBox = new PasswordField();
	        grid.add(pwBox, 1, 3);

	        // Add a login button
	        Button btn = new Button("Login");
	        grid.add(btn, 1, 4);
	        btn.setStyle("-fx-background-color: #8BC34A; -fx-text-fill: white;");
	        
	        
	        btn.setOnAction(e -> {
	        	
	            String username = userTextField.getText();
	            String password = pwBox.getText();;
	            
	            System.out.println(username + password);
	          
	        });
			
			return primaryStage;
		}
		
		
		//MAIN METHOD
		public static void main(String[] args) throws Exception {
			
			Client client = new Client();
			client.reciever();
			//login
			Stage loginStage = client.loginScreen();
			try {
				BorderPane root = new BorderPane();
				Scene scene = new Scene(root,400,400);
				//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				loginStage.setScene(scene);
				Login login = new Login();
				//login.showLogin(loginStage);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			loginStage.show();
			

	
			
			Scanner scanner = new Scanner(System.in);
			
	
			
			boolean result = false;
			do {
				System.out.println("enter username : ");
				String username = scanner.nextLine();
				
				System.out.println("enter Password:");
				String password = scanner.nextLine(); 
				
				client.attemptLogin(username, password);
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
