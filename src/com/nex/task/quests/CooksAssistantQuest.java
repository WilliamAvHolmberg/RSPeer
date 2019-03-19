package com.nex.task.quests;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import com.nex.task.SkillTask;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;

import com.nex.script.Quest;
import com.nex.script.banking.BankHandler;
import com.nex.script.banking.WithdrawItemEvent;
import com.nex.script.inventory.InventoryItem;
import com.nex.script.inventory.NexInventory;
import com.nex.script.items.RSItem;
import com.nex.task.QuestTask;



public class CooksAssistantQuest extends QuestTask {

	private final Area cookArea = Area.rectangular(3205, 3212, 3212, 3217);
	private final InventoryItem egg = new InventoryItem(1, new RSItem("Egg", 1944), 1);
	private final InventoryItem potOfFlour = new InventoryItem(1, new RSItem("Pot of Flour", 1933), 1);
	private final InventoryItem bucketOfMilk = new InventoryItem(1, new RSItem("Bucket of Milk", 1927), 1);
	public NexInventory requiredInventory = new NexInventory()
			.setItems(new ArrayList<InventoryItem>(Arrays.asList(egg, potOfFlour, bucketOfMilk)));

	
	public int loop() {
		
		if (getCurrentSection() == 0 && getItemToWithdraw(requiredInventory) != null) {
			BankHandler.addBankEvent(new WithdrawItemEvent(getItemToWithdraw(requiredInventory)));
		} else if (!inArea(cookArea)) {
			walkTo(cookArea);
		} else if (pendingOption()) {
			selectOption("What's wrong?", "I'm always happy to help a cook in distress");
		} else if (pendingContinue()) {
			selectContinue();
		} else if(!Dialog.isOpen()) {
			talkToNpc("Cook");
		}


		return 200;
	}

	@Override
	public String getLog() {
		return SkillTask.getLog(getTaskID(), 0, 0);
	}

	@Override
	public void removeTask() {
		// TODO queue tutorial island is done. Add RunTime
		//TaskHandler.getCurrentTask() = null;

	}

	
	
	public int getQuestConfig() {
		return 29;
	}


	public Quest getQuest() {
		return Quest.COOKS_ASSISTANT;
	}

	public static Quest getThisQuest() {
		return Quest.COOKS_ASSISTANT;
	}

	@Override
	public void notify(RenderEvent arg0) {
		// TODO Auto-generated method stub
		
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
