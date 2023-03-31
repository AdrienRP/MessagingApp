

package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


import Requests.GetConversationsRequestResponse;
import Requests.GetConversationsRequest;
import Requests.GetAllUsersRequestResponse;
import Requests.GetAllUsersRequest;
import Requests.BroadcastMessageRequest;
import Requests.BroadcastRequest;
import Requests.LoginRequest;
import Requests.Request;
import Requests.SetStatusRequest;
import Requests.SuccessfulLoginRequest;
import Requests.MessageRequest;
import Requests.NewConvoRequest;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Messenger extends Application{
//DATA MEMBERS
		private Socket socket;
		private ObjectOutputStream os;
		private ObjectInputStream is;
		private boolean certification = false;
		private String username;
		Stage mainStage = new Stage();
		Stage arg0;
		File selectedFile;
		TextArea conversationTextArea;
		TextField userTextField;
		PasswordField pwBox;
		public String status = "online";
		public ArrayList<Integer> conversations;
		private ObservableList<String> contactListContents = FXCollections.observableArrayList();
		private ObservableList<String> selectedContactListContents = FXCollections.observableArrayList();
		private ObservableList<String> inboxListContents = FXCollections.observableArrayList();
		public ArrayList<Conversation> conversationList;
		public Conversation activeConversation;
		private ObservableList<String> displayedMessages = FXCollections.observableArrayList();
		private ObservableList<String> selectedContacts = FXCollections.observableArrayList();

		
		
	
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
		this.arg0=arg0;
		mainStage.setScene(loginScene());
		mainStage.show();
	}
