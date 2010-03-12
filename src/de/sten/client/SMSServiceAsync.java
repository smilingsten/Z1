package de.sten.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.sten.shared.SMSObject;
import de.sten.shared.SMSResponse;

public interface SMSServiceAsync {


	void sendSMS(SMSObject smsobj, AsyncCallback<SMSResponse> callback);

}
