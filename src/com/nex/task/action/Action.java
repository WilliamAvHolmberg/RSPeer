package com.nex.task.action;

import com.nex.script.Nex;
import com.nex.script.handler.TaskHandler;
import com.nex.task.NexTask;

public abstract class Action {
	
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	public NexTask getCurrentTask() {
		return TaskHandler.getCurrentTask();
	}

}