//STOP
	public void stop() throws IOException {
		//close sockets
		close();
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
				    	case "GetStatus":
				    		System.out.println("status update: " + incoming.getMessage());
				    		break;
				    		
				    	case "GetAllUsersRequestResponse":
				    		buildUserList(((GetAllUsersRequestResponse)incoming).getUserList());
				    		break;
				    	
				    	case "NewConversationRequest":
				    		newConversationReceived(incoming);
				    		break;
				    		
				    	case "GetConversationsRequestResponse":
				    		buildInboxList(((GetConversationsRequestResponse) incoming).getConversations());
				    		break;
				    		
				    	case "NewMessage":
				    		//new message incoming in conversation_ID "request.getmessage()"
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

//USER ACTIONS	
	//user data
	public String getUsername() {
		return this.username;
	}
	
	//login
	public void login(String username, String password) throws IOException, Exception {
	    // create LoginRequest object
	    LoginRequest loginRequest = new LoginRequest(username, password);
	    this.username = username;

	    //send request to server
	    this.os.writeObject(loginRequest);
	    this.os.reset();
	    //give client time to update certification
	    Thread.sleep(1000);

	    Platform.runLater(() -> {
	        if (this.certification) {
	            mainStage.hide();
	            mainStage = homePage();
	            mainStage.show();
	        } else {
	            userTextField.clear();
	            pwBox.clear();
	        }
	    });
	}
	
	private void buildUserList(HashMap<String,String> userList) {
		Platform.runLater(() -> {
			contactListContents.removeAll(contactListContents);
			userList.forEach((user,status) -> contactListContents.add(new String(user + " [" + status + "]")));
		});
	}
	
	private void buildInboxList(ArrayList<Conversation> conversations) {
		//build inboxListContents<STRING> from conversations
		Platform.runLater(() -> {
			conversationList = new ArrayList<>();
			inboxListContents.removeAll(inboxListContents);
			for(Conversation convo: conversations) {
				conversationList.add(convo);
				inboxListContents.add(new String(convo.getGroupName()));
			}
		});
		
	}
	
	public void sendMessage(String text) {
		//send the request to append message to the convo
	}
	
	public void loadActiveConversation() {
		Platform.runLater(() -> {
			displayedMessages.removeAll(displayedMessages);
			try {
				for (Message msg: activeConversation.getMessages()) {
					displayedMessages.add(new String(msg.getUser() + ": " + msg.getMessages()));
				}
			} catch (Exception e) {
				displayedMessages.removeAll(displayedMessages);
			}
		});
	}
	
	
	
	private void createGroup(String groupName, ArrayList<String> members) throws IOException {
        NewConvoRequest rq = new NewConvoRequest(members, groupName);
        os.writeObject(rq);
        os.flush();
    }

    private void showAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}
	  
	public void SuccessfulLoginHandler(SuccessfulLoginRequest request) {
		
		 Label errorMessage = new Label("Invalid login");

		    if (!request.getResult()) {
		        this.certification = false;
		        System.out.println(request.getMessage());
		        Platform.runLater(() -> {
		            errorMessage.setText(request.getMessage()); // Set error message
		        });
		    } else {
		        this.certification = true;
		        System.out.println(request.getMessage());

		}
	}

	//status
	public void setStatus(String status) throws IOException {
		this.status = new String(status);
		SetStatusRequest request = new SetStatusRequest(status);
		this.os.writeObject(request);
		this.os.flush();
	}
	
	
	public void getStatus() throws IOException {
		Request msg = new Request("GetStatusRequest");
		
		this.os.writeObject(msg);
		this.os.reset();
		System.out.println("GetStatus: object sent");
		
	}
	public void getStatusReceived(Request incoming){
		Platform.runLater(()->{
			this.status = incoming.getMessage();
			System.out.println("Status: " +this.status);
			
		});
		
	}
	
	
	private void requestAllUsers() throws IOException {
		GetAllUsersRequest request = new GetAllUsersRequest();
		os.writeObject(request);
		os.flush();
		
	}
	
	private void getConversations() throws IOException {
		GetConversationsRequest rq = new GetConversationsRequest();
		os.writeObject(rq);
		os.flush();
	}
	//send messages
	
	public void testMessage(String hello) throws IOException, ClassNotFoundException {

		Request msg = new Request( hello);
		
		this.os.writeObject(msg);
		this.os.reset();
		System.out.println("test: object sent");
		
	}
	public void newConvoRequest(ArrayList<String> members) throws IOException {
		members.add(this.username);
		NewConvoRequest newConvoRequest = new NewConvoRequest(members);
		this.os.writeObject(newConvoRequest);
		this.os.reset();
	//overload
	}
	public void newConvoRequest(ArrayList<String> members, String groupName) throws IOException {
		members.add(this.username);
		NewConvoRequest newConvoRequest = new NewConvoRequest(members, groupName);
		this.os.writeObject(newConvoRequest);
		this.os.reset();
		
	}
	public void newConversationReceived(Request request) {
		System.out.println("you have been added to conversation_ID: " + request.getMessage());
	}
	
	public void sendMessage(int conversation, String message) throws IOException {
		MessageRequest messageRequest = new MessageRequest(this.username, conversation,message);
		this.os.writeObject(messageRequest);
		this.os.reset();
		System.out.println("MessageRequest Sent");
	}

	
	public void broadcastMessage(String message) throws IOException {
		BroadcastRequest broadcast = new BroadcastRequest(message);
		this.os.writeObject(broadcast);
		this.os.reset();
		System.out.println("broadcast message sent");
	}
	
	private void broadcastMessageReceived(BroadcastMessageRequest incoming) {
		 Platform.runLater(() -> {
		        System.out.println(">>>BROADCAST<<<");
		        String receivedMessage = incoming.getUser() + ": " + incoming.getMessage();
		        System.out.println(receivedMessage);
		        conversationTextArea.appendText(receivedMessage + "\n");
		        System.out.println(">>>BROADCAST<<<");
		    });
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
	public Scene loginScene() {
		
		double scaleFactor = 1.4;
    
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
        userTextField = new TextField();
        grid.add(userTextField, 1, 2);

        // Add a label for the password
        Label pw = new Label("Password:");
        grid.add(pw, 0, 3);

        // Add a password field for the password
        pwBox = new PasswordField();
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
				if(this.certification==true) {
					mainStage.hide();
					mainStage = homePage();
					mainStage.show();
					
				}
				else {
					userTextField.clear();
					pwBox.clear();
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            

            // Add login process here.....

            // Clear the text fields after login
           
            

          
          
        });
   
        
      

        // Create a scene and set it to the stage
        Scene scene = new Scene(grid, 640 * scaleFactor, 480 * scaleFactor);
        
        return scene;
        
		
	}

