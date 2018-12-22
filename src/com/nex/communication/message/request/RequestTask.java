package com.nex.communication.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.ui.Log;

import com.nex.communication.NexHelper;
import com.nex.communication.message.DisconnectMessage;
import com.nex.communication.message.NexMessage;
import com.nex.communication.message.respond.CombatRespond;
import com.nex.communication.message.respond.MiningRespond;
import com.nex.communication.message.respond.MuleRespond;
import com.nex.communication.message.respond.QuestRespond;
import com.nex.communication.message.respond.WoodcuttingRespond;
import com.nex.script.Quest;

public class RequestTask extends NexRequest {

	public RequestTask(String respond) {
		super(respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		String string = "skills;";
		for (Skill skill : Skill.values()) {
			string += skill + "," + Skills.getCurrentLevel(skill) + ";";
		}
		String quests = "quests;";
		for (Quest quest : Quest.values()) {
			quests += quest.name() + "," + quest.isCompleted() + ";";
		}
		String request = "task_request:1:" + string + ":" + quests;
		out.println(request);
		String respond = in.readLine();
		Log.fine("got respond from task_request:" + respond);
		handleRespond(respond);
	}

	private void handleRespond(String respond) {
		if (respond.contains("TASK_RESPOND:0") || respond.contains("DISCONNECT")) {
			NexHelper.pushMessage(new DisconnectMessage("Failed to get task"));
		} else {
			String[] parsedRespond = respond.split(":");
			String taskType = parsedRespond[2];

			switch (taskType) {
			case "MINING":
				NexHelper.pushMessage(new MiningRespond(respond));
				break;
			case "WOODCUTTING":
				NexHelper.pushMessage(new WoodcuttingRespond(respond));
				break;
			case "COMBAT":
				NexHelper.pushMessage(new CombatRespond(respond));
				break;
			case "MULE_WITHDRAW":
				NexHelper.pushMessage(new MuleRespond(respond));
				break;
			case "MULE_DEPOSIT":
				NexHelper.pushMessage(new MuleRespond(respond));
				break;
			case "QUEST":
				NexHelper.pushMessage(new QuestRespond(respond));
			break;
			}
		}
	}

}
