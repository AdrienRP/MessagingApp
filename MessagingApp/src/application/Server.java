package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {
	//dedclare port number
		public static final int PORT = 3191;
		public ServerSocket ss;
		public static HashMap<String,String> lc = new HashMap<String, String>();
		public static HashMap<String,String> userList = new HashMap<String, String>();
		

		
		
		public Server() throws IOException, ClassNotFoundException {
			
			this.ss = new ServerSocket(PORT);
			// running server
		}
		
		public void loadLogin() {		
			Map<String, String> mapFromFile = HashMapFromTextFile();
			for (Map.Entry<String, String> entry :
	             mapFromFile.entrySet()) {
	            lc.put(entry.getKey(),entry.getValue());
	            userList.put(entry.getKey(), "offline");
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
	

		public void loadConversations() {
			//create conversation for every text file 1.txt, 2.txt...
			
		}
		public void loadUserData() {
			//create user object
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
			server.startServer();
			
			System.out.println("enter command: ");
			Scanner scanner = new Scanner(System.in);
			int input = scanner.nextInt();
			
			//sadfa
		}




}
