package de.sten.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

import de.sten.shared.SMSObject;
import de.sten.shared.SMSResponse;


//main class with entry point
public class Z1 implements EntryPoint, ClickHandler {
	private final SMSServiceAsync smsService = GWT
	.create(SMSService.class);
	
		TextBox namebox, numbox, originbox;
		PasswordTextBox pwbox;
		TextArea msgbox;
		HashMap<Date, TextBox> numfields;
		HashMap<Date, Button> rembuttons;
		Label lbl;
	
		public void onModuleLoad() {
	numfields = new HashMap<Date, TextBox>();
	rembuttons = new HashMap<Date, Button>();
	namebox = new TextBox();
	lbl = new Label("counter...");
	pwbox = new PasswordTextBox();
	numbox = new TextBox();
	originbox = new TextBox();
	msgbox = new TextArea();
	msgbox.setSize("25em", "10em");
	countMsgChars();
	msgbox.addKeyUpHandler(new KeyUpHandler() {
		
		public void onKeyUp(KeyUpEvent event) {
			countMsgChars();
			
		}
	});
	RootPanel.get("namef").add(namebox);
	RootPanel.get("pwf").add(pwbox);
	final Date d = new Date();
	RootPanel.get("numberf").add(numbox);
	numfields.put(d, numbox);
	Button removebtn = new Button("-");
	removebtn.addClickHandler(new ClickHandler() {
		
		public void onClick(ClickEvent event) {
			numfields.get(d).removeFromParent();
			rembuttons.get(d).removeFromParent();
			numfields.remove(d);
			rembuttons.remove(d);
			if (rembuttons.size()<=1){
				  Iterator i = rembuttons.entrySet().iterator(); 
		          while ( i.hasNext() ) { 
		          Map.Entry entry = (Map.Entry) i.next(); 
		          Button btn = (Button)entry.getValue(); 
		          btn.setVisible(false);
		        }
			}
			
		}
	});
	removebtn.setVisible(false);
	rembuttons.put(d, removebtn);
	RootPanel.get("numberf").add(removebtn);
	RootPanel.get("originf").add(originbox);
	RootPanel.get("msgf").add(msgbox);
	RootPanel.get("countlabel").add(lbl);
	final Button sendButton = new Button("Send");
	RootPanel.get("sendb").add(sendButton);
	sendButton.addClickHandler(this);
	final Button numplusbtn = new Button("+");
	numplusbtn.addClickHandler(new ClickHandler() {
		
		public void onClick(ClickEvent event) {
			  Iterator i = rembuttons.entrySet().iterator(); 
	          while ( i.hasNext() ) { 
	          Map.Entry entry = (Map.Entry) i.next(); 
	          Button btn = (Button)entry.getValue(); 
	          btn.setVisible(true);
	        }
			final Date date = new Date();
			Button btn = new Button("-");
			btn.addClickHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
					numfields.get(date).removeFromParent();
					rembuttons.get(date).removeFromParent();
					numfields.remove(date);
					rembuttons.remove(date);
					if (rembuttons.size()<=1){
						  Iterator i = rembuttons.entrySet().iterator(); 
				          while ( i.hasNext() ) { 
				          Map.Entry entry = (Map.Entry) i.next(); 
				          Button btn = (Button)entry.getValue(); 
				          btn.setVisible(false);
				        }
					}
					
				}
			});
			TextBox tmp = new TextBox();			
			numfields.put(date, tmp);
			rembuttons.put(date, btn);
			RootPanel.get("numberf").add(tmp);
			RootPanel.get("numberf").add(btn);		
		}
	});

	RootPanel.get("numplusb").add(numplusbtn);	
	}

	public void onClick(ClickEvent event) {
		String uname = namebox.getText().trim();
		String pw = pwbox.getText();
		String origin = originbox.getText();
	    String message = msgbox.getText();
		ArrayList<String> numbers = new ArrayList<String>();
		 Iterator i = numfields.entrySet().iterator(); 
         while ( i.hasNext() ) { 
         Map.Entry entry = (Map.Entry) i.next(); 
         TextBox box = (TextBox)entry.getValue(); 
         numbers.add(box.getText().trim());
         
	}
         System.out.println("name: "+uname+"\npw: "+pw+"\norigin: "+origin+"\nmessage: "+message);
         for (int j = 0; j < numbers.size(); j++) {
			System.out.println("\nnumber "+j+": "+numbers.get(j));
			
		}
         SMSObject smso = new SMSObject();
         smso.setMessage(message);
         smso.setNumbers(numbers);
         smso.setOriginator(origin);
         smso.setPassword(pw);
         smso.setUsername(uname);
         send(smso);
	}

	void send(SMSObject smsobj){
		smsService.sendSMS(smsobj,
				new AsyncCallback<SMSResponse>() {
					public void onFailure(Throwable caught) {
						System.out.println("rpc failure");
					}

					public void onSuccess(SMSResponse result) {
						System.out.println("rpc success");
						System.out.println("answer was: "+result.getSomemessage());

					}
				});
		
		
	}

	private void countMsgChars() {
		char[] specialchars= {'|','^', '€', '{' ,'}', '[', ']', '~',0x0a};
		String msg = msgbox.getText();
		int charcnt = msg.length();
		char[] chars = msg.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			
				for (int j = 0; j < specialchars.length; j++) {
					if (chars[i] == specialchars[j]){
						System.out.println("specialchar found");
						charcnt++;
					}
				
			}
			
		}
		lbl.setText("Nachricht hat "+charcnt+" Zeichen");
	}
	
	
}
