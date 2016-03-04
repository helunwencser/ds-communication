package org.cmu.edu.util;
/* Team 7
 * Lunwen He - lunwenh
 * Alejandro Jove - ajovedel
 * Distributed Systems: lab0
 *
 * File: Util.java
 * Desc: Useful tools used to configure the Distributed System
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.cmu.edu.clock.ClockType;
import org.cmu.edu.group.Group;
import org.cmu.edu.node.Node;
import org.cmu.edu.rule.Rule;
import org.yaml.snakeyaml.Yaml;


public class Util {
    /*
     * parse and set nodes based on the configuration file
     * */
public static void parseAndSetNodes(String confFile, List<Node> nodeList){
       Map<String,Object> confData = null;
       InputStream input = null;
       try {
           input = new FileInputStream(new File(confFile));
           Yaml yaml = new Yaml();
           confData = (Map<String,Object>)yaml.load(input);
       }catch(FileNotFoundException e){
           e.printStackTrace();
       }
       // get node configuration, which is a List of maps, each map being a node configuration
       List<Map<String, Object>> myNodeConfig = (List<Map<String,Object>>)confData.get("configuration");
       
       // for each map use the key to get the value and set it in the Node
       for(int i=0; i<myNodeConfig.size();i++){
           String name = (String) myNodeConfig.get(i).get("name");
           String ip = (String) myNodeConfig.get(i).get("ip");
           Integer port = (Integer) myNodeConfig.get(i).get("port");
           nodeList.add(new Node(name, ip, port));
       }
       if(input != null){
    	   try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       }
       
       /* after get all nodes, sort all nodes based node's name
        * so that the nodes are ordered.
        *  */
       Collections.sort(nodeList);
   }
   
   /*
    *parse and set send rules based on the configuration file
    * */
public static void parseAndSetSendRules(String confFile, List<Rule> sendRuleList){
      Map<String,Object> ruleData = null;
      InputStream input = null;
      try {
          input = new FileInputStream(new File(confFile));
          Yaml yaml = new Yaml();
          ruleData = (Map<String,Object>)yaml.load(input);
      }catch(FileNotFoundException e){
          e.printStackTrace();
      }
      
      // get send rule configuration, which is a List of maps, each map being a send rule configuration
      List<Map<String, Object>> mySendConfig = (List<Map<String,Object>>)ruleData.get("sendRules");

      if(mySendConfig == null) {
          return;
      }

      for(int i=0; i < mySendConfig.size();i++) {
          String action = (String) mySendConfig.get(i).get("action");
          String src = (String) mySendConfig.get(i).get("src");
          String dst = (String) mySendConfig.get(i).get("dest");
          String kind = (String) mySendConfig.get(i).get("kind");
          Integer seqNum = (Integer) mySendConfig.get(i).get("seqNum");
          sendRuleList.add(new Rule(action, src, dst, kind, seqNum));
      }
      
      if(input != null){
    	  try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
  }

	/*
	 * parse and set receive rules based on the configuration file
	 * */
	public static void parseAndSetReceiveRules(String confFile, List<Rule> recvRuleList){
	   Map<String,Object> ruleData = null;
	   InputStream input = null;
	   try {
	       input = new FileInputStream(new File(confFile));
	       Yaml yaml = new Yaml();
	       ruleData = (Map<String,Object>)yaml.load(input);
	   }catch(FileNotFoundException e){
	       e.printStackTrace();
	   }
	   
	   // get recv rule configuration, which is a List of maps, each map being a recv rule configuration
	   List<Map<String, Object>> myRecvConfig = (List<Map<String,Object>>)ruleData.get("receiveRules");

        if (myRecvConfig == null) {
            return;
        }

	   for(int i = 0; i< myRecvConfig.size();i++) {
	       String action = (String) myRecvConfig.get(i).get("action");
	       String src = (String) myRecvConfig.get(i).get("src");
	       String dst = (String) myRecvConfig.get(i).get("dest");
	       String kind = (String) myRecvConfig.get(i).get("kind");
	       Integer seqNum = (Integer) myRecvConfig.get(i).get("seqNum");
	       recvRuleList.add(new Rule(action, src, dst, kind, seqNum));
	   }
	   if(input != null){
	 	  try {
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }
	}
	/*
	 * parse and get clock type
	 * */
    public static ClockType parseAndSetClock(String confFile){
        Map<String,Object> clockData = null;
        InputStream input = null;

        try {
            input = new FileInputStream(new File(confFile));
            Yaml yaml = new Yaml();
            clockData = (Map<String,Object>)yaml.load(input);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        List<Map<String, Object>> myClockConfig = (List<Map<String,Object>>)clockData.get("clock");
        String myClockType = (String) myClockConfig.get(0).get("type");
        if (myClockType.equals("logical")){
            return ClockType.Logical;
        }else{
            return ClockType.Vector;
        }
    }
    
    /*
     * parse and set multicast groups
     * */
    public static void parseGroupInfo(String confFile, Map<String, Group> groups) {
        Map<String, Object> multicastData = null;
        InputStream input = null;
        try {
            input = new FileInputStream(new File(confFile));
            Yaml yaml = new Yaml();
            multicastData = (Map<String, Object>) yaml.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // get multi casts groups into a list
        List<Map<String, Object>> myMultiCastConfig = (List<Map<String, Object>>) multicastData.get("groups");

        if (myMultiCastConfig == null) {
            return;
        }

        for(int i=0; i < myMultiCastConfig.size(); i++) {
        	String groupName = (String) myMultiCastConfig.get(i).get("name");
        	List<String> groupMembers = (List<String>) myMultiCastConfig.get(i).get("members");
        	groups.put(groupName, new Group(groupName, groupMembers));
        }
    }
    
}
