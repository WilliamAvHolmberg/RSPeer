package com.nex.communication.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import com.nex.script.Nex;



public class BannedMessage extends NexMessage {

	public BannedMessage(String respond) {
		super(respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		out.println("banned:1");
		Nex.SHOULD_RUN = false;
		System.exit(1);
	}

}
