package org.cmu.edu.messagePasser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cmu.edu.node.Node;

public class Config {
	
	/* the name of this node */
	private static String localName = "";
	
	/* the index of this node in the node list */
	private static int index = -1;
	
	/* the total number of nodes in the whole system */
	private static int nodeSize = -1;
	
    /* the index of each node in the node list */
    private static Map<String, Integer> nodeIndex = new HashMap<String, Integer>();
    
    /*
     * get the index of node in the node list
     * @param	nodeName	the name of the node
     * @return	the index of this node
     * */
    public static int getIndexOfNode(String nodeName){
    	if(nodeIndex.keySet().contains(nodeName)){
    		return nodeIndex.get(nodeName);
    	}
    	return -1;
    }
    
    /*
     * set the index of each node
     * */
    protected static void setNodeIndex(List<Node> nodeList){
    	for(int i = 0; i < nodeList.size(); i++){
    		nodeIndex.put(nodeList.get(i).getName(), i);
    	}
    }
    
	/*
	 * protected method, so that only MessagePasser can
	 * set the value of localName
	 * @param	localName	the name of this node
	 * */
	protected static void setLocalName(String localName){
		Config.localName = localName;
	}
	
	/*
	 * protected method, so that only MessagePasser can
	 * set the value of index
	 * @param	index	the index of this node
	 * */
	protected static void setIndex(int index){
		Config.index = index;
	}
	
	/*
	 * public method, so that methods in other package can 
	 * access the value of localName
	 * @return	the name of this node
	 * */
	public static String getLocalName(){
		return Config.localName;
	}
	
	/*
	 * public method, so that methods in other package can 
	 * access the value of index
	 * @return	the index of this node
	 * */
	public static int getIndex(){
		return Config.index;
	}

	/*
	 * public method, so that methods in other package can 
	 * access the value of nodeSize
	 * @return	nodeSize
	 * */
	public static int getNodeSize() {
		return Config.nodeSize;
	}

	/*
	 * protected method, so that only MessagePasser can
	 * set the value of nodeSize
	 * @param	nodeSize	the size of nodes in the system
	 * */
	protected static void setNodeSize(int nodeSize) {
		Config.nodeSize = nodeSize;
	}
	
	
}
