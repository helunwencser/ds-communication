package org.cmu.edu.mutualexclusion;

/* Team 7
 * Lunwen He - lunwenh
 * Alejandro Jove - ajovedel
 * Distributed Systems: lab0
 *
 * File: MutualExclusionService.java
 * Desc: This class implements Maekawa's mutual exclusion algorithm
 */

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.cmu.edu.group.Group;
import org.cmu.edu.message.Message;
import org.cmu.edu.multicastService.MulticastService;

public class MutualExclusionService {
	/* the state of node */
	private MEState state;
	
	/* if voted */
	private boolean voted;

	/* the queue of request */
	private Queue<Message> requestQueue;
	
	/* multicast service */
	private MulticastService multicastService;
	
	/* the node's name */
	private String localName;
	
	/* the information of groups */
	private Map<String, Group> groups;
	
	/* the group this node belongs to */
	private String groupName;
	
	/* the number of vote received for one request */
	private Integer numberOfVote;
	
	/* the number of nodes in this group */
	private int numberOfNode;
	
	/* 
	 * initialize the mutual exclusion service, 
	 * state is released, voted is false and 
	 * request queue is empty, start multicastService
	 * @param	configFile
	 * 			the path of configuration file
	 * 
	 * @param	localName
	 * 			the name of this node
	 * */
	public MutualExclusionService(String confFile, String localName){
		this.state = MEState.RELEASED;
		this.voted = false;
		this.requestQueue = new LinkedList<Message>();
		this.localName = localName;
		this.multicastService = new MulticastService(confFile, localName);
		this.groups = this.multicastService.getGroups();
		this.groupName = this.getGroupName();
		this.numberOfVote = 0;
		this.numberOfNode = this.getNumberOfNode();
		/* thread for receiving message */
		new Thread(new ThreadForReceiveMessage()).start();
	}
	
	/*
	 * get the name of group which this node belongs to
	 * @return	the name of group
	 * */
	private String getGroupName(){
		Set<String> keys = this.groups.keySet();
		for(String key : keys){
			if(key.endsWith(this.localName)){
				return key;
			}
		}
		return null;
	}
	
	/*
	 * get the number of nodes in this group
	 * @return	the number of nodes in this group
	 * */
	private int getNumberOfNode(){
		return this.groups.get(groupName).getNumberOfNode();
	}
	
	/*
	 * try to enter the critical section, this method will
	 * block until the  request is granted
	 * */
	public void enterCS(){
		/* set the count of vote to 0 */
		this.numberOfVote = 0;
		this.state = MEState.WANTED;
		Message request = new Message(this.localName, null, MessageKindForME.REQUEST, "request");
		request.setGroupName(this.groupName);
		request.setSrc(this.localName);
		this.multicastService.coMulticast(request);
		System.out.println("Multicast one request message to group " + this.groupName);
		/* wait until the number of vote equals to K */
		while(this.numberOfVote < this.numberOfNode){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.state = MEState.HELD;
		System.out.println("Entered critical section");
	}
	
	/*
	 * exit critical section
	 * */
	public void exitCS(){
		this.state = MEState.RELEASED;
		Message release = new Message(this.localName, null, MessageKindForME.RELEASE, "release");
		release.setGroupName(this.groupName);
		release.setSrc(this.localName);
		this.multicastService.coMulticast(release);
		System.out.println("Multicast one release message to group " + this.groupName);
		System.out.println("Left critical section");
	}
	
	private class ThreadForReceiveMessage implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				Message message = multicastService.receive();
				if(message != null){
					System.out.println("Receive one " + message.getKind() + " from " + message.getCreatedBy());
					//System.out.println(message.toString());
					if(message.getGroupName() == null){
						/* p2p message, get an vote */
						if(message.getKind().equals(MessageKindForME.VOTE)){
							synchronized(numberOfVote){
								numberOfVote++;
							}
						}
					}else{
						/* group message, can be request or release */
						if(message.getKind().equals(MessageKindForME.REQUEST)){
							if(state == MEState.HELD || voted == true){
								requestQueue.offer(message);
							}else{
								Message reply = new Message(localName, message.getCreatedBy(), 
										MessageKindForME.VOTE, "vote");
								multicastService.sendP2PMessage(reply);
								System.out.println("Send one vote message to " + message.getCreatedBy());
								voted = true;
							}
						}else if(message.getKind().equals(MessageKindForME.RELEASE)){
							if(!requestQueue.isEmpty()){
								Message request = requestQueue.poll();
								Message reply = new Message(localName, request.getCreatedBy(),
										MessageKindForME.VOTE, "vote");
								multicastService.sendP2PMessage(reply);
								System.out.println("Send one vote message to " + request.getCreatedBy());
								voted = true;
							}else{
								voted = false;
							}
						}
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}




