//=============HOMEPAGE============================//		
	public Stage homePage() {
		//ListView<File> inboxListView;
	    TextArea conversationTextArea;
	    
	    
		double scaleFactor = 1.4;


        // Create the plus button
        Button plusButton = new Button("+");

        // Create the username label
        Label usernameLabel = new Label(username);

        //Create the drop down list
        ComboBox<String> onlineStatusDropdown = new ComboBox<>();
        onlineStatusDropdown.getItems().addAll("Online", "Away", "Busy");
        onlineStatusDropdown.setValue(status);
        
 //set status
        onlineStatusDropdown.setOnAction(e ->{
        	try {
				setStatus(onlineStatusDropdown.getValue());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });
        
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
        ListView<String> inboxListView = new ListView<>(inboxListContents);
        try {
			getConversations();
		} catch (IOException e1) {
			// 
			e1.printStackTrace();
		}
        inboxListView.setPrefWidth(150 * scaleFactor); // set preferred width
        inboxListView.setPrefHeight(400 * scaleFactor);
        
        inboxListView.setOnMouseClicked(event -> {
        	activeConversation = conversationList.get(inboxListView.getSelectionModel().getSelectedIndex());
        	loadActiveConversation();
        });
        
//        inboxListView.setOnMouseClicked(event -> {
//            selectedFile = inboxListView.getSelectionModel().getSelectedItem();
//            if (selectedFile != null) {
//                try {
//                	/*
//                    // Read the contents of the text file using FileReader and BufferedReader
//                	//for(Conversation convo : Conversation.convoList){
//                	//	covo.getMembers.contains(this.username){
//                			convo.getmessages{
//                				print convo.getmessages.getSender
//                				print convo.getmessages.getmessage
//                	//		}
//                	//
//                	 * */
//                	
//                    FileReader fileReader = new FileReader(selectedFile);
//                    BufferedReader bufferedReader = new BufferedReader(fileReader);
//                    StringBuilder conversationText = new StringBuilder();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        conversationText.append(line).append("\n");
//                    }
//                    bufferedReader.close();
//                    fileReader.close();
//
//                    // Set the conversation TextArea text to the contents of the text file
//                    conversationTextArea.setText(conversationText.toString());
//
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//                //Change conversation name based on which conversation is clicked in the inbox
//                selectedFile = inboxListView.getSelectionModel().getSelectedItem();
//                if (selectedFile != null) {
//                    String conversationTitleText = selectedFile.getName();
//                    conversationTitle.setText(conversationTitleText);
//                    try {
//                        // Read the contents of the text file using FileReader and BufferedReader
//                        FileReader fileReader = new FileReader(selectedFile);
//                        BufferedReader bufferedReader = new BufferedReader(fileReader);
//                        StringBuilder conversationText = new StringBuilder();
//                        String line;
//                        while ((line = bufferedReader.readLine()) != null) {
//                            conversationText.append(line).append("\n");
//                        }
//                        bufferedReader.close();
//                        fileReader.close();
//
//                        // Set the conversation TextArea text to the contents of the text file
//                        conversationTextArea.setText(conversationText.toString());
//
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        });

        // Add the files to the inbox
