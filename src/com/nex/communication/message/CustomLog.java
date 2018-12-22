package com.nex.communication.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;



public class CustomLog extends NexMessage {


	public CustomLog(String respond) {
		super( respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		out.println(respond);
		in.readLine(); //will always return ok. Therefor nothing shall be done.
		
	}
}
