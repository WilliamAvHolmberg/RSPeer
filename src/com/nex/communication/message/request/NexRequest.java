package com.nex.communication.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.function.BooleanSupplier;

import com.nex.communication.message.NexMessage;

public abstract class NexRequest extends NexMessage{

	
	public NexRequest(String respond) {
		super(respond);
	}

}
