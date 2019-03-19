package com.nex.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;

import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.ui.Log;

import com.nex.handler.gear.Gear;
import com.nex.script.WebBank;
import com.nex.script.handler.TaskHandler;
import com.nex.script.inventory.NexInventory;
import com.nex.script.items.RSItem;
import com.nex.task.CombatTask;
import com.nex.task.NexTask;

public class CombatRespond extends TaskRespond {

	public CombatRespond(String respond) {
		super(respond);

	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		if (respond != null) {
			String[] parsed = respond.split(":");
			String currentTaskID = parsed[3];
			String parsedBankArea = parsed[4];
			String parsedActionArea = parsed[5];
			String monsterName = parsed[6];
			String parsedBreakCondition = parsed[7];
			// parsed 7 is our break condition. Will always be set to time for now
			String breakAfter = parsed[8];

			ArrayList<String> listOfParsedGear = new ArrayList<String>();
			String parsedHelm = parsed[9];
			String parsedCape = parsed[10];
			String parsedAmulet = parsed[11];
			String parsedWeapon = parsed[12];
			String parsedChest = parsed[13];
			String parsedShield = parsed[14];
			String parsedLegs = parsed[15];
			String parsedGloves = parsed[16];
			String parsedBoots = parsed[17];
			String parsedRing = parsed[18];
			String parsedAmmo = parsed[19];
			listOfParsedGear.add(parsedHelm);
			listOfParsedGear.add(parsedCape);
			listOfParsedGear.add(parsedAmulet);
			listOfParsedGear.add(parsedWeapon);
			listOfParsedGear.add(parsedChest);
			listOfParsedGear.add(parsedShield);
			listOfParsedGear.add(parsedLegs);
			listOfParsedGear.add(parsedGloves);
			listOfParsedGear.add(parsedBoots);
			listOfParsedGear.add(parsedRing);
			listOfParsedGear.add(parsedAmmo);

			String parsedFood = parsed[21];
			String parsedInventory = parsed[22];
			String parsedLootThreshold = parsed[23];
			String parsedSkill = parsed[24];
			String parsedlevelGoal = parsed[25];
			String shouldMule = parsed[26];
			int lootThreshold = Integer.parseInt(parsedLootThreshold);
			RSItem food = null;
			if (!parsedFood.toLowerCase().equals("none")) {
				food = new RSItem(parsedFood.split(",")[0], Integer.parseInt(parsedFood.split(",")[1]));
			}

			Gear gear = getGear(parsed, listOfParsedGear);

			Area bankArea = null;
			if (!parsedBankArea.equals("none")) {
				bankArea = WebBank.parseCoordinates(parsedBankArea);
			}
			Log.fine("after bank");
			Log.fine(parsedInventory);
			currentTime = System.currentTimeMillis();
			Area actionArea = WebBank.parseCoordinates(parsedActionArea);
			NexInventory inv = new NexInventory();
			inv = getInventory(parsedInventory);
			CombatTask newTask;
			Log.fine("after inv");
			newTask = new CombatTask(actionArea, bankArea, monsterName, gear, food, inv,
					Skill.valueOf(parsedSkill.toUpperCase()), lootThreshold);
			newTask.setTaskID(currentTaskID);
			setBreakConditions(newTask, parsedBreakCondition, breakAfter, parsedlevelGoal);
			TaskHandler.addPrioritizedTask(newTask);
		}
		
		
	}

	

}
