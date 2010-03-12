package de.sten.shared;

import java.io.Serializable;

public class SMSResponse implements Serializable{

	String httpresponsemsg, tserverresponse, somemessage;
	public void setHttpresponsemsg(String httpresponsemsg) {
		this.httpresponsemsg = httpresponsemsg;
	}
	public void setTserverresponse(String tserverresponse) {
		this.tserverresponse = tserverresponse;
	}
	public void setSomemessage(String somemessage) {
		this.somemessage = somemessage;
	}
	public void setHttpcode(int httpcode) {
		this.httpcode = httpcode;
	}
	public String getHttpresponsemsg() {
		return httpresponsemsg;
	}
	public String getTserverresponse() {
		return tserverresponse;
	}
	public String getSomemessage() {
		return somemessage;
	}
	public int getHttpcode() {
		return httpcode;
	}
	int httpcode;

	
}
