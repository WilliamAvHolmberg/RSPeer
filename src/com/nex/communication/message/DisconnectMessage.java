package com.nex.communication.message;

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
		System.exit(1);
	}

}
