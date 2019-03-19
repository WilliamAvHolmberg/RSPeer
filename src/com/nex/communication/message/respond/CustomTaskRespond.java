package com.nex.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.nex.task.SkillTask;
import com.nex.task.custom.CustomTask;
import com.nex.task.tanning.TanningTask;
import org.rspeer.ui.Log;

import com.nex.communication.message.NexMessage;
import com.nex.script.handler.TaskHandler;
import com.nex.task.NexTask;

public class CustomTaskRespond extends TaskRespond {

    public CustomTaskRespond(String respond) {
        super(respond);
    }

    @Override
    public void execute(PrintWriter out, BufferedReader in) throws IOException {
        String[] parsed = respond.split(":");
        String currentTaskID = parsed[3];
        String parsedBreakCondition = parsed[4];
        String breakAfter = parsed[5];
        String parsedLevelGoal = parsed[6];

        SkillTask newTask = new CustomTask();
        newTask.setTaskID(currentTaskID);
        setBreakConditions(newTask, parsedBreakCondition, breakAfter, parsedLevelGoal);
        TaskHandler.addPrioritizedTask(newTask);
    }
}
