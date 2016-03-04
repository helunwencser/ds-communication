package org.cmu.edu.driver;
/* Team 7
 * Lunwen He - lunwenh
 * Alejandro Jove - ajovedel
 * Distributed Systems: lab0
 *
 * File: MulticastDriver.java
 * Desc: Program driver
 */

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.cmu.edu.group.Group;
import org.cmu.edu.message.Message;
import org.cmu.edu.multicastService.MulticastService;

public class MulticastDriver {	
    private static String confFile = "../node_conf.yaml";
	private static String localName = null;
	
	/* the name of all nodes */
	private static Set<String> nodeNames = new HashSet<String>();
	/* the information of groups */
	private static Map<String, Group> groups;
	
	/* print out all groups' name */
	private static void printGroupNames(){
		System.out.println("Detailed group information: ");
		for(String groupName : groups.keySet()){
			Group group = groups.get(groupName);
			System.out.print("\t" + groupName + ": ");
			List<String> members = group.getMembers();
			for(String member : members){
				System.out.print(member + "\t");
			}
			System.out.println();
		}
		System.out.println("Local name is: " + localName);
	}
	
	/* print out all nodes' name */
	private static void printNodeNames(){
		System.out.println("Possible node names are: ");
		for(String nodeName : nodeNames)
			System.out.println("\t" + nodeName);
		System.out.println("Local name is: " + localName);
	}
	
	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please select the local name.");
		String name = scanner.nextLine();
		while(name == null || name.length() == 0){
			System.out.println("Invalid local name, please select again");
			name = scanner.nextLine();
		}
		localName = name;
		
		MulticastService multicastService = new MulticastService(confFile, localName);
		multicastService.getNodeNames(nodeNames);
		groups = multicastService.getGroups();
		
		while(true){
			System.out.println("Do you want to send group message(g) or P2P message(p)? Please select g/p: ");
			String option = scanner.nextLine();
			while(!option.equals("g") && !option.equals("p")){
				System.out.println("Invalid operation, please select again(g/p): ");
				option = scanner.nextLine();
			}
			if(option.equals("g")){
				System.out.println("Please input the group destnation of this message. ");
				printGroupNames();
				String groupName = scanner.nextLine();
				while(groupName == null 
						|| !groups.keySet().contains(groupName)
						|| !groups.get(groupName).isAMember(localName)){
					System.out.println("Invalid group name, please input again");
					printGroupNames();
					groupName = scanner.nextLine();
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
				Message message = new Message(localName, null, kind, content);
				message.setGroupName(groupName);
				message.setSrc(localName);
				multicastService.coMulticast(message);
			}else{
				System.out.println("Please input the destnation of this message. ");
				printNodeNames();
				String dest = scanner.nextLine();
				while(dest == null || !nodeNames.contains(dest)){
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
				multicastService.sendP2PMessage(message);
			}
		}
	}
}
