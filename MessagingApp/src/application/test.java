package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class test {
	public static void main(String[]args) throws FileNotFoundException, IOException {
		ArrayList<String> namesA = new ArrayList<>();
		namesA.add("b");
		namesA.add("a");
		namesA.add("c");
		namesA.add("d");
		namesA.add("e");
		
		ArrayList<String> namesB = new ArrayList<>();
		namesB.add("b");
		namesB.add("a");
		
		ArrayList<String> namesC= new ArrayList<>();
		namesC.add("a");
		namesC.add("b");

		
		
		Conversation convoA = new Conversation(namesA, "funTeam");
		
		Conversation convoB = new Conversation(namesB, "lmao");
		
		for (Conversation convo: Conversation.convoList) {
			ArrayList<String> temp = convo.getMembers();
			Collections.sort(temp);
			System.out.println(temp);
		}
		
		ArrayList<String> temp= (ArrayList<String>) namesB.clone();
		System.out.println("temp"+temp);
		System.out.println("NamesB" + namesB);
		temp.add("c");
		System.out.println("temp"+temp);
		System.out.println("NamesB" + namesB);

		if (namesB.equals(namesC)) {
			System.out.println("equal");
			
		}
		else {
			System.out.println("not smae");
		}
		
		File file = new File("1.txt");
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(convoA);
		oos.close();
		System.out.println("new obejct file");
		
	}

}