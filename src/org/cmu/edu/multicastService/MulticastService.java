package org.cmu.edu.multicastService;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.cmu.edu.group.Group;
import org.cmu.edu.message.Message;
import org.cmu.edu.messagePasser.Config;
import org.cmu.edu.messagePasser.MessagePasser;
import org.cmu.edu.util.Util;

/*
 * this class provides the operation of multi-cast
 * */
public class MulticastService {
    /*
     * map groups stores the information about groups
     * <key: group name, value: the corresponding group object>
     * groups is instantiated to ConcurrentHashMap for multi-thread
     * safety consideration. But it maybe not needed, since there 
     * will only get operation and no modification operation to this map. 
     * */
    private Map<String, Group> groups = new ConcurrentHashMap<String, Group>();
   
    /* local name */
    private String localName;
	/* configuration file path */
	private String confFile;
    
	/* message passer */
	private MessagePasser messagePasser;
	
	/* the queue of received messages */
	private Queue<Message> receivedMessage;
	
    public MulticastService(String confFile, final String localName){
    	this.confFile = confFile;
    	this.localName = localName;
    	this.messagePasser = new MessagePasser(confFile, localName);
    	this.receivedMessage = new LinkedList<Message>();
    	
    	/* parse the configuration file and get group information */
    	Util.parseGroupInfo(confFile, groups);
    	
    	/*
    	 * new thread to receive message and B-deliver it 
    	 * */
    	new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					Message message = messagePasser.receive();
					if(message != null){
						if(message.getGroupName() == null){
							//System.out.println("New P2P message: ");
							//System.out.println(message.toString());	
							synchronized(receivedMessage){
								receivedMessage.offer(message);
							}
						}else{
							bDeliver(message);
						}
					}
					/* cannot hold and lock resources all the time */
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

    	}).start();
    	
    	/*
    	 * new thread to Co-deliver message
    	 * */
    	new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Set<String> keys = groups.keySet();
				while(true){
					for(String groupName : keys){
						Message message = null;
						while((message = groups.get(groupName).getDeliverableMessage()) != null){
							int index = Config.getIndexOfNode(message.getCreatedBy());
					    	//System.out.println("New delivered message: ");
					    	//System.out.println(message.toString());
							synchronized(receivedMessage){
								receivedMessage.offer(message);
							}
					    	if(!message.getCreatedBy().equals(message.getDest())){
					    		groups.get(groupName).increaseTimeStamp(index);
					    	}
						}
					}
					/* cannot hold and lock resources all the time */
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
    		
    	}).start();
    }
    
    /*
     * B-multicast operation
     * the message should contain the group name as destination
     * during the multicast, we do not update the local clock or
     * the corresponding group clock because the causal order will
     * take care of that, B-multicast operation only serially send
     * message to each member in the group. At the same time, bMulticast
     * will call bDeliver to deliver the message to it self
     * @param	message	the message need to be multicasted
     * */
    private void bMulticast(Message message){
    	String groupName = message.getGroupName();
    	if(groupName == null || !this.groups.keySet().contains(groupName))
    		return;
    	message.setSrc(this.localName);
    	List<String> members = this.groups.get(groupName).getMembers();
    	this.messagePasser.send(message, members);
    }
    
    /*
     * B-deliver operation
     * this is used in the reliable multicast algorithm 
     * @param	message	the message to be delivered
     * */
    private void bDeliver(Message message){
    	String groupName = message.getGroupName();
    	if(groupName == null || !this.groups.keySet().contains(groupName))
    		return;
    	if(!this.groups.get(groupName).isReceived(message)){
    		this.groups.get(groupName).addReceivedMessage(message);
    		if(!message.getCreatedBy().equals(this.localName)){
    			this.bMulticast(message);
    		}
    		this.groups.get(groupName).addMessageToHoldBackQueue(message);
    	}
    }
    

    /*
     * Co-multicast operation
     * */
    public void coMulticast(Message message){
    	String groupName = message.getGroupName();
    	int index = Config.getIndexOfNode(this.localName);
    	if(groupName == null || !this.groups.keySet().contains(groupName)){
    		return;
    	}
    	this.groups.get(groupName).increaseTimeStamp(index);
    	message.setTimeStamp(this.groups.get(groupName).getClock().getTimeStamp());
    	this.bMulticast(message);
    }
    
    public Map<String, Group> getGroups() {
		return groups;
	}

	/*
     * get the name of all nodes
     * */
    public void getNodeNames(Set<String> nodeNames){
    	this.messagePasser.getNodeNames(nodeNames);
    }
    
    /* send P2P message */
    public void sendP2PMessage(Message message){
    	this.messagePasser.send(message);
    }
    
    /*
     * get message from the received message queue
     * @return	if the received message is not empty,
     * 			return the message; otherwise, return null
     * */
    public Message receive(){
    	synchronized(this.receivedMessage){
    		if(!this.receivedMessage.isEmpty()){
    			return this.receivedMessage.poll();
    		}
    		return null;
    	}
    }
}
