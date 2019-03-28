package com.nex.communication.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.nex.communication.message.respond.*;
import com.nex.script.Nex;
import com.nex.script.handler.TaskHandler;
import com.nex.task.actions.mule.CheckIfWeShallSellItems;
import com.nex.task.mule.PrepareForMuleDepositTask;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.ui.Log;

import com.nex.communication.NexHelper;
import com.nex.communication.message.DisconnectMessage;
import com.nex.script.Quest;

public class RequestTask extends NexRequest {

	public RequestTask(String respond) {
		super(respond);
	}

	static boolean alreadySent = false;
	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		String string = "skills;";
		for (Skill skill : Skill.values()) {
			int level = Skills.getCurrentLevel(skill);
			if (level > 1 || !alreadySent)
				string += skill + "," + level + ";";
		}
		string += "QP" + "," + Quest.getQuestPoints() + ";";
		String quests = "quests;";
		for (Quest quest : Quest.values()) {
			quests += quest.name() + "," + quest.isCompleted() + ";";
		}
		String request = "task_request:1:" + string + ":" + quests;
		println(out, request);
		String respond = in.readLine();
		Log.fine("got respond from task_request:" + respond);
		handleRespond(respond);
		alreadySent = true;
	}

	static long timeAskedToDC = 0;
	static String lastTask;
	private void handleRespond(String respond) {
		if (respond.contains("TASK_RESPOND:0") || respond.contains("DISCONNECT")) {
			if (lastTask != null && lastTask.startsWith("MULE"))
			{
				int coins = Inventory.getCount(true, 995);
				if(RequestAccountInfo.account_type != null && RequestAccountInfo.account_type.equals("MULE") && coins > 100000 && TaskHandler.available_tasks.isEmpty()) {
					Log.fine("Idle Mule");
					if (coins > Nex.MULE_THRESHOLD_NOW()) {
						TaskHandler.addTaskAndResetStack(new PrepareForMuleDepositTask());
						return;
					}
				}
				//If we are a mule, lets just hang around for 2 minutes incase another task is ready
				if(timeAskedToDC == 0) {
					timeAskedToDC = System.currentTimeMillis();
					return;
				}
				else if(System.currentTimeMillis() - timeAskedToDC < 120000) {
					Time.sleep(10000);
					return;
				}
			}
			if (respond.contains("DISCONNECT"))
				NexHelper.pushMessage(new DisconnectMessage("Told to Disconnect"));
			else
				NexHelper.pushMessage(new DisconnectMessage("No task supplied"));
		} else {
			timeAskedToDC = 0;
			String[] parsedRespond = respond.split(":");
			String taskType = parsedRespond[2];
			lastTask = taskType;
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
			case "TANNER":
				NexHelper.pushMessage(new TannerRespond(respond));
				break;
			case "FISHING":
				NexHelper.pushMessage(new FishingRespond(respond));
				break;
			case "CUSTOM":
				NexHelper.pushMessage(new CustomTaskRespond(respond));
				break;
			case "QUEST":
				NexHelper.pushMessage(new QuestRespond(respond));
			break;
			}
		}
	}

}
