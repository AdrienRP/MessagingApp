

package application;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import Requests.GetConversationsRequestResponse;
import Requests.GetConversationsRequest;
import Requests.GetAllUsersRequestResponse;
import Requests.GetAllUsersRequest;
import Requests.ConversationUpdateRequest;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;

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
	public void restart() throws UnknownHostException, IOException {
		stop();
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
		
		os.writeObject(new Request("ClosedClient"));
		os.flush();
		System.out.println("closed");
		closeMessenger();
	}
	public void closeMessenger() {
		
		try {
			if(this.os != null) {
				this.os.close();
			}
			if(this.is != null) {
				this.is.close();
			}
			if(this.socket != null) {
				this.socket.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
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
				    		SuccessfulLoginHandler((SuccessfulLoginRequest)incoming);
				    		
				    		break;

				    	case "GetStatus":
				    		break;
				    		
				    	case "GetAllUsersRequestResponse":
				    		buildUserList(((GetAllUsersRequestResponse)incoming).getUserList());
				    		break;
				    	
				    	case "NewConversationRequest":
				    		newConversationReceived(incoming);
				    		break;
				    		
				    	case "GetConversationsRequestResponse":
				    		GetConversationsRequestResponse request = (GetConversationsRequestResponse) incoming;
				    		//updateConversation(request);
				    		buildInboxList(((GetConversationsRequestResponse) incoming).getConversations());
				    		loadActiveConversation();
				    		break;
				    	case "ConversationUpdateRequest":	
				    		ConversationUpdateRequest rq = (ConversationUpdateRequest)incoming;
				    		updateConversation(rq);
				    		buildInboxList(conversationList);
				    		loadActiveConversation();
				    		
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
			userList.forEach((user,status) -> {
				if (!user.equals(this.username)) contactListContents.add(new String(user + " [" + status + "]"));
				
		});
			
			
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
	
	
	public void loadActiveConversation() {
		Platform.runLater(() -> {
			displayedMessages.removeAll(displayedMessages);
			try {
				for (Message msg: activeConversation.getMessages()) {
					if (!msg.isDeleted())displayedMessages.add(new String(msg.getUser() + ": " + msg.getMessages()));
					
				}
			} catch (Exception e) {
				displayedMessages.removeAll(displayedMessages);
			}
		});
	}
	
	public void updateConversation(ConversationUpdateRequest request) {
		//update activeConversation object
		
		int id = request.getID();
		String sender= request.getSender();
		String message = request.getMessage();
		Message newmsg= new Message(sender, message);
		for(Conversation convo: conversationList) {
			if(convo.getConversationID() == id) {
				convo.addMessage(newmsg);
				Platform.runLater(() -> {
					if (activeConversation == null || convo.getConversationID() != activeConversation.getConversationID()) {
						convo.setUnread();
						buildInboxList(conversationList);
						showAlert("Inbox", "You've got mail!");
						
					}
				});
								
				break;
			}
		}
		
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
		        Platform.runLater(() -> {
		            errorMessage.setText(request.getMessage()); // Set error message
		        });
		    } else {
		        this.certification = true;

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
		
	}
	public void getStatusReceived(Request incoming){
		Platform.runLater(()->{
			this.status = incoming.getMessage();
			
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
	
	public void sendMessage(int conversationID, String message) throws IOException {
		MessageRequest messageRequest = new MessageRequest(this.username, conversationID,message);
		this.os.writeObject(messageRequest);
		this.os.flush();
		System.out.println("MessageRequest Sent");
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
        onlineStatusDropdown.getItems().addAll("Online", "Away", "Busy", "Out to Lunch", "On the Phone", "Away from Desk");
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
        
      
      /*
        //Create the conversationtext area
        conversationTextArea = new TextArea();
        conversationTextArea.setPrefWidth(420 * scaleFactor);
        conversationTextArea.setPrefHeight(400 * scaleFactor);
*/
        
        
        //Create the conversation area using a listview
        ListView<String> conversationListView = new ListView<>(displayedMessages);
        try {
			getConversations();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        conversationListView.setPrefWidth(420 * scaleFactor); // set preferred width
        conversationListView.setPrefHeight(400 * scaleFactor);
        
        conversationListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                    setStyle("-fx-font-size: 10;");
                }
            };
            return cell;
        });

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
        	activeConversation.setRead();
        	loadActiveConversation();
        	buildInboxList(conversationList);
        	conversationTitle.setText("Conversation: " + activeConversation.getGroupName() + " with " + activeConversation.getMembers());
        });
        
        
        // Create the text box
        TextField textBox = new TextField();
        textBox.setPrefWidth(420 * scaleFactor); 

     // Create the delete button
        Button deleteButton = new Button("Delete Selected Message");
        	
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
        bottomBar.getChildren().addAll(deleteButton ,textBox, sendButton);

        // Create the main layout
        BorderPane layout = new BorderPane();
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(conversationTitle, conversationListView);
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
        	try {
				sendMessage(activeConversation.getConversationID(), textBox.getText());
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            //String message = usernameLabel.getText() + ": " + textBox.getText() + "\n";
            //conversationTextArea.appendText(message);
            textBox.clear();


            
        });
        
        logOutButton.setOnAction(e -> {
        	//groupNameTextField.clear();
            selectedContacts.clear();
            //updateSelectedContacts(null, false);
        		
    		try {
    			
				restart();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	mainStage.hide();
    		mainStage.setScene(loginScene());
    		mainStage.show();
          
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
        
        deleteButton.setOnAction(e -> {       	
        	activeConversation.getMessages().get(conversationListView.getSelectionModel().getSelectedIndex()).delete();
        	
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
        onlineStatusDropdown.getItems().addAll("Online", "Away", "Busy", "Out to Lunch", "On the Phone", "Away from Desk");
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
        
        
       // Create a VBox for group settings elements
        VBox groupSettingsElements = new VBox(10);
        groupSettingsElements.setAlignment(Pos.CENTER);
        groupSettingsElements.setPadding(new Insets(5 * scaleFactor, 5 * scaleFactor, 5 * scaleFactor, 5 * scaleFactor));
        
        //Create HBOX for group name and text field
        HBox groupNameContainer = new HBox(5);
        groupNameContainer.setAlignment(Pos.CENTER);

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



        

      

        // Create the top bar
        HBox topBar = new HBox(10);
        topBar.setSpacing(140 * scaleFactor);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10 * scaleFactor, 10 * scaleFactor, 10 * scaleFactor, 10 * scaleFactor));
        topBar.getChildren().addAll(homeButton, usernameLabel, onlineStatusDropdown, logOutButton);
        HBox.setHgrow(logOutButton, Priority.ALWAYS);
       

    

        // Create the main layout
        BorderPane layout = new BorderPane();
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(groupSettingsElements);
        // Set the VBox container as the center node of the layout
        layout.setCenter(vbox);
        layout.setTop(topBar);
        layout.setLeft(vbox2);
        layout.setBottom(null);
        
        
    
     
        
        // Set action for Home button
        homeButton.setOnAction(e -> {
        	mainStage.hide();
			mainStage = homePage();
			mainStage.show();
            
        });
        
        // Set action for logout button
        logOutButton.setOnAction(e -> {
        	groupNameTextField.clear();
            selectedContacts.clear();
            updateSelectedContacts(null, false);
    		try {
				restart();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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