package com.nex.communication.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import org.rspeer.ui.Log;

import com.nex.utils.json.JsonObject;

public abstract class NexMessage {
	

	protected String respond;
	
	
	public NexMessage( String respond) {
		this.respond = respond;
		Log.info("New Message Created: " + respond);
	}
	public abstract void execute(PrintWriter out, BufferedReader in) throws IOException, InterruptedException;
	public void println(PrintWriter out, String message){
		Log.info("SENT: " + message);
		out.println(message);
	}
	
}
