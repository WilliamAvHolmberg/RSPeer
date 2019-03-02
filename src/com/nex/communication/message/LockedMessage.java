package com.nex.communication.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import com.nex.script.Nex;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.ui.Log;


public class LockedMessage extends NexMessage {

	public LockedMessage(String respond) {
		super(respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		Log.fine("Sent Locked Message");
		out.println("locked:1");
		Time.sleep(200);//Allow a small time for the message to definitely send
	}

}
