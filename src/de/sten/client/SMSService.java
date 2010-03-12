package de.sten.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.sten.shared.SMSObject;
import de.sten.shared.SMSResponse;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("smsserv")
public interface SMSService extends RemoteService {
	SMSResponse sendSMS(SMSObject smsobj);

}
