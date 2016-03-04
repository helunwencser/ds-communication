package org.cmu.edu.mutualexclusion;

/*
 * the kind of message for mutual exclusion
 * */
public class MessageKindForME {
	/* request to enter critical section */
	public static String REQUEST = "request";
	
	/* vote for one request */
	public static String VOTE = "vote";
	
	/* exit critical section */
	public static String RELEASE = "release";
}
