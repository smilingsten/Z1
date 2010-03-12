package de.sten.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class SMSObject implements Serializable {

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public void setNumbers(ArrayList<String> numbers) {
		this.numbers = numbers;
	}

	String username, password, message, originator;
	ArrayList<String> numbers;
	
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getMessage() {
		return message;
	}

	public String getOriginator() {
		return originator;
	}

	public ArrayList<String> getNumbers() {
		return numbers;
	}

	
	

	
	
	
}
