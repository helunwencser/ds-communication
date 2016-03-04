package org.cmu.edu.clock;

import java.io.Serializable;

import org.cmu.edu.timestamp.LogicalTimeStamp;
import org.cmu.edu.timestamp.TimeStamp;

public class LogicalClock extends ClockService implements Serializable{

	private LogicalTimeStamp timeStamp;
	
	public LogicalClock() {
		super();
		this.timeStamp = new LogicalTimeStamp();
	}

	@Override
	public TimeStamp generateNextTimeStamp() {
		// TODO Auto-generated method stub
		return this.timeStamp.generateNextTimeStamp();
	}

	@Override
	public TimeStamp getTimeStamp() {
		// TODO Auto-generated method stub
		return this.timeStamp;
	}

	@Override
	public void updateTimeStamp(TimeStamp timeStamp) {
		int newLocalTime = ((LogicalTimeStamp)timeStamp).getLocalTime();
		this.timeStamp.setLocalTime(Math.max(newLocalTime, this.timeStamp.getLocalTime() + 1));
	}
}
