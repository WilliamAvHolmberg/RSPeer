package com.nex.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.function.BooleanSupplier;

import org.rspeer.ui.Log;

import com.nex.communication.message.NexMessage;
import com.nex.script.Quest;
import com.nex.script.handler.TaskHandler;
import com.nex.task.NexTask;
import com.nex.task.quests.CooksAssistantQuest;
import com.nex.task.quests.GoblinDiplomacyQuest;
import com.nex.task.quests.RomeoAndJulietQuest;

public class QuestRespond extends TaskRespond {

	public QuestRespond(String respond) {
		super(respond);

	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		NexTask newTask = null;
		String[] parsed = respond.split(":");
		String currentTaskID = parsed[3];
		String questName = parsed[4];
		if (questName.contains(Quest.GOBLIN_DIPLOMACY.name())) {
			newTask = new GoblinDiplomacyQuest();
		} else if (questName.contains(Quest.ROMEO_JULIET.name())) {
			newTask = new RomeoAndJulietQuest();
		} else if (questName.contains(Quest.COOKS_ASSISTANT.name())) {
			newTask = new CooksAssistantQuest();
		} else {

		}
		Log.fine("new task quest");
		if (newTask != null) {
			newTask.setTaskID(currentTaskID);
			Log.fine("task is not null");
			TaskHandler.addPrioritizedTask(newTask);
		}
	}
}
