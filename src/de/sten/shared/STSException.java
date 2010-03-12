package de.sten.shared;

import java.io.Serializable;

public class STSException extends Exception implements Serializable{
	
	public String getHttpMessage() {
		return HttpMessage;
	}
	public void setHttpMessage(String httpMessage) {
		HttpMessage = httpMessage;
	}
	public String getMyMessage() {
		return MyMessage;
	}
	public void setMyMessage(String myMessage) {
		MyMessage = myMessage;
	}
	public String getTServerMessage() {
		return TServerMessage;
	}
	public void setTServerMessage(String tServerMessage) {
		TServerMessage = tServerMessage;
	}
	public int getHttpCode() {
		return HttpCode;
	}
	public void setHttpCode(int httpCode) {
		HttpCode = httpCode;
	}
	String HttpMessage, MyMessage, TServerMessage;
	int HttpCode;


}
