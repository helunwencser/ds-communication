package org.cmu.edu.rule;

import org.cmu.edu.message.Message;
/* Team 7
 * Lunwen He - lunwenh
 * Alejandro Jove - ajovedel
 * Distributed Systems: lab0
 *
 * File: Rule.java
 * Desc: Rules to be used by the MessagePasser
 */

public class Rule {

	/*  rule elements, if not specified, default values are null */
    private String action;
    private String src;
    private String dest;
    private String kind;
    private Integer seqNum;
    
    public Rule(String action, String src, String dest, String kind, Integer seqNum){
    	this.action = action;
    	this.src = src;
    	this.dest = dest;
    	this.kind = kind;
    	this.seqNum = seqNum;
    }

    /*
     * return the string representation of the rule
     * */
    public String toString(){
    	String res = "Rule Details: \n" + 
    				"\tRule action: " + this.action + "\n" + 
    				"\tRule source: " + this.src + "\n" + 
    				"\tRule destination: " + this.dest + "\n" +
    				"\tRule of message kind: " + this.kind + "\n" + 
    				"\tRule of message sequence number: " + this.seqNum + "\n";
    	return res;
    }

    
    public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
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

	public Integer getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	// checks if message matches a rule
    public boolean matchRule(Message message){
    	if(this.src != null && !this.src.equals(message.getSrc()))
    		return false;
    	if(this.dest != null && !this.dest.equals(message.getDest()))
    		return false;
    	if(this.kind != null && !this.kind.equals(message.getKind()))
    		return false;
    	if(this.seqNum != null){
    		if(this.action.equals("dropAfter")){
    			if(message.getSeqNumber() <= this.seqNum)
    				return false;
    		}else{
    			if(message.getSeqNumber() != this.seqNum)
    				return false;
    		}
    	}
    	return true;
    }
}
