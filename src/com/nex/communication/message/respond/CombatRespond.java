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
import com.nex.task.NexTask;
import com.nex.task.SkillTask;
import com.nex.task.combat.CombatTask;
import com.nex.task.woodcutting.WoodcuttingTask;
import com.nex.utils.json.JsonObject;

public class CombatRespond extends TaskRespond {

	public CombatRespond(String respond) {
		super(respond);

	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		if (respond != null) {
			
			JsonObject jsonRespond = JsonObject.readFrom(respond);
			JsonObject jsonFood = jsonRespond.get("food").asObject();
			JsonObject parsedBreakCondition = jsonRespond.get("break_condition").asObject();
			JsonObject jsonGear = jsonRespond.get("gear").asObject();
			JsonObject jsonInventory = jsonRespond.get("inventory").asObject();
			
			
			
			String currentTaskID = jsonRespond.get("task_id").toString();
			String parsedBankArea = jsonRespond.get("bank_area").asString();
			String parsedActionArea = jsonRespond.get("action_area").asString();
			String monsterName = jsonRespond.get("monster_name").asString();
			String parsedSkill = jsonRespond.get("skill").asString();
			int lootThreshold = jsonRespond.get("loot_threshold").asInt();

			
			Gear gear = getGear(jsonGear);
			NexInventory inv=  getInventory(jsonInventory);
			RSItem food = new RSItem(jsonFood);
			Area actionArea = WebBank.parseCoordinates(parsedActionArea);
			Area bankArea = null;		
		
			if (!parsedBankArea.equals("none")) {
				bankArea = WebBank.parseCoordinates(parsedBankArea);
			}
			

		
			



	
		
			currentTime = System.currentTimeMillis();
			CombatTask newTask;
			Log.fine("after inv");
			newTask = new CombatTask(actionArea, bankArea, monsterName, gear, food, inv,
					Skill.valueOf(parsedSkill.toUpperCase()), lootThreshold);
			newTask.setTaskID(currentTaskID);
			setBreakConditions(newTask, parsedBreakCondition);
			TaskHandler.addPrioritizedTask(newTask);
		}
		
		
	}

	

}