//        File[] files = new File("MessagingApp/src").listFiles();
//        if (files != null) {
//            // Sort the files based on last modified time (most recent first)
//            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
//            for (File file : files) {
//                if (file.isFile() && file.getName().endsWith(".txt")) {
//                    inboxListView.getItems().add(file);
//                }
//            }
//        }
        
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
        
        // Put the txt file in the conversation window
        //selectedFile = inboxListView.getSelectionModel().getSelectedItem();
       
        
        //Send message
        sendButton.setOnAction(event -> {
        	sendMessage(textBox.getText());
            //String message = usernameLabel.getText() + ": " + textBox.getText() + "\n";
            //conversationTextArea.appendText(message);
            textBox.clear();

//            try {
//            	  broadcastMessage(message);
//                // Open the convo.txt file in append mode and write the message to it
//            	FileWriter fileWriter = new FileWriter(selectedFile, true);
//                fileWriter.write(message);
//                fileWriter.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
            
        });
        
        plusButton.setOnAction(e -> {
        	mainStage.hide();
			try {
				mainStage = create();
			} catch (IOException e1) {
		
				e1.printStackTrace();
			}
			mainStage.show();
        });
        
        // Create the scene
        Scene scene = new Scene(layout, 640 * scaleFactor, 480 * scaleFactor);
        
        Stage stage = new Stage();
        stage.setScene(scene);

        // Set the scene
        
        return stage;
	}
