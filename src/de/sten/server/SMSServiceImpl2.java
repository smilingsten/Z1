package de.sten.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.sten.client.SMSService;
import de.sten.shared.SMSObject;
import de.sten.shared.SMSResponse;
import de.sten.shared.STSException;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.repackaged.com.google.common.util.Base64;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SMSServiceImpl2 extends RemoteServiceServlet implements
		SMSService {

	public SMSResponse sendSMS(SMSObject input)  {
		System.out.println("heellooo from impl2");
		String username = input.getUsername();
		String password = input.getPassword();
		String message = input.getMessage();
		String originator = input.getOriginator();
		ArrayList<String> numbers = input.getNumbers();
		
		SMSResponse respi = new SMSResponse();
		String token = null;
		try {
			token = getSTSToken(username, password);
		} catch (IOException e) {
			System.out.println("exception while fetching token");
			e.printStackTrace();
			respi.setHttpcode(-1);
			respi.setHttpresponsemsg("none");
			respi.setSomemessage("IO Fehler beim Token holen. Etwas stimmt anscheinend mit deiner Connection nicht");
			
		} catch (STSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			respi.setHttpcode(e.getHttpCode());
			respi.setTserverresponse(e.getTServerMessage());
			respi.setSomemessage(e.getMyMessage());
		}
	
		
		for (int i = 0; i < numbers.size(); i++) {
			try {
				doSend(numbers.get(i), originator, message, token);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
		
		return respi;
	}



	private String doSend(String number, String originator, String message, String token)
			throws MalformedURLException, UnsupportedEncodingException,
			IOException {
		URLFetchService urlFetchService = URLFetchServiceFactory
		.getURLFetchService();
		String environment = "production";
		URL url = new URL(
				"https://gateway.developer.telekom.com/p3gw-mod-odg-sms/rest/"+environment+"/sms?");
		HTTPRequest httpRequest = new HTTPRequest(url, HTTPMethod.POST);
		String authheader = "TAuth realm=\"https://odg.t-online.de\",tauth_token=\""
				+ token + "\"";
		httpRequest.addHeader(new HTTPHeader("Authorization", authheader));

		String number2 = URLEncoder.encode(number, "UTF-8");
		String message2 = URLEncoder.encode(message, "UTF-8");
		String originator2 = URLEncoder.encode(originator, "UTF-8");
		String payload = "number=" + number2 + "&message=" + message2
				+ "&originator=" + originator2;
		httpRequest.setPayload(payload.getBytes());
		HTTPResponse response2 = urlFetchService.fetch(httpRequest);
		String responsestr2 = new String(response2.getContent());
		System.out.println("got response from sms server");
		if (response2.getResponseCode() == 200) {
			System.out.println("got response 200from sms server");

			System.out.println(
							"<p>Hallo "
									+ originator2
									+ ",<br />Deine Nachricht \""
									+ message2
									+ "\" wurde an die Nummer "
									+ number2
									+ " gesendet :-)<br />Die Antwort des ODG Servers war: "
									+ responsestr2
									+ "</p><br /><a href=\"./\">zurück</a>");
		}
		else {					System.out.println("not got response from sms server");

			System.out.println(

					"<p>Es gab ein Problem beim Senden der Nachricht!<br />Der Response Code war: "
							+ response2.getResponseCode()
							+ "<br />Die Antwort des ODG Servers war: "
							+ responsestr2 + "</p>"
							+ "<br /><a href=\"./\">zurück</a>");
		}
		return responsestr2;
	}
	
	
	
	private String getSTSToken(String username, String password) throws IOException, STSException{
		
	
		URL url = new URL("https://sts.idm.telekom.com/rest-v1/tokens/odg");
		URLFetchService urlFetchService = URLFetchServiceFactory
				.getURLFetchService();
		HTTPRequest httpRequest = new HTTPRequest(url, HTTPMethod.GET);

	
		String creds = username + ":" + password;
		String headerstr = "Basic " + Base64.encode(creds.getBytes("UTF-8"));
		httpRequest.addHeader(new HTTPHeader("Authorization", headerstr));

		HTTPResponse response = urlFetchService.fetch(httpRequest);
		String responsestr = new String(response.getContent());

		if (response.getResponseCode() != 200) {
			String error = (
							"<p> Problem beim abholen des Security-Tokens."
									+ " Es scheint etwas mit Deinen Zugangdaten nicht zu stimmen.<br />"
									+ " Der HTTP Statuscode war: "
									+ response.getResponseCode() + "<br />"
									+ "Die Antwort des ODG Servers war: "
									+ responsestr + "</p>"
									+ "<br /><a href=\"./\">zurück</a>");
			System.out.println(error);
			STSException e = new STSException();
			e.setHttpCode(response.getResponseCode());
			e.setTServerMessage(responsestr);
			e.setMyMessage(error);
			throw e;
		}
	

		Pattern pattern = Pattern.compile("\"token\":\"(.*)\"");
		Matcher matcher = pattern.matcher(responsestr);

		if (!matcher.find()) {
			String error =(
							"<p> Dieser Fehler sollte nicht vorkommen! Problem beim abholen des Security-Tokens."
									+ " Es scheint etwas mit Deinen Zugangdaten nicht zu stimmen.<br />"
									+ " Der HTTP Statuscode war: "
									+ response.getResponseCode()
									+ "<br />"
									+ "Die Antwort des ODG Servers war: "
									+ responsestr
									+ "</p>"
									+ "<br /><a href=\"./\">zurück</a>");
			System.out.println(error);
			STSException e = new STSException();
			e.setHttpCode(response.getResponseCode());
			e.setTServerMessage(responsestr);
			e.setMyMessage(error);
			throw e;
		}

		String SecToken = matcher.group(1);
		System.out.println("got token: "+SecToken);
		
		return SecToken;
	}
}
