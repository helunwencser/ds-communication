package org.cmu.edu.clock;

import java.io.Serializable;

import org.cmu.edu.timestamp.TimeStamp;

public abstract class ClockService implements Serializable{

	public ClockService(){
	}
	
	/*
	 * generate the next time stamp and return a copy of it
	 * @return the next time stamp
	 * */
	public abstract TimeStamp generateNextTimeStamp();
	
	/*
	 * get the time stamp
	 * @return the time stamp
	 * */
	public abstract TimeStamp getTimeStamp();

	/*
	 * update the time stamp
	 * @param	timeStamp	the new timeStamp
	 * */
	public abstract void updateTimeStamp(TimeStamp timeStamp);
}
