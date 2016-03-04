package org.cmu.edu.exception;

public class NotComparableException extends Exception{
	
	private String errorMessage = "the logical time stamp is not comparable";
	
	public NotComparableException(){
		
	}
	
	public String getErrorMessage(){
		return this.errorMessage;
	}
}
