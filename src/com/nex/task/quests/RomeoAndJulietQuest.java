package com.nex.task.quests;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Camera;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.ui.Log;

import com.nex.script.Quest;
import com.nex.script.handler.TaskHandler;
import com.nex.script.inventory.InventoryItem;
import com.nex.script.inventory.NexInventory;
import com.nex.script.items.RSItem;
import com.nex.script.walking.WalkEvent;
import com.nex.script.walking.WalkWithObstacle;
import com.nex.task.QuestTask;

public class RomeoAndJulietQuest extends QuestTask {

	private final Position julietPosition = new Position(3158,3425,1);
	private final Position basementStairs = new Position(3156,3435);
	private final Position upperStairs = new Position(3152,3436,1);
	private final Position romeoPosition = new Position(3213,3423,0);

	private final WalkEvent walkToJulietEvent = new WalkWithObstacle(basementStairs, julietPosition, "Staircase");
	private final WalkEvent walkToRomeoEvent = new WalkWithObstacle(upperStairs, romeoPosition, "Staircase");



	private InventoryItem cadavaBerries = new InventoryItem(1, new RSItem("Cadava berries", 753), 1);
	private InventoryItem cadavaPotion = new InventoryItem(1, new RSItem("Cadava potion", 756), 1);

	



	@Override
	public int loop() {
		Log.fine("ROMEO");
		switch (getCurrentSection()) {
		case 0:
			
			walkAndTalkTo(walkToJulietEvent, "Juliet", new NexInventory().addItem(cadavaBerries),
					"I guess I could look for him for you.");
			break;
		case 20:
			walkAndTalkTo(walkToRomeoEvent, "Romeo", "I guess I could look for him for you.");
			break;
		case 30:
			if(inCutScene()) {
				Time.sleepUntil(() -> !inCutScene(), 50000);
			}else{
				walkAndTalkTo(new Position(3255, 3481, 0), "Father Lawrence");
			}
			break;
		case 40:
			walkAndTalkTo(new Position(3195, 3404, 0), "Apothecary", "Talk about something else.", "Talk about Romeo & Juliet.", "Ok, thanks.");
			break;
		case 50:
			if (!Inventory.contains("Cadava potion")) {
				walkAndTalkTo(new Position(3195, 3404, 0), "Apothecary", new NexInventory().addItem(cadavaBerries), "Talk about something else.", "Talk about Romeo & Juliet.","Ok, thanks.");
				Time.sleep(1000);
			}else {
				walkAndTalkTo(walkToJulietEvent, "Juliet", new NexInventory().addItem(cadavaPotion));
				return 2500;
			}
			break;
		case 60:
			if(inCutScene()) { //cutscene
				if(pendingContinue()) {
					selectContinue();
				}
			}else {
			walkAndTalkTo(walkToRomeoEvent, "Romeo");
			}
		}

		return 1000;
	}

	public int getQuestConfig() {
		return 144;
	}

	@Override
	public String getLog() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void removeTask() {
		// TODO queue tutorial island is done. Add RunTime
		
	}



	public Quest getQuest() {
		return Quest.ROMEO_JULIET;
	}

	public static Quest getThisQuest() {
		return Quest.ROMEO_JULIET;
	}

	@Override
	public void notify(RenderEvent event) {
		Graphics g = event.getSource();
		g.drawString("STATE:" + getCurrentSection(), 50,50);
		
	}

	@Override
	public void notify(ChatMessageEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notify(ObjectSpawnEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	


}