//==================CREATE PAGE=============================//	
	public Stage create() throws IOException {
		double scaleFactor = 1.4;
    	

        // Create the plus button
        Button homeButton = new Button("Home");

        // Create the username label
        Label usernameLabel = new Label(username);

        //Create the drop down list (create page)
        ComboBox<String> onlineStatusDropdown = new ComboBox<>();
        onlineStatusDropdown.getItems().addAll("Online", "Away", "Busy");
        onlineStatusDropdown.setValue(status);
        
        onlineStatusDropdown.setOnAction(event -> {
        	try {
				setStatus(onlineStatusDropdown.getValue());
			} catch (IOException e) {
				// 
				e.printStackTrace();
			}
        });
        

        // Create the log out button
        Button logOutButton = new Button("Log Out");
        logOutButton.setStyle("-fx-background-color: red;"); // set button color
        
        //Create group settings area
        TextArea groupSettings = new TextArea();
        
       // Create a VBox for group settings elements
        VBox groupSettingsElements = new VBox(10);
        groupSettingsElements.setAlignment(Pos.CENTER_LEFT);
        groupSettingsElements.setPadding(new Insets(5 * scaleFactor, 5 * scaleFactor, 5 * scaleFactor, 5 * scaleFactor));
        
        //Create HBOX for group name and text field
        HBox groupNameContainer = new HBox(5);
        groupNameContainer.setAlignment(Pos.CENTER_LEFT);

        //Create textfield for name of group
        Label groupName = new Label("Group Name:");
        TextField groupNameTextField = new TextField();
        
        groupNameContainer.getChildren().addAll(groupName, groupNameTextField);

        //Create label for users added
        usersAdded = new Label("Users Added:");
        updateSelectedContacts(username, true);

     // Create the contactList
        ListView<String> contactList = new ListView<>(contactListContents);
        contactList.setPrefWidth(150 * scaleFactor); // set preferred width
        contactList.setPrefHeight(400 * scaleFactor);
        	
      //Add or remove users from the group by clicking them on the contact list
        contactList.setOnMouseClicked(event -> {
            String selectedContact = contactList.getSelectionModel().getSelectedItem();
            if (selectedContact != null) {
                if (selectedContacts.contains(selectedContact)) {
                    updateSelectedContacts(selectedContact, false);
                } else {
                    updateSelectedContacts(selectedContact, true);
                }
            }
        });
        
        //Create the "create group" button
        Button createGroupButton = new Button("Create Group");
        createGroupButton.setStyle("-fx-background-color: green;"); // set button color
        
        createGroupButton.setOnAction(event -> {
            if (selectedContacts.size() > 0 && !groupNameTextField.getText().isEmpty()) {
                String groupNameInput = groupNameTextField.getText();
                ArrayList<String> tempToPass = new ArrayList<>();
                for (int i=0; i<selectedContacts.size(); i++) {
                	String string = new String(selectedContacts.get(i));
                	string = string.replaceAll("\\[.*?\\]", "");
                	string = string.replaceAll("\\s+", "");
                	tempToPass.add(string);
                }
                try {
					createGroup(groupNameInput, tempToPass);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                showAlert("Group Created", "Group '" + groupNameInput + "' was successfully created!");
                
                groupNameTextField.clear();
                selectedContacts.clear();
                updateSelectedContacts(null, false);
            } else {
                showAlert("Error", "Please enter a group name and add at least one contact.");
            }
        });

        // Add the group name, group name text field, users added, and create group button to the VBox
        groupSettingsElements.getChildren().addAll(groupNameContainer, usersAdded, createGroupButton);

       
      
      
        //Create the conversationtext area
        ListView<String> selectedContactList = new ListView<>(selectedContactListContents);
        conversationTextArea = new TextArea();
        selectedContactList.setPrefWidth(420 * scaleFactor);
        conversationTextArea.setPrefHeight(400 * scaleFactor);

      //Create Contacts label
        Label contactsLabel = new Label ("Contacts");
       
        
        
        
       
        requestAllUsers();
        
      //Add or remove users from the group by clicking them on the contact list
        contactList.setOnMouseClicked(event -> {
            String selectedContact = contactList.getSelectionModel().getSelectedItem();
            if (selectedContact != null) {
                if (selectedContacts.contains(selectedContact)) {
                    updateSelectedContacts(selectedContact, false);
                } else {
                    updateSelectedContacts(selectedContact, true);
                }
            }
        });
        //Store group conversation on Create group button
       

      //...

        
        //Create vbox for contact label and contacts
        VBox vbox2 = new VBox();
        vbox2.setAlignment(Pos.CENTER);
        vbox2.getChildren().addAll(contactsLabel, contactList);


        // Create the conversation
        TextArea conversation = new TextArea();
        conversation.setPrefWidth(420 * scaleFactor);
        conversation.setPrefHeight(400 * scaleFactor);


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
        topBar.getChildren().addAll(homeButton, usernameLabel, onlineStatusDropdown, logOutButton);
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
        vbox.getChildren().addAll(groupSettingsElements, conversation);
        // Set the VBox container as the center node of the layout
        layout.setCenter(vbox);
        layout.setTop(topBar);
        layout.setLeft(vbox2);
        layout.setBottom(bottomBar);
        
        
     // Put the txt file in the conversation window
        File selectedFile = new File("src/Empty.txt");
        if (selectedFile.exists()) {
            try {
                // Read the contents of the file using FileReader and BufferedReader
                FileReader fileReader = new FileReader(selectedFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                StringBuilder conversationText = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    conversationText.append(line).append("\n");
                }
                bufferedReader.close();
                fileReader.close();

                // Set the conversation TextArea text to the contents of the file
                conversation.setText(conversationText.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        //Send message
        sendButton.setOnAction(event -> {
            String message = usernameLabel.getText() + ": " + textBox.getText() + "\n";
            conversation.appendText(message);
            textBox.clear();

            try {
                // Open the convo.txt file in append mode and write the message to it
                FileWriter fileWriter = new FileWriter("src/Sandy.txt", true);
                fileWriter.write(message);
                fileWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        
        // Set action for Home button
        homeButton.setOnAction(e -> {
        	mainStage.hide();
			mainStage = homePage();
			mainStage.show();
            
        });
        
        // Set action for logout button
        logOutButton.setOnAction(e -> {
        	mainStage.hide();
    		mainStage.setScene(loginScene());
    		mainStage.show();
          
        });
        
        
    
        
           

        // Create the scene
        Scene scene = new Scene(layout, 640 * scaleFactor, 480 * scaleFactor);

        // Set the scene
        Stage stage = new Stage();
        stage.setScene(scene);

        // Set the scene
        
        return stage;

    }
    private Label usersAdded;
	
	private void updateSelectedContacts(String contact, boolean add) {
        if (add && !selectedContacts.contains(contact)) {
            selectedContacts.add(contact);
        } else {
            selectedContacts.remove(contact);
        }
        selectedContactListContents.setAll(selectedContacts);
        usersAdded.setText("Users Added: " + String.join(", ", selectedContacts));
    }
	
	public static void main(String[] args) {
	        launch(args);
	}
		
	

}//