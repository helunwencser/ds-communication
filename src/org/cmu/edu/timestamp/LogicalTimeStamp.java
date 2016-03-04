package org.cmu.edu.timestamp;

import java.io.Serializable;

import org.cmu.edu.exception.NotComparableException;

public class LogicalTimeStamp extends TimeStamp  implements Serializable{

	/* the time of this node */
	private int localTime;
	
	public LogicalTimeStamp(){
		this.localTime = 0;
	}
		
	public int getLocalTime() {
		return localTime;
	}

	public void setLocalTime(int localTime) {
		this.localTime = localTime;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String res = "logical time stamp detail:\t" + new Integer(this.localTime).toString();
		return res;
	}

	@Override
	public TimeStamp generateNextTimeStamp() {
		// TODO Auto-generated method stub
		this.localTime++;
		LogicalTimeStamp timeStamp = new LogicalTimeStamp();
		timeStamp.localTime = this.localTime;
		return timeStamp;
	}

	@Override
	public RelationShip getRelationShip(TimeStamp timeStamp) {
		// TODO Auto-generated method stub
		try {
			throw new NotComparableException();
		} catch (NotComparableException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getErrorMessage());
		}
		return RelationShip.Equal;
	}

	@Override
	public TimeStamp getCopy() {
		// TODO Auto-generated method stub
		LogicalTimeStamp copy = new LogicalTimeStamp();
		copy.setLocalTime(this.localTime);
		return copy;
	}

	@Override
	public boolean isEqual(TimeStamp timeStamp) {
		// TODO Auto-generated method stub
		LogicalTimeStamp logicalTimeStamp = (LogicalTimeStamp) timeStamp;
		return this.localTime == logicalTimeStamp.getLocalTime();
	}

}
