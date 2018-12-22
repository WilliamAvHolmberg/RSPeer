package com.nex.communication.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;



import com.nex.task.SkillTask;

public class TaskLog extends NexMessage {

	SkillTask currentTask;
	public TaskLog(SkillTask currentTask, String respond) {
		super(respond);
		this.currentTask = currentTask;
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		out.println(currentTask.getLog());
		in.readLine(); //will always return ok. Therefor nothing shall be done.
		
	}

}
