package de.sten.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.sten.client.SMSService;
import de.sten.shared.SMSObject;
import de.sten.shared.SMSResponse;

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
public class SMSServiceImpl extends RemoteServiceServlet implements
		SMSService {

	public SMSResponse sendSMS(SMSObject input)  {
		SMSResponse respi = new SMSResponse();
		String token = null;
		try {
			token = getSTSToken(input.getUsername(), input.getPassword());
		} catch (IOException e) {
			System.out.println("exception while fetching token");
			e.printStackTrace();
		}
		String s = null;
		try {
			 s =doSend(input, token);
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
respi.setSomemessage("server respnse was :"+s);
	
		
		return respi;
	}



	private String doSend(SMSObject input, String token)
			throws MalformedURLException, UnsupportedEncodingException,
			IOException {
		URLFetchService urlFetchService = URLFetchServiceFactory
		.getURLFetchService();
		// neuen HTTP-Request an den Telekom SMS Server vorbereiten; diesmal ein
		// POST Request,
		// siehe API Doku
		String environment = "production";
		URL url = new URL(
				"https://gateway.developer.telekom.com/p3gw-mod-odg-sms/rest/"+environment+"/sms?");
		HTTPRequest httpRequest = new HTTPRequest(url, HTTPMethod.POST);
		// dem POST einen Header zur Authentifizierung hinzufügen, der unseren
		// Token enthält
		String authheader = "TAuth realm=\"https://odg.t-online.de\",tauth_token=\""
				+ token + "\"";
		httpRequest.addHeader(new HTTPHeader("Authorization", authheader));

		// Nummer, Absender und Nachricht als application/x-www-form-urlencoded
		// String speichern
		// hätten wir auch gleich am Anfang machen können, aber so ist es
		// übersichtlicher;
		// ist vorallem für die Nachricht wichtig, da sie Parameter Anweisungen
		// wie & oder ?
		// oder Umlaute und Leerzeichen enthalten könnte
		String number2 = URLEncoder.encode(input.getNumbers().get(0), "UTF-8");
		String message2 = URLEncoder.encode(input.getMessage(), "UTF-8");
		String originator2 = URLEncoder.encode(input.getOriginator(), "UTF-8");

		// dem Post die SMS-Daten als Payload geben
		String payload = "number=" + number2 + "&message=" + message2
				+ "&originator=" + originator2;
		httpRequest.setPayload(payload.getBytes());

		// die Antwort holen und als String speichern
		HTTPResponse response2 = urlFetchService.fetch(httpRequest);
		String responsestr2 = new String(response2.getContent());
		System.out.println("got response from sms server");
		// wenn als HTTP Antwort OK kam, geben wir eine positive Versandmeldung
		// aus
		if (response2.getResponseCode() == 200) {
			System.out.println("got response 200from sms server");

			System.out.println(
							"<p>Hallo "
									+ input.getOriginator()
									+ ",<br />Deine Nachricht \""
									+ input.getMessage()
									+ "\" wurde an die Nummer "
									+ input.getNumbers().get(0)
									+ " gesendet :-)<br />Die Antwort des ODG Servers war: "
									+ responsestr2
									+ "</p><br /><a href=\"./\">zurück</a>");
		}
		// wenn nicht HTTP OK zurück kam, geben wir eine Fehlermeldung mit der
		// Serverantwort aus
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
	
	
	
	private String getSTSToken(String username, String password) throws IOException{
		
		// einen Get-Request an den Authtifizierungsserver vorbereiten;
		// wir nutzen den URLFetchService aus der App Engine API um den URL
		// aufzurufen
		URL url = new URL("https://sts.idm.telekom.com/rest-v1/tokens/odg");
		URLFetchService urlFetchService = URLFetchServiceFactory
				.getURLFetchService();
		HTTPRequest httpRequest = new HTTPRequest(url, HTTPMethod.GET);

		// unseren Nutzernamen und Passwort (Base64-kodiert; Basic
		// Authentication
		// als Request-Header hinzufügen (siehe Developer Garden API
		// Dokumentation)
		String creds = username + ":" + password;
		String headerstr = "Basic " + Base64.encode(creds.getBytes("UTF-8"));
		httpRequest.addHeader(new HTTPHeader("Authorization", headerstr));

		// den Request ausführen und die Antwort als String speichern
		HTTPResponse response = urlFetchService.fetch(httpRequest);
		String responsestr = new String(response.getContent());

		// Wenn wir als HTTP Antwort nicht OK zurückbekommen, ist etwas schief
		// gelaufen
		// wir geben eine Fehlermeldung aus und beenden die Servlet-Ausführung
		if (response.getResponseCode() != 200) {
			System.out.println(
							"<p> Problem beim abholen des Security-Tokens."
									+ " Es scheint etwas mit Deinen Zugangdaten nicht zu stimmen.<br />"
									+ " Der HTTP Statuscode war: "
									+ response.getResponseCode() + "<br />"
									+ "Die Antwort des ODG Servers war: "
									+ responsestr + "</p>"
									+ "<br /><a href=\"./\">zurück</a>");
			return null;
		}
		// Wir haben eine positive Antwort vom STS Server
		// jetzt holen wir uns den eigentlichen Token als String aus der Server
		// Antwort

		// ein regulärer Ausdruck, der nach dem Token sucht
		Pattern pattern = Pattern.compile("\"token\":\"(.*)\"");
		Matcher matcher = pattern.matcher(responsestr);
		// da wir eine OK Antwort vom Server hatten, sollten wir eigentlich auch
		// einen Token bekommen haben
		// wenn doch (aus unerfindlichen Gründen) etwas schief gehen sollte,
		// geben wir wieder eine Fehlermeldung aus
		if (!matcher.find()) {
			System.out.println(
							"<p> Dieser Fehler sollte nicht vorkommen! Problem beim abholen des Security-Tokens."
									+ " Es scheint etwas mit Deinen Zugangdaten nicht zu stimmen.<br />"
									+ " Der HTTP Statuscode war: "
									+ response.getResponseCode()
									+ "<br />"
									+ "Die Antwort des ODG Servers war: "
									+ responsestr
									+ "</p>"
									+ "<br /><a href=\"./\">zurück</a>");
			return null;
		}

		// unseren Token als String speichern
		String SecToken = matcher.group(1);
		System.out.println("got token: "+SecToken);
		
		return SecToken;
	}
}
