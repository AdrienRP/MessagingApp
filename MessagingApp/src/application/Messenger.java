package application;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import Requests.BroadcastMessageRequest;
import Requests.BroadcastRequest;
import Requests.LoginRequest;
import Requests.Request;
import Requests.SuccessfulLoginRequest;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Messenger extends Application{
//DATA MEMBERS
		private Socket socket;
		private ObjectOutputStream os;
		private ObjectInputStream is;
		private boolean certification = false;
		private String username;
		Stage mainStage;
	
//INIT
	public void init() throws UnknownHostException, IOException {
		//connect to server
		this.socket = new Socket("127.0.0.1", Server.PORT);

		//create input/ output streams
		this.os = new ObjectOutputStream(socket.getOutputStream());
		this.is = new ObjectInputStream(socket.getInputStream()); 
		reciever();
		
	}
//START
	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		mainStage = loginScene();
		mainStage.show();
	}
//STOP
	public void stop() {
		System.out.println("closed");
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
			System.out.println(request.getMessage());
			mainStage= homePage();
			mainStage.show();
			 
			
		}else {
			this.certification=true;
			System.out.println(request.getMessage());
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
//SCENE BUILDERS
	public Stage loginScene() {
		
		Stage loginStage = new Stage();
		
		double scaleFactor = 1.8;
    	
    	loginStage.setTitle("Messenger Application");

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
     

        // Set action for login button
        btn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            try {
				login(username, password);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            

            // Add login process here.....

            // Clear the text fields after login
           
            

          
        });
   
        
      

        // Create a scene and set it to the stage
        Scene scene = new Scene(grid, 640 * scaleFactor, 480 * scaleFactor);
        loginStage.setScene(scene);
        return loginStage;
        
		
	}

		

	public Stage homePage() {
		ListView<File> inboxListView;
	    TextArea conversationTextArea;
	    File selectedFile;
	    
		double scaleFactor = 1.8;
    	Stage homeStage = new Stage();
    	homeStage.setTitle("Home");

        // Create the plus button
        Button plusButton = new Button("+");

        // Create the username label
        Label usernameLabel = new Label("Randy");

        //Create the drop down list
        ComboBox<String> onlineStatusDropdown = new ComboBox<>();
        onlineStatusDropdown.getItems().addAll("Online", "Away", "Busy");
        onlineStatusDropdown.setValue("Online");
        
        //Create conversation title
        Label conversationTitle = new Label("Conversation:");
        
      
      
        //Create the conversationtext area
        conversationTextArea = new TextArea();
        conversationTextArea.setPrefWidth(420 * scaleFactor);
        conversationTextArea.setPrefHeight(400 * scaleFactor);


        // Create the log out button
        Button logOutButton = new Button("Log Out");
        logOutButton.setStyle("-fx-background-color: red;"); // set button color
        
        
        //Create inbox label
        Label inboxLabel = new Label ("Inbox");
       
        
        // Create the inbox
        inboxListView = new ListView<>();
        inboxListView.setPrefWidth(150 * scaleFactor); // set preferred width
        inboxListView.setPrefHeight(400 * scaleFactor);
        
        // Create the text box
        TextField textBox = new TextField();
        textBox.setPrefWidth(420 * scaleFactor); 

        // Create the send button
        Button sendButton = new Button("Send");

        // Create the top bar
        HBox topBar = new HBox(10);
        topBar.setSpacing(140 * scaleFactor);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10 * scaleFactor, 10 * scaleFactor, 10 * scaleFactor, 10 * scaleFactor));
        topBar.getChildren().addAll(plusButton, usernameLabel, onlineStatusDropdown, logOutButton);
        HBox.setHgrow(logOutButton, Priority.ALWAYS);
       

        // Create the bottom bar
        HBox bottomBar = new HBox(10);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(10 * scaleFactor, 10 * scaleFactor, 10 * scaleFactor, 10 * scaleFactor));
        bottomBar.getChildren().addAll(textBox, sendButton);

        // Create the main layout
        BorderPane layout = new BorderPane();
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(conversationTitle, conversationTextArea);
        // Set the VBox container as the center node of the layout
        layout.setCenter(vbox);
        layout.setTop(topBar);
        
        VBox vbox2 = new VBox();
        vbox2.setAlignment(Pos.CENTER);
        vbox2.getChildren().addAll(inboxLabel, inboxListView);
        
        layout.setLeft(vbox2);
        layout.setBottom(bottomBar);
        
        // Create the scene
        Scene scene = new Scene(layout, 640 * scaleFactor, 480 * scaleFactor);

        // Set the scene
        homeStage.setScene(scene);
        return homeStage;
        
        
        

	}
	
	
	public static void main(String[] args) {
	        launch(args);
	}
		
	

}
