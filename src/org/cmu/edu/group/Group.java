package org.cmu.edu.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cmu.edu.clock.VectorClock;
import org.cmu.edu.message.Message;
import org.cmu.edu.messagePasser.Config;
import org.cmu.edu.timestamp.VectorTimeStamp;

/* Team 7
 * Lunwen He - lunwenh
 * Alejandro Jove - ajovedel
 * Distributed Systems: lab2
 * */

/*
 * This class defines one group, includes the following information
 * about the group: the name of the group, the member list of the
 * group, the vector clock for this group, received message until now
 * */
public class Group implements Serializable{
	
	/* The name of the group */
	private String groupName;
	
	/* the vector clock for this group */
	private VectorClock clock;
	
	/* the member list of this group */
	private List<String> members;
	
	/* received messages until now */
	private Set<Message> receivedMessages;
	
	/* hold-back queue for this group */
	private List<Message> holdBackQueue;
	
	/* the index of this node */
	//private final int index;
	
	public Group(String groupName, List<String> members){
		this.groupName = groupName;
		this.clock = new VectorClock();
		this.members = members;
		this.receivedMessages = new HashSet<Message>();
		this.holdBackQueue = new ArrayList<Message>();
	}
	
	/*
	 * if the specified message has received
	 * @param	message	the specified message
	 * @return	if this message has received, return true; 
	 * 		otherwise, return false 
	 * */
	public boolean isReceived(Message message){
		synchronized(this.receivedMessages){
			for(Message m : this.receivedMessages){
				if(m.isEqual(message)){
					return true;
				}
			}
			return false;
		}
	}
	
	/*
	 * add new received message to received message
	 * @param	message	the new message
	 * */
	public void addReceivedMessage(Message message){
		synchronized(this.receivedMessages){
			this.receivedMessages.add(message);
		}
	}
	
	/*
	 * add message to hold-back queue
	 * @param	message	message to be added in to hold-back queue
	 * */
	public synchronized void addMessageToHoldBackQueue(Message message){
		this.holdBackQueue.add(message);
	}
	
	/*
	 * if the message can be delivered from the hold-back queue
	 * @param	index	the index of this node
	 * @return	return true if it can be delivered; otherwise, return false
	 * */
	private boolean isDeliverable(Message message){
		VectorTimeStamp localTimeStamp = (VectorTimeStamp) this.clock.getTimeStamp();
		VectorTimeStamp timeStamp = (VectorTimeStamp) message.getTimeStamp();
		int index = Config.getIndexOfNode(message.getCreatedBy());
		for(int i = 0; i < Config.getNodeSize(); i++){
			if(i == index){
				if(!message.getCreatedBy().equals(message.getDest()) 
						&& timeStamp.getValueAt(i) != localTimeStamp.getValueAt(i) + 1){
					return false;
				}
			}else{
				if(timeStamp.getValueAt(i) > localTimeStamp.getValueAt(i)){
					return false;
				}
			}
		}
		return true;
	}
	
	/*
	 * check if there is any message can be delivered from hold-back queue
	 * if so, remove that message from hold-back queue and return it; otherwise,
	 * return null
	 * @param	index	the index of this node
	 * @return	the deliverable message
	 * */
	public synchronized Message getDeliverableMessage(){
		int i = 0;
		for(; i < this.holdBackQueue.size(); i++){
			if(this.isDeliverable(this.holdBackQueue.get(i))){
				break;
			}
		}
		if(i < this.holdBackQueue.size()){
			Message message = this.holdBackQueue.get(i);
			this.holdBackQueue.remove(i);
			return message;
		}
		return null;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public VectorClock getClock() {
		return clock;
	}

	public void setClock(VectorClock clock) {
		this.clock = clock;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}
	
	/*
	 * increase the time stamp bit at index
	 * @param	index	the index of the time stamp bit
	 * */
	public void increaseTimeStamp(int index){
		this.clock.increaseTimeStamp(index);
	}
	
	/*
	 * if node whose name is nodeName is a member of this group
	 * @param	nodeName	the name of the node
	 * @return	if nodeName is a member of this group, return true;
	 * 			otherwise, return false
	 * */
	public boolean isAMember(String nodeName){
		for(String member : this.members){
			if(member.equals(nodeName)){
				return true;
			}
		}
		return false;
	}
	
	/*
	 * get the number of nodes in this group
	 * @return	the number of nodes in this group
	 * */
	public int getNumberOfNode(){
		return this.members.size();
	}
}
