package com.nex.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


import com.nex.communication.message.request.RequestAccountInfo;
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
import com.nex.utils.json.JsonObject;

public class MuleRespond extends TaskRespond {

	public MuleRespond(String respond) {
		super(respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		JsonObject jsonRespond = JsonObject.readFrom(respond);
		String muleType = jsonRespond.get("task_type").asString();
		String tradeName = jsonRespond.get("slave_name").asString();
		int world = Integer.parseInt(jsonRespond.get("world").asString());
		int itemID = Integer.parseInt(jsonRespond.get("item_id").asString());
		int itemAmount = Integer.parseInt(jsonRespond.get("item_amount").asString());

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
