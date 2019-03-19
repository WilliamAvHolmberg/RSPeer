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
	public void execute(PrintWriter out, BufferedReader in) throws IOException, InterruptedException {
		Log.fine("Sent Banned Message");
		println(out, "banned:1");
		Nex.SHOULD_RUN = false;
		Thread.sleep(200);//Allow a small time for the message to definitely send
		System.exit(1);
	}

}
