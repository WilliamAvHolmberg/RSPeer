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
import com.nex.utils.json.JsonObject;
import com.nex.utils.json.JsonValue;

public abstract class TaskRespond extends NexMessage {

	protected long currentTime;
	
	public TaskRespond(String respond) {
		super(respond);
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
	
	public void setBreakConditions(SkillTask newTask, JsonObject jsonBreakCondition) {
		String type = jsonBreakCondition.get("type").asString();
		int breakAfter = jsonBreakCondition.get("task_duration").asInt();
		int wantedLevel = jsonBreakCondition.get("level_goal").asInt();
		if(type.toLowerCase().contains("time_or_level")) {
			newTask.setBreakAfterTime(5 + breakAfter);
			newTask.setWantedLevel(wantedLevel);
		}else if(type.toLowerCase().contains("time")) {
			newTask.setBreakAfterTime(5 + (breakAfter));
		}else if(type.toLowerCase().contains("level")) {
			newTask.setWantedLevel(wantedLevel);
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
	
	protected Gear getGear(JsonObject jsonGear) {
		Gear gear = new Gear();
		JsonValue unParsedItem;
		String itemName;
		int itemID;
		for(int i = 0; i < EquipmentSlot.values().length; i++) {
			String equipmentSlot = EquipmentSlot.values()[i].toString().toLowerCase();
			Log.fine(equipmentSlot);
			unParsedItem = jsonGear.get(equipmentSlot);
			Log.fine(unParsedItem);
			if(unParsedItem.isObject()) {
				JsonObject object = unParsedItem.asObject();
				itemName = object.get("name").asString();
				itemID = object.get("id").asInt();
				Log.fine("slot:" + EquipmentSlot.values()[i] + "   itemName:" + itemName + "    itemID:" + itemID);
				gear.addGear(EquipmentSlot.values()[i], new RSItem(itemName, itemID));
			}
		}
		return gear;	
	}
	
	protected NexInventory getInventory(JsonObject jsonInventory) {
		NexInventory inv = new NexInventory();
		JsonValue unParsedItem;
		int itemID;
		int amount;
		int buyAmount;

			for(int i = 0; i < jsonInventory.names().size(); i++) {
			String itemName = jsonInventory.names().get(i);
			Log.fine(itemName);
			unParsedItem = jsonInventory.get(itemName);
			if(unParsedItem.isObject()) {
				JsonObject object = unParsedItem.asObject();
				amount = object.get("amount").asInt();
				buyAmount = object.get("buy_amount").asInt();
				itemID = object.get("id").asInt();
				Log.fine("slot:" + EquipmentSlot.values()[i] + "   itemName:" + itemName + "    itemID:" + itemID);
				InventoryItem newItem = new InventoryItem(amount, new RSItem(itemName, itemID), buyAmount);
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
		for(int i = 0; i < EquipmentSlot.values().length; i++) {
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
