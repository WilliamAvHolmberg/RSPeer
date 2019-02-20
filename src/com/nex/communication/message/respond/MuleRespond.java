package com.nex.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


import com.nex.script.WebBank;
import com.nex.script.grandexchange.BuyItemHandler;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.ui.Log;

import com.nex.script.handler.TaskHandler;
import com.nex.task.NexTask;
import com.nex.task.mule.DepositToPlayerTask;

import com.nex.task.mule.WithdrawFromPlayerTask;

public class MuleRespond extends TaskRespond {

	public MuleRespond(String respond) {
		super(respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		String[] parsed = respond.split(":");
		String muleType = parsed[2];
		String tradeName = parsed[3];
		int world = Integer.parseInt(parsed[4]);
		int itemID = Integer.parseInt(parsed[5]);
		int itemAmount = Integer.parseInt(parsed[6]);

		Area actionArea = null;
//		if(parsed.length >= 8) {//Is there a player position attached to this message?
//			try {
//				String parsedPosition = parsed[7];
//				Position playerPos = WebBank.parseCoordinate(parsedPosition);
//				if (playerPos != null) {
//					Log.fine("Player Pos: " + playerPos);
//					actionArea = Area.surrounding(playerPos, 7);
//				}
//			}catch (Exception ex){
//				ex.printStackTrace();
//			}
//		}else{Log.fine("No Player Pos Recieved");}
		if (actionArea == null)
			actionArea = BuyItemHandler.getGEArea();

		NexTask newTask = null;
		switch(muleType.toLowerCase()) {
	
		//Reversed order =)
		case "mule_withdraw":
			Log.fine("lets deposit to player");
			newTask = new DepositToPlayerTask(world, itemID, itemAmount, tradeName, actionArea);
			currentTime = System.currentTimeMillis();
			newTask.setBreakAfterTime(2);
			break;
		case "mule_deposit":
			Log.fine("lets withdraw from player");
			newTask = new WithdrawFromPlayerTask(world, itemID, itemAmount, tradeName, actionArea);
			currentTime = System.currentTimeMillis();
			Log.fine("TIME STARTED MILLI: " + currentTime);
			newTask.setBreakAfterTime(2);
			break;
		}

		TaskHandler.addTask(newTask);
	}
}
