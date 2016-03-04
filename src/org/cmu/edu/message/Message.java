package org.cmu.edu.message;
/* Team 7
 * Lunwen He - lunwenh
 * Alejandro Jove - ajovedel
 * Distributed Systems: lab0
 *
 * File: Message.java
 * Desc: Messages to be sent between MessagePasser objects
 */

import java.io.Serializable;

import org.cmu.edu.timestamp.RelationShip;
import org.cmu.edu.timestamp.TimeStamp;

public class Message implements Serializable{

    /**
	 * serial version ID, this is needed when Serializable is implemented
	 */
	private static final long serialVersionUID = 3493928306822122574L;
	// message header data
    private String src;
    private String dest;
    private String kind;
    private int seqNumber;
    
    /* the node who create this message */
    private String createdBy;
    
    /* 
     * group name, if this message does not belong to any groups,
     * groupName is null
     * */
    private String groupName;

	// message contents
    private Object data;
    
	private TimeStamp timeStamp;

    /*public Message(String dest, String kind, Object data){
    	this.dest = dest;
    	this.kind = kind;
    	this.data = data;
    }*/
    
    public Message(String createdBy, String dest, String kind, Object data){
    	this.createdBy = createdBy;
    	this.dest = dest;
    	this.kind = kind;
    	this.data = data;
    }
    
	public Message(TimeStamp timeStamp, String src, String dest, String kind, 
			Object data, String groupName) {
    	this.dest = dest;
    	this.kind = kind;
    	this.data = data;
		this.timeStamp = timeStamp;
		this.groupName = groupName;
	}
	
    /*
     * return the string representation of the message
     * */
    public String toString(){
    	String res = "Message Details: \n" + 
    				"\tMessage created by " + this.createdBy + "\n" +
    				"\tMessage comes from: " + this.src + "\n" + 
    				"\tMessage goes to: " + this.dest + "\n" +
    				"\tMessage belongs to group: " + this.groupName + "\n" +
    				"\tMessage kind of: " + this.kind + "\n" + 
    				"\tMessage sequence number: " + this.seqNumber + "\n" + 
    				"\tMessage content: " + this.data.toString() + "\n" + 
    				"\tMessage time stamp " + this.timeStamp.toString() + "\n";
    	return res;
    }
    
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public int getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(int seqNumber) {
		this.seqNumber = seqNumber;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(TimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public RelationShip getRelationShip(Message message){
		return this.timeStamp.getRelationShip(message.getTimeStamp());
	}
	
    public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	/*
	 * get the deep copy of this message
	 * @return	the deep copy of this message
	 * */
	public Message getCopy(){
		Message message = new Message(this.timeStamp.getCopy(),this.src, this.dest, 
				this.kind, this.data, this.groupName);
		message.setCreatedBy(this.createdBy);
		message.setSrc(this.src);
		message.setSeqNumber(this.seqNumber);
		return message;
	}
	
	/*
	 * if this equals to message. if two messages are equals both in
	 * createdBy and timeStamp, then they are the same message, this is mainly
	 * used when multicast message in groups
	 * @return	true if this equals to message; otherwise, return false
	 * */
	public boolean isEqual(Message message){
		if(!this.createdBy.equals(message.getCreatedBy()) 
				|| !this.timeStamp.isEqual(message.getTimeStamp())){
			return false;
		}
		return true;
	}
}
