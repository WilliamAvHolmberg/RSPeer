package com.nex.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.nex.task.SkillTask;
import com.nex.task.tanning.TanningTask;
import org.rspeer.ui.Log;

import com.nex.communication.message.NexMessage;
import com.nex.script.handler.TaskHandler;
import com.nex.task.NexTask;

public class TannerRespond extends TaskRespond {

    public TannerRespond(String respond) {
        super(respond);

    }

    @Override
    public void execute(PrintWriter out, BufferedReader in) throws IOException {
        String[] parsed = respond.split(":");
        String currentTaskID = parsed[3];
        String parsedBreakCondition = parsed[4];
        String breakAfter = parsed[5];
        String parsedLevelGoal = parsed[6];
        int hideID = Integer.parseInt(parsed[7]);
        hideID = 1739;
        int withdrawQty = Integer.parseInt(parsed[8]);

        SkillTask newTask = new TanningTask(hideID, withdrawQty);
        newTask.setTaskID(currentTaskID);
        setBreakConditions(newTask, parsedBreakCondition, breakAfter, parsedLevelGoal);
        TaskHandler.addPrioritizedTask(newTask);
    }
}
