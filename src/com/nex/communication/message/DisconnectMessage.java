package com.nex.communication.message;

import org.rspeer.runetek.api.commons.Time;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;


public class DisconnectMessage extends NexMessage{

	public DisconnectMessage(String respond) {
		super(respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		out.println("puts:" + respond);
		Time.sleep(6000);//I like to see the reason, and be able to cancel a Disconnect
		System.exit(1);
	}

}
