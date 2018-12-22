package com.nex.communication.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import org.rspeer.ui.Log;

public abstract class NexMessage {
	

	protected String respond;
	
	
	public NexMessage( String respond) {
		this.respond = respond;
		Log.info("new mess created: " + respond);
	}
	public abstract void execute(PrintWriter out, BufferedReader in) throws IOException;
	
}
