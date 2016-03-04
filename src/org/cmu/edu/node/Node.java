package org.cmu.edu.node;
/* Team 7
 * Lunwen He - lunwenh
 * Alejandro Jove - ajovedel
 * Distributed Systems: lab0
 *
 * File: Node.java
 * Desc: Describes all node's parameters
 */

public class Node implements Comparable{

    private String name;
    private String ip;
    private Integer port;

    public Node(String name, String ip, Integer port){
    	this.name = name;
    	this.ip = ip;
    	this.port = port;
    }

    public String getName(){
        return this.name;
    }

    public String getIp(){
        return this.ip;
    }

    public Integer getPort(){
        return this.port;
    }

    /*
     * since the name are unique, sort nodes based node's name is reasonable
     * */
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return this.name.compareTo(((Node)arg0).getName());
	}
}
