package org.cmu.edu.driver;
/* Team 7
 * Lunwen He - lunwenh
 * Alejandro Jove - ajovedel
 * Distributed Systems: lab0
 *
 * File: Main.java
 * Desc: Program driver
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import org.cmu.edu.message.Message;
import org.cmu.edu.messagePasser.MessagePasser;
import org.cmu.edu.timestamp.RelationShip;

public class Logger {	
    private static String confFile = "../node_conf.yaml";
	private static String localName = "logger";
	
	public static void main(String[] args){
		/*
		 * Store all received messages and it's concurrent message
		 * */
		final LinkedHashMap<Message, List<Message>> messages = new LinkedHashMap<Message, List<Message>>();
		final MessagePasser messagePasser = new MessagePasser(confFile, localName);
		System.out.println("Logger has started, waiting for coming messages...");
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(true){
					Message message = messagePasser.receive();
					if(message != null){
						synchronized(messages){
							List<Message> concurrentMessages = new ArrayList<Message>();
							Iterator<Message> iterator = messages.keySet().iterator();
							while(iterator.hasNext()){
								Message key = iterator.next();
								if(key.getRelationShip(message) == RelationShip.Concurrent){
									messages.get(key).add(message);
									concurrentMessages.add(key);
								}
							}
							messages.put(message, concurrentMessages);
						}
					}else{
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
		}).start();
		
		Scanner reader = new Scanner(System.in);
		while(true){
			System.out.println("would you like to check all the received message? Y/N");
			String option = reader.nextLine();
			while(option == null || (!option.equals("Y") && !option.equals("N"))){
				System.out.println("would you like to check all the received message? Y/N");
				option = reader.nextLine();
			}
			if(option.equals("Y")){
				if(messages.size() == 0){
					System.out.println("There is no message received now.");
				}else{
					System.out.println("The details of all received messages.");
					synchronized(messages){
						Iterator<Message> iterator = messages.keySet().iterator();
						while(iterator.hasNext()){
							Message key = iterator.next();
							System.out.println(key.toString());
							System.out.println("\tConcurrent messages: ");
							List<Message> list = messages.get(key);
							if(list.size() == 0){
								System.out.println("\tThere is no concurrent messages.");
							}else{
								for(Message each : list){
									System.out.println("\t" + each.toString());
								}
							}
							System.out.println();
						}
					}
				}
			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
