package com.nex.communication.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import com.nex.script.Nex;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.ui.Log;


public class BannedMessage extends NexMessage {

	public BannedMessage(String respond) {
		super(respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		Log.fine("Sent Banned Message");
		out.println("banned:1");
		Time.sleep(200);//Allow a small time for the message to definitely send
		Nex.SHOULD_RUN = false;
		System.exit(1);
	}

}
