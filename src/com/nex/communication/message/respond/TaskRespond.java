package com.nex.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;
import java.util.function.BooleanSupplier;


import org.rspeer.runetek.api.component.tab.EquipmentSlot;
import org.rspeer.ui.Log;

import com.nex.communication.message.NexMessage;
import com.nex.handler.gear.Gear;
import com.nex.script.inventory.InventoryItem;
import com.nex.script.inventory.NexInventory;
import com.nex.script.items.RSItem;
import com.nex.task.SkillTask;

public abstract class TaskRespond extends NexMessage {

	protected long currentTime;
	
	public TaskRespond(String respond) {
		super( respond);
	}
	/*
	public NexInventory getInventory(String parsedInventory) {
		NexInventory inv = new NexInventory();
		for(String parsedInvItem : parsedInventory.split(";")) {
			if(parsedInvItem.length() > 2) {
				String[] moreParsed = parsedInvItem.split(",");
				String itemName = moreParsed[0];
				int itemId = Integer.parseInt(moreParsed[1]);
				int itemAmount = Integer.parseInt(moreParsed[2]);
				int buyAmount = Integer.parseInt(moreParsed[3]);
				methodProvider.log("Buy amount for item: " + itemName + ":::" + buyAmount);
				InventoryItem newItem = new InventoryItem(itemAmount, new RSItem(itemName, itemId), buyAmount);
				inv.addItem(newItem);
			}
		}
		return inv;
	}*/

	public void setBreakConditions(SkillTask newTask, String parsedBreakCondition, String breakAfter,
			String parsedlevelGoal) {
		if(parsedBreakCondition.toLowerCase().contains("time_or_level")) {
			newTask.setBreakAfterTime(5 + (int)Double.parseDouble(breakAfter));
			newTask.setWantedLevel((int)Double.parseDouble(parsedlevelGoal));
		}else if(parsedBreakCondition.toLowerCase().contains("time")) {
			newTask.setBreakAfterTime(5 + (int)Double.parseDouble(breakAfter));
		}else if(parsedBreakCondition.toLowerCase().contains("level")) {
			newTask.setWantedLevel((int)Double.parseDouble(parsedlevelGoal));
		}
		
	}
	
	public NexInventory getInventory(String parsedInventory) {
		NexInventory inv = new NexInventory();
		for(String parsedInvItem : parsedInventory.split(";")) {
			if(parsedInvItem.length() > 2) {
				String[] moreParsed = parsedInvItem.split(",");
				String itemName = moreParsed[0];
				int itemId = Integer.parseInt(moreParsed[1]);
				int itemAmount = Integer.parseInt(moreParsed[2]);
				int buyAmount = Integer.parseInt(moreParsed[3]);
				InventoryItem newItem = new InventoryItem(itemAmount, new RSItem(itemName, itemId), buyAmount);
				inv.addItem(newItem);
			}
		}
		return inv;
	}
	
	protected Gear getGear(String[] parsed, ArrayList<String> listOfParsedGear) {
		Gear gear = new Gear();
		String unParsedItem;
		String itemName;
		int itemID;
		for(int i = 0; i<EquipmentSlot.values().length; i++) {
			unParsedItem = listOfParsedGear.get(i);
			if(!unParsedItem.toLowerCase().equals("none")) {
				itemName = unParsedItem.split(",")[0];
				itemID = Integer.parseInt(unParsedItem.split(",")[1]);
				Log.fine("slot:" + EquipmentSlot.values()[i] + "   itemName:" + itemName + "    itemID:" + itemID);
				gear.addGear(EquipmentSlot.values()[i], new RSItem(itemName, itemID));
			}
		}
		return gear;		
	}
}
