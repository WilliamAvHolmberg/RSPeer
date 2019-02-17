package com.nex.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;
import java.util.function.BooleanSupplier;

import org.rspeer.runetek.api.component.tab.EquipmentSlot;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.ui.Log;

import com.nex.communication.message.NexMessage;
import com.nex.handler.gear.Gear;
import com.nex.script.Nex;
import com.nex.script.WebBank;
import com.nex.script.handler.TaskHandler;
import com.nex.script.items.RSItem;
import com.nex.task.SkillTask;
import com.nex.task.woodcutting.WoodcuttingTask;

public class WoodcuttingRespond extends TaskRespond {

	public WoodcuttingRespond(String respond) {
		super(respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		if(respond != null) {
		}else {
		}
		String[] parsed = respond.split(":");
		String currentTaskID = parsed[3];
		String parsedBankArea = parsed[4];
		String parsedActionArea = parsed[5];
		String parsedAxeID = parsed[6];
		String axeName = parsed[7];
		String treeName = parsed[8];
		String parsedBreakCondition = parsed[9];
		String breakAfter = parsed[10];
		String parsedLevelGoal = parsed[11];
		ArrayList<String> listOfParsedGear = new ArrayList<String>();
		String parsedHelm = parsed[12];
		String parsedCape = parsed[13];
		String parsedAmulet = parsed[14];
		String parsedWeapon = parsed[15];
		String parsedChest = parsed[16];
		String parsedShield = parsed[17];
		String parsedLegs = parsed[18];
		String parsedGloves = parsed[19];
		String parsedBoots = parsed[20];
		String parsedRing = parsed[21];
		String parsedAmmo = parsed[22];
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
		Gear gear = getGear(parsed, listOfParsedGear);
		Area actionArea = WebBank.parseCoordinates(parsedActionArea);
		Area bankArea = null;
		if (!parsedBankArea.equals("none")) {
			bankArea = WebBank.parseCoordinates(parsedBankArea);
		}
		
		int axeID = Integer.parseInt(parsedAxeID);
		RSItem axe = new RSItem(axeName, axeID);
		SkillTask newTask = new WoodcuttingTask(actionArea, bankArea, treeName, axe);
		newTask.setGear(gear);
		newTask.setTaskID(currentTaskID);
		setBreakConditions(newTask, parsedBreakCondition, breakAfter, parsedLevelGoal);
		TaskHandler.addPrioritizedTask(newTask);
	}
	
 
}
