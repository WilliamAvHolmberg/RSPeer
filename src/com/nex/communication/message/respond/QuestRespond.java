package com.nex.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.function.BooleanSupplier;

import com.nex.task.quests.*;
import com.nex.utils.json.JsonObject;

import org.rspeer.ui.Log;

import com.nex.communication.message.NexMessage;
import com.nex.script.Quest;
import com.nex.script.handler.TaskHandler;
import com.nex.task.NexTask;

public class QuestRespond extends TaskRespond {

	public QuestRespond(String respond) {
		super(respond);

	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		NexTask newTask = null;
		JsonObject jsonRespond = JsonObject.readFrom(respond);
		String currentTaskID = jsonRespond.get("task_id").toString();
		String questName = jsonRespond.get("quest_name").asString();
		
		if (questName.contains(Quest.GOBLIN_DIPLOMACY.name())) {
			newTask = new GoblinDiplomacyQuest();
		} else if (questName.contains(Quest.ROMEO_JULIET.name())) {
			newTask = new RomeoAndJulietQuest();
		} else if (questName.contains(Quest.COOKS_ASSISTANT.name())) {
			newTask = new CooksAssistantQuest();
		} else if (questName.contains(Quest.IMP_CATCHER.name())) {
			newTask = new ImpCatcherQuest();
		} else if (questName.contains(Quest.DORICS_QUEST.name())) {
			newTask = new DoricsQuest();
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
