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
import com.nex.utils.json.JsonObject;
import com.nex.utils.json.JsonValue;

public class WoodcuttingRespond extends TaskRespond {

	public WoodcuttingRespond(String respond) {
		super(respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
			JsonObject jsonRespond = JsonObject.readFrom(respond);
			JsonObject jsonAxe = jsonRespond.get("axe").asObject();
			JsonObject parsedBreakCondition = jsonRespond.get("break_condition").asObject();
			JsonObject jsonGear = jsonRespond.get("gear").asObject();
			
			
			String currentTaskID = jsonRespond.get("task_id").toString();
			String parsedBankArea = jsonRespond.get("bank_area").asString();
			String parsedActionArea = jsonRespond.get("action_area").asString();
			String treeName = jsonRespond.get("tree_name").asString();
			
			Gear gear = getGear(jsonGear);
			RSItem axe = new RSItem(jsonAxe);
			Area actionArea = WebBank.parseCoordinates(parsedActionArea);
			Area bankArea = null;
		
		
			if (!parsedBankArea.equals("none")) {
				bankArea = WebBank.parseCoordinates(parsedBankArea);
			}
			

			SkillTask newTask = new WoodcuttingTask(actionArea, bankArea, treeName, axe);
			newTask.setGear(gear);
			newTask.setTaskID(currentTaskID);
			setBreakConditions(newTask, parsedBreakCondition);
			TaskHandler.addPrioritizedTask(newTask);	
	}

}
