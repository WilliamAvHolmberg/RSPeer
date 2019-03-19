package com.nex.communication.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import com.nex.script.items.RequiredItem;
import com.nex.task.SkillTask;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

import com.nex.communication.NexHelper;
import com.nex.communication.message.DisconnectMessage;
import com.nex.communication.message.NexMessage;
import com.nex.script.Nex;
import com.nex.script.handler.TaskHandler;
import com.nex.task.NexTask;
import com.nex.task.actions.mule.PrepareForMuleDeposit;
import com.nex.task.mule.DepositToPlayerTask;
import com.nex.task.mule.Mule;
import com.nex.task.mule.PrepareForMuleDepositTask;
import com.nex.task.mule.WithdrawFromPlayerTask;

public class MuleRequest extends NexRequest {

	public MuleRequest(String respond) {
		super(respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		if ((TaskHandler.getCurrentTask() != null && !(TaskHandler.getCurrentTask() instanceof WithdrawFromPlayerTask)&& !(TaskHandler.getCurrentTask() instanceof DepositToPlayerTask)) || TaskHandler.getCurrentTask() == null) {

			Log.fine("Send a Mule Request");
			String[] nextRequest = respond.split(":");
			String muleType = nextRequest[0].toLowerCase();
			String itemID = nextRequest[1];
			String amount = nextRequest[2];

			println(out, "mule_request:" + itemID + ":" + amount + ":" + Players.getLocal().getName() + ":"
					+ Worlds.getCurrent() + ":" + muleType);
			String respond = in.readLine();
			Log.fine("mule respond: " + respond);
			String[] parsedRespond = respond.split(":");
			if (parsedRespond[0].equals("SUCCESSFUL_MULE_REQUEST")) {
				handleSuccessfullMuleRespond(parsedRespond, Integer.parseInt(itemID), Integer.parseInt(amount));
			} else if (parsedRespond[0].equals("MULE_BUSY")) {
				Log.fine("mule is busy atm. lets sleep for 15sec and see if available");
				Time.sleep(15000);
			} else {
				Log.fine("no mule available");
				NexHelper.pushMessage(new DisconnectMessage(null));
			}
		}

	}

	private void handleSuccessfullMuleRespond(String[] parsedRespond, int itemID, int amount) {
		Log.fine("successfull!");
		String muleName = parsedRespond[1];
		String world = parsedRespond[2];
		String muleType = parsedRespond[3].toLowerCase();

		long currentTime = System.currentTimeMillis();
		NexTask newTask = null;
		switch (muleType) {
		case "mule_deposit":
			newTask = new DepositToPlayerTask(Integer.parseInt(world), itemID, amount,
					muleName.toLowerCase(), Area.surrounding(Players.getLocal().getPosition(), 7));

			newTask.setBreakAfterTime(10);
			break;
		case "mule_withdraw":
			newTask = new WithdrawFromPlayerTask(Integer.parseInt(world), itemID, amount,
					muleName.toLowerCase(), Area.surrounding(Players.getLocal().getPosition(), 7));
			newTask.setBreakAfterTime(10);
			break;
		}
		if (newTask != null && (TaskHandler.getCurrentTask() instanceof PrepareForMuleDepositTask)) {
			TaskHandler.removeTask();
			TaskHandler.addTaskAndResetStack(newTask);
		}else {
			TaskHandler.addTaskAndResetStack(newTask);
		}

	}

}
