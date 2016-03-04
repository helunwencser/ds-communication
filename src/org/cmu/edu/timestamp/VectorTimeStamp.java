package org.cmu.edu.timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.cmu.edu.messagePasser.Config;

public class VectorTimeStamp extends TimeStamp  implements Serializable{
	
	private List<Integer> vector = new ArrayList<Integer>();
	
	public VectorTimeStamp(){
		for(int i = 0; i < Config.getNodeSize(); i++){
			this.vector.add(0);
		}
	}

	public List<Integer> getVector() {
		return vector;
	}
	
	public void setValue(int index, int value){
		this.vector.set(index, value);
	}
	
	public int getValueAt(int index){
		if(index < 0 || index >= this.vector.size()){
			return -1;
		}
		return this.vector.get(index);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("vector time stamp detail:\t");
		for(int v : this.vector)
			sb.append(v + " ");
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	@Override
	public TimeStamp generateNextTimeStamp() {
		// TODO Auto-generated method stub
		this.vector.set(Config.getIndex(), this.vector.get(Config.getIndex()) + 1);
		VectorTimeStamp timeStamp = new VectorTimeStamp();
		for(int i = 0; i < this.vector.size(); i++)
			timeStamp.vector.set(i, this.vector.get(i));
		return timeStamp;
	}


	@Override
	public RelationShip getRelationShip(TimeStamp timeStamp) {
		List<Integer> comparedVector = ((VectorTimeStamp)timeStamp).vector;
		int equal = 0, less = 0, greater = 0;
		for(int i = 0; i < Config.getNodeSize(); i++){
			if(this.vector.get(i) == comparedVector.get(i)){
				equal++;
			}else if(this.vector.get(i) < comparedVector.get(i)){
				less++;
			}else{
				greater++;
			}
		}
		if(equal == Config.getNodeSize()){
			return RelationShip.Equal;
		}else if((equal + less) == Config.getNodeSize()){
			return RelationShip.HappenBefore;
		}else if((equal + greater) == Config.getNodeSize()){
			return RelationShip.HappenAfter;
		}else{
			return RelationShip.Concurrent;
		}
	}

	@Override
	public TimeStamp getCopy() {
		// TODO Auto-generated method stub
		VectorTimeStamp copy = new VectorTimeStamp();
		for(int i = 0; i < this.vector.size(); i++){
			copy.setValue(i, this.vector.get(i));
		}
		return copy;
	}

	@Override
	public boolean isEqual(TimeStamp timeStamp) {
		// TODO Auto-generated method stub
		VectorTimeStamp vectorTimeStamp = (VectorTimeStamp) timeStamp;
		for(int i = 0; i < this.vector.size(); i++){
			if(this.vector.get(i) != vectorTimeStamp.getValueAt(i)){
				return false;
			}
		}
		return true;
	}

}
