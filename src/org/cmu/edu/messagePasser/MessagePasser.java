package org.cmu.edu.messagePasser;
/* Team 7
 * Lunwen He - lunwenh
 * Alejandro Jove - ajovedel
 * Distributed Systems: lab0
 *
 * File: MessagePasser.java
 * Desc: Sends and receives messages
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.cmu.edu.clock.ClockService;
import org.cmu.edu.clock.ClockType;
import org.cmu.edu.clock.LogicalClock;
import org.cmu.edu.clock.VectorClock;
import org.cmu.edu.message.Message;
import org.cmu.edu.node.Node;
import org.cmu.edu.rule.Rule;
import org.cmu.edu.timestamp.TimeStamp;
import org.cmu.edu.util.Util;


public class MessagePasser {
	/* holds all nodes that are read by the configuration file */
    private List<Node> nodeList = new ArrayList<Node>();
    /* holds all send rules that are read by the configuration file */
    private List<Rule> sendRuleList = Collections.synchronizedList(new ArrayList<Rule>());
    /* holds all receive rules that are read by the configuration file */
    private List<Rule> recvRuleList = Collections.synchronizedList(new ArrayList<Rule>());

    /* local name */
    private String localName = null;
	/* configuration file path */
	private String confFile = null;
    /* sequence number */
    private int seqNum = 0;
    
    /* local clock service */
    private ClockService clock;
    
    /* clock type */
    private ClockType clockType;
    
    /* buffer for sending, each message will queue here before sending to the destination */
    private BlockingQueue<Message> sendBuffer = new LinkedBlockingQueue<Message>();
    /* when a message is delayed, it will wait in this queue until none-delayed message coming */
    private Queue<Message> sendDelayBuffer = new LinkedList<Message>();
    
    /* each received message will queue here, before going into the receive buffer */
    private BlockingQueue<Message> receiveQueue = new LinkedBlockingQueue<Message>();
    /* buffer for receiving, each received message will queue here before consumed by receive operation */
    private Queue<Message> receiveBuffer = new LinkedList<Message>();
    /* when a message is delayed, it will wait in this queue until none-delayed message coming*/
    private Queue<Message> receiveDelayBuffer = new LinkedList<Message>();

    /* 
     * ConcurrentHashMap which is thread safe stores the output stream to each node for sending message,
     * when need to send message to some node, just get the output stream and send the message
     *  */
    private Map<String, ObjectOutputStream> outputStreams = new ConcurrentHashMap<String, ObjectOutputStream>();
    
    public MessagePasser(String confFile, String localName){
        /* set current local name */
        this.localName = localName;
		this.confFile = confFile;

		/* set the localName in Config.java */
		Config.setLocalName(localName);
		
        /* parse the configuration file and set all nodes, send rules and receive rules */
        Util.parseAndSetNodes(confFile, this.nodeList);
        
        /* set the index of node */
        Config.setNodeIndex(nodeList);
        
        /* set the index in Config.java */
        Config.setIndex(Config.getIndexOfNode(this.localName));
        
        /* set nodeSize in Config.java */
        Config.setNodeSize(this.nodeList.size());

        /* parse the configuration file and set clock type */
		this.clockType = Util.parseAndSetClock(confFile);
		
		if(this.clockType == ClockType.Logical){
			this.clock = new LogicalClock();
		}else{
			this.clock = new VectorClock();
		}

        synchronized(this.sendRuleList){
        	Util.parseAndSetSendRules(confFile, this.sendRuleList);
        }
        synchronized(this.recvRuleList){
        	Util.parseAndSetReceiveRules(confFile, this.recvRuleList);
        }
        
        /*
         * setup the server side socket, and wait new messages coming
         * whenever there are new connection request to this node, this 
         * thread will create a new thread to maintain the connection.
         * */
        new Thread(new ThreadReceiveMessageFromSocket()).start();
        /*
         * whenever there are new message queuing in the receive queue, this
         * thread will get the message and handle it based on the receive rule
         * */
        new Thread(new ThreadRecieveMessageFromQueue()).start();
        
        /* set up the socket connection to each node, store the outputstream */
        setupSendSocket();
        
        /*
         * whenever there are new messages queuing in the send buffer, this thread will
         * get them out of the queue and send them out throw the socket 
         * */
        new Thread(new ThreadSendMessageToSocket()).start();

		/*
		 * this thread will read and update the ruleList in a synchronized way
		 * */
		new Thread(new ThreadUpdateRuleConfig()).start();
    }

    /* 
     * set the source and sequence number of the message, add it to the send buffer
     * */
    public void send(final Message message){
    	message.setSrc(this.localName);
    	message.setSeqNumber(this.seqNum++);
		message.setTimeStamp(getNextTimeStamp());
    	/*
    	 * create a new thread to queue the new message, so that the send operation do
    	 * not need to be blocked
    	 * */
    	new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					sendBuffer.put(message);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

    	}).start();
    }

    /*
     * send message to each member in members
     * @param	message	the message need to send
     * @param	members	the member list
     * */
    public void send(Message message, List<String> members){
    	for(String member : members){
    		final Message copy = message.getCopy();
    		copy.setSeqNumber(this.seqNum);
    		copy.setDest(member);
        	/*
        	 * create a new thread to queue the new message, so that the send operation do
        	 * not need to be blocked
        	 * */
        	new Thread(new Runnable(){

    			@Override
    			public void run() {
    				// TODO Auto-generated method stub
    				try {
    					sendBuffer.put(copy);
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}
        	}).start();
    	}
    	/* update the local time stamp */
    	this.getNextTimeStamp();
    	this.seqNum++;
    }
    
    // retrieve message from buffer
    public Message receive() {
    	if(receiveBuffer.size() > 0){
    		return receiveBuffer.poll();
    	}
    	return null;
    }

    /* get all the node names */
    public void getNodeNames(Set<String> names){
    	for(Node node : this.nodeList)
    		names.add(node.getName());
    }
    /* 
     * sent up the client socket to each node except self,
     * store the OutputStream to each node into this.sockets
     * */
    private void setupSendSocket(){
    	/* find the current node in the nodeList */
    	int i = 0;
    	while(i < this.nodeList.size() && !this.nodeList.get(i).getName().equals(this.localName))
    		i++;
    	/* 
    	 * current node will be client side for those nodes comes after current node in 
    	 * the ordered node list, in this way, we have only one tcp connection between
    	 * each pair of nodes.
    	 *  */
    	for(int j = i; j < this.nodeList.size(); j++){
    		Node node = this.nodeList.get(j);
			while(true){
    			try {
					Socket socket = new Socket(node.getIp(), node.getPort());
					/*
					 * after the connection has established, client first send node name to server
					 * so that server can distinguish different clients, then create a new thread
					 * to listen the output stream for this connection
					 * */
					ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
					writer.writeObject(this.localName);
					writer.flush();
					this.outputStreams.put(node.getName(), writer);
					ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
					new Thread(new ThreadHandlingSingleInputStream(reader)).start();
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("The destination is not ready, retrying...");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					continue;
				}
			}
    	}
    }
    
    /*
     * get matched send rule for one message, if there is one matched rule,
     * return the rule; otherwise return null
     * */
    private Rule getMatchedSendRule(Message message){
    	synchronized(this.sendRuleList){
	    	for(Rule rule : this.sendRuleList){
	    		if(rule.matchRule(message))
	    			return rule;
	    	}
    	}
    	return null;
    }
    
    /*
     * get matched receive rule for one message, if there is one matched rule,
     * return the rule; otherwise return null
     * */
    private Rule getMatchedReceiveRule(Message message){
    	synchronized(this.recvRuleList){
	    	for(Rule rule : this.recvRuleList){
	    		if(rule.matchRule(message))
	    			return rule;
	    	}
    	}
    	return null;
    }
    
    /*
     * get next time stamp from clock, since there are more than one 
     * threads may visit the clock, we need to synchronize this method
     * @return	next time stamp
     * */
    public synchronized TimeStamp getNextTimeStamp(){
    	return this.clock.generateNextTimeStamp();
    }
    
    /* 
     * whenever there is available message in the sendBuffer queue,
     * ThreadSendMessageToSocket will get one from the sendBuffer queue
     * and decide to send it or not based on sending rules, operations
     * related to sendRules are basically handled here
     *  */
    private class ThreadSendMessageToSocket implements Runnable{
    	
    	private void sendMessageThroughSocket(ObjectOutputStream writer, Message message){
			try {
				writer.writeObject(message);
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try {
					/*
					 * it will block here when the queue is empty
					 * */
					Message message = sendBuffer.take();
					Rule rule = getMatchedSendRule(message);
					if(rule == null){
						/* 
						 * no specific rule matched, send delayed messages and the current message
						 * */
						while(sendDelayBuffer.size() > 0){
							Message delayedMessage = sendDelayBuffer.poll();
							sendMessageThroughSocket(outputStreams.get(delayedMessage.getDest()), delayedMessage);
						}
						sendMessageThroughSocket(outputStreams.get(message.getDest()), message);
					}else{
						/*
						 * possible rules: drop, dropAfter, delay, duplicate
						 * */
						if(rule.getAction().equals("delay")){
							// delay message
							sendDelayBuffer.offer(message);
						}
						/*
						 * drop or dropAfter rule, drop the message
						 * */
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
    
    /*
     * whenever there are messages queuing in receive queue, this thread
     * will get them out of the queue one by one and handle each message 
     * based on the receive rule
     * */
    private class ThreadRecieveMessageFromQueue implements Runnable{
    	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try {
					Message message = receiveQueue.take();
					Rule rule = getMatchedReceiveRule(message);
					if(rule == null){
						receiveBuffer.offer(message);
						/* this is only for increase the local time stamp
						 * the returned time stamp is not used
						 * */
						clock.generateNextTimeStamp();
						clock.updateTimeStamp(message.getTimeStamp());
						/*
						 * no specific rule matched, send delayed deliver message and message to 
						 * receive buffer
						 * */
						while(receiveDelayBuffer.size() > 0){
							Message delayedMessage = receiveDelayBuffer.poll();
							receiveBuffer.offer(delayedMessage);
							/* this is only for increase the local time stamp
							 * the returned time stamp is not used
							 * */
							clock.generateNextTimeStamp();
							clock.updateTimeStamp(delayedMessage.getTimeStamp());
						}
					}else{
						/*
						 * possible rules: drop, dropAfter, delay
						 * */
						if(rule.getAction().equals("delay"))
							receiveDelayBuffer.offer(message);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
    
	/*
	 * This thread will keep listening on one input stream from a node,
	 * whenever there is a new message received, it will put the new message
	 * into the receive queue
	 * */
    private class ThreadHandlingSingleInputStream implements Runnable{
    	
    	private ObjectInputStream reader = null;
    	
    	public ThreadHandlingSingleInputStream(ObjectInputStream reader){
    		this.reader = reader;
    	}
    	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message message = null;
			try {
				while((message = (Message) reader.readObject()) != null)
					receiveQueue.put(message);
				this.reader.close();
			} catch (ClassNotFoundException | IOException
					| InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    private class ThreadReceiveMessageFromSocket implements Runnable{
    	/*
    	 * This thread accept tcp connection from a client. It will first receive the client's
    	 * name and then use the name as key to store the output stream for this connection. 
    	 * At last, it create a new thread to listen on the input stream to recieve messages.
    	 * */
    	private class ThreadHandingEachConnection implements Runnable{
    		private Socket socket;
    		public ThreadHandingEachConnection(Socket socket){
    			this.socket = socket;
    		}
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
					String clientName = (String) objectInputStream.readObject();
					outputStreams.put(clientName, objectOutputStream);
					new Thread(new ThreadHandlingSingleInputStream(objectInputStream)).start();
				} catch (IOException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    		
    	}
    	
    	private ServerSocket serverSocket;
    	
    	public ThreadReceiveMessageFromSocket(){
    		Node node = null;
    		for(Node each : nodeList){
    			if(each.getName().equals(localName)){
    				node = each;
    				break;
    			}
    		}

    		int port = node.getPort();
    		try {
				this.serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try {
					Socket server = this.serverSocket.accept();
					new Thread(new ThreadHandingEachConnection(server)).start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	
    }

	private class ThreadUpdateRuleConfig implements Runnable {
		
		public void run() {
			while (true) {
				/*
				 * Clear rule lists and re-read configuration file in a synchronized way
				 */
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				synchronized(sendRuleList){
					sendRuleList.clear();
					Util.parseAndSetSendRules(confFile, sendRuleList);
				}
				synchronized (recvRuleList) {
					recvRuleList.clear();
					Util.parseAndSetReceiveRules(confFile, recvRuleList);
				}
			}
		}
	}
}
