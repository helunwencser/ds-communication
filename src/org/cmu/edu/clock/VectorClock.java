package org.cmu.edu.clock;

import java.io.Serializable;
import java.util.List;

import org.cmu.edu.messagePasser.Config;
import org.cmu.edu.timestamp.TimeStamp;
import org.cmu.edu.timestamp.VectorTimeStamp;

public class VectorClock extends ClockService implements Serializable{

	private VectorTimeStamp timeStamp;
	
	public VectorClock() {
		super();
		// TODO Auto-generated constructor stub
		this.timeStamp = new VectorTimeStamp();
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
	
	public void increaseTimeStamp(int index){
		if(index >= this.timeStamp.getVector().size()){
			return;
		}
		this.timeStamp.setValue(index, this.timeStamp.getVector().get(index) + 1);
	}

	@Override
	public void updateTimeStamp(TimeStamp timeStamp) {
		// TODO Auto-generated method stub
		List<Integer> vector = ((VectorTimeStamp)timeStamp).getVector();
		for(int i = 0; i < Config.getNodeSize(); i++){
			int newValue = Math.max(vector.get(i), this.timeStamp.getVector().get(i));
			this.timeStamp.setValue(i, newValue);
		}
	}
}
