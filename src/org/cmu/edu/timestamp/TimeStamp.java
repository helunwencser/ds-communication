package org.cmu.edu.timestamp;

import java.io.Serializable;

public abstract class TimeStamp  implements Serializable{

	/*
	 * @return the string representation of this time stamp
	 * */
	public abstract String toString();
	
	/*
	 * @return the next time stamp
	 * */
	public abstract TimeStamp generateNextTimeStamp();
	
	
	/*
	 * @return	the relationship between this and timestamp
	 * */
	public abstract RelationShip getRelationShip(TimeStamp timeStamp);
	
	/*
	 * get the deep copy of this time stamp
	 * @return	the deep copy of this time stamp
	 * */
	public abstract TimeStamp getCopy();
	
	/*
	 * if this equals to timeStamp
	 * @return	return true if this equals to timeStamp; otherwise, return false
	 * */
	public abstract boolean isEqual(TimeStamp timeStamp);
}
