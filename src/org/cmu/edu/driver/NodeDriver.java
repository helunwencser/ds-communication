package org.cmu.edu.driver;
/* Team 7
 * Lunwen He - lunwenh
 * Alejandro Jove - ajovedel
 * Distributed Systems: lab0
 *
 * File: Main.java
 * Desc: Program driver
 */

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.cmu.edu.message.Message;
import org.cmu.edu.messagePasser.MessagePasser;

public class NodeDriver {	
    private static String confFile = "../node_conf.yaml";
	private static String localName = null;
	
	private static Set<String> names = new HashSet<String>();
	
	private static void printNodeNames(){
		System.out.println("Possible node names are: ");
		for(String name : names)
			System.out.println("\t" + name);
		System.out.println("Local name is: " + localName);
	}
	
	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please select the local name.");
		String name = scanner.nextLine();
		while(name == null){
			System.out.println("Invalid local name, please select again");
			name = scanner.nextLine();
		}
		localName = name;
		
		MessagePasser messagePasser = new MessagePasser(confFile, localName);
		messagePasser.getNodeNames(names);
		
		while(true){
			System.out.println("Do you want to send(s) or receive(r) message or get time stamp(t)? Please select s/r/t: ");
			String option = scanner.nextLine();
			while(!option.equals("s") && !option.equals("r") && !option.equals("t")){
				System.out.println("Invalid operation, please select again(s/r/t): ");
				option = scanner.nextLine();
			}
			if(option.equals("s")){
				System.out.println("Please input the destnation of this message. ");
				printNodeNames();
				String dest = scanner.nextLine();
				while(dest == null || !names.contains(dest)){
					System.out.println("Invalid node name, please input again");
					printNodeNames();
					dest = scanner.nextLine();
				}
				System.out.println("Please select the kind of message: ");
				String kind = scanner.nextLine();
				while(kind == null || kind.trim().length() == 0){
					System.out.println("Invalid kind of message, please select again: ");
					kind = scanner.nextLine();
				}
				System.out.println("Please input the message you want to send: ");
				String content = scanner.nextLine();
				while(content == null || content.length() == 0){
					System.out.println("Message can't be empty, please input your message again: ");
					content = scanner.nextLine();
				}
				Message message = new Message(localName, dest, kind, content);
				messagePasser.send(message);
			}else if(option.equals("r")){
				Message message = messagePasser.receive();
				if(message != null){
					System.out.println(message.toString());
				}else{
					System.out.println("There is no message currently received.");
				}
			}else{
				System.out.println(messagePasser.getNextTimeStamp().toString());
			}
		}
	}
}
