package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {
	//dedclare port number
		public static final int PORT = 3191;
		public ServerSocket ss;
		public static HashMap<String,String> lc = new HashMap<String, String>();
		public static ArrayList<Conversation> allConversations = new ArrayList<>();
		public int convoNumber;
		
		
		public Server() throws IOException, ClassNotFoundException {
			
			this.ss = new ServerSocket(PORT);
			// running server
		}
		
		public void loadLogin() {		
			Map<String, String> mapFromFile = HashMapFromTextFile();
			for (Map.Entry<String, String> entry :
	             mapFromFile.entrySet()) {
	            lc.put(entry.getKey(),entry.getValue());
	        }
		}
		
		public static Map<String, String> HashMapFromTextFile()
	    {
	  
	        Map<String, String> map
	            = new HashMap<String, String>();
	        BufferedReader br = null;
	  
	        try {
	  
	            // create file object
	            File file = new File("MessagingApp/src/application/users.txt");
	  
	            // create BufferedReader object from the File
	            br = new BufferedReader(new FileReader(file));
	  
	            String line = null;
	  
	            // read file line by line
	            while ((line = br.readLine()) != null) {
	  
	                // split the line by :
	                String[] parts = line.split(":");
	  
	                // first part is name, second is number
	                String name = parts[0].trim();
	                String pass = parts[1].trim();
	  
	                // put name, number in HashMap if they are
	                // not empty
	                if (!name.equals("") && !pass.equals(""))
	                    map.put(name, pass);
	            }
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally {
	  
	            // Always close the BufferedReader
	            if (br != null) {
	                try {
	                    br.close();
	                }
	                catch (Exception e) {
	                };
	            }
	        }
	        
	        return map;
	    }
	

		public void loadConversations() throws IOException, ClassNotFoundException {
			//create conversation for every text file 1.txt, 2.txt...
			File file = new File("MessagingApp/src/application/Server.txt");
			
			 // create BufferedReader object from the File
            BufferedReader br = new BufferedReader(new FileReader(file));
            String convonum = br.readLine();
            br.close();
            this.convoNumber=Integer.valueOf(convonum);
            System.out.println("Conversations on server ="+ convonum);
            
            for(int i =0;i < this.convoNumber; i++) {
            	String location = "MessagingApp/src/application/"+i+".txt";
            	file = new File(location);
            	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            	Conversation temp = (Conversation)ois.readObject();
            	
            	Server.allConversations.add(temp);
            	System.out.println(i);
            }
         
			
		}
		public void saveConversations() throws IOException {
			
         for(Conversation convo: Conversation.convoList) {
        	String location = "MessagingApp/src/application/"+Integer.toString(convo.getConversation_ID())+".txt";
        	File file = new File(location);
     		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
     		oos.writeObject(convo);
     		oos.close();
     		System.out.println("Output convoID: "+Integer.toString(convo.getConversation_ID()));
        	 
            }
			
		}
		public void updateConvoCount() {
			this.convoNumber=Conversation.convoList.size();
		}

			
		public void startServer() throws IOException, ClassNotFoundException {
			System.out.println("Server Status: Running...");
			
			
			
			
			
			while(!ss.isClosed()) {
				
				Socket socket = this.ss.accept(); // blocking function till client connects
				
				//once client connects to server, create client handler for specific instance
				ClientHandler clientHandler = new ClientHandler(socket);
				System.out.println("handler created");
				
				Thread thread = new Thread(clientHandler);
				thread.start();
				
			}
			
		
		}
		public static void main(String[] args) throws IOException, ClassNotFoundException {
			//create server
			Server server = new Server();
			System.out.println("server Status: Launching...");
			server.loadLogin();
			server.loadConversations();
			server.startServer();
			
			System.out.println("enter command: ");
			Scanner scanner = new Scanner(System.in);
			int input = scanner.nextInt();
			
			//sadfa
		}




}
