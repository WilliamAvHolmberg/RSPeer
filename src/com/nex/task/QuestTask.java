package com.nex.task;
import java.awt.event.KeyEvent;
import java.util.Arrays;


import com.nex.script.grandexchange.BuyItemHandler;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.input.Mouse;
import com.nex.script.walking.WalkTo;

import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.path.Path;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.event.listeners.ChatMessageListener;
import org.rspeer.runetek.event.listeners.ObjectSpawnListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.ui.Log;

import com.nex.script.Quest;
import com.nex.script.banking.BankHandler;
import com.nex.script.banking.WithdrawItemEvent;
import com.nex.script.inventory.InventoryItem;
import com.nex.script.inventory.NexInventory;
import com.nex.script.items.WithdrawItem;
import com.nex.script.walking.WalkEvent;
import com.nex.script.walking.WalkTo;
import com.nex.task.action.QuestAction;

public abstract class QuestTask extends NexTask {
	
	
	public abstract Quest getQuest();
	public abstract int getQuestConfig();
	
	protected void removeItem(InventoryItem item) {
		if(requiredInventory.getItems().contains(item)) {
			requiredInventory.getItems().remove(item);
			
		}	
	}
	
	protected Position myPosition() {
		return Players.getLocal().getPosition();
	}
	
	protected boolean walkTo(Area area) {
		 
	
		return Movement.buildPath(area.getCenter()) != null && WalkTo.execute(area.getCenter());
	}
	
	protected boolean walkTo(Position position) {
		return WalkTo.execute(position);
	}
	
	
	

	protected boolean talkToNpc(String name) {
		if (getNpc(name) != null && getNpc(name).interact("Talk-to")) {
			Time.sleepUntil(this::pendingContinue, 500, 5000);
			return true;
		}
		return false;
	}
	
	public void walkAndTalkTo(Position position,String name) {
		if(pendingContinue()) {
			selectContinue();
		}else if(getNpc(name) == null) {
			walkTo(position);
		}else if(!Dialog.isOpen()) {
			talkToNpc(name);
		}
	}
	
	public void walkAndTalkTo(Position position,String name, String... options) {
		if(pendingContinue()) {
			selectContinue();
		}else if(getNpc(name) == null) {
			walkTo(position);
		}else if(pendingOption()) {
			selectOption(options);
		}else if(!Dialog.isOpen()) {
			talkToNpc(name);
		}
		
	}
	
	public void walkAndTalkTo(WalkEvent walkEvent,String name, String... options) {
		if(pendingContinue()) {
			selectContinue();
		}else if(getNpc(name) == null) {
			walkEvent.execute();
		}else if(pendingOption()) {
			selectOption(options);
		}else if(!Dialog.isOpen()) {
			Log.fine("lets talk?");
			talkToNpc(name);
		}
		
	}
	
	public void walkAndTalkTo(WalkEvent walkEvent,String name, NexInventory requiredInventory, String... options) {
		if(getItemToWithdraw(requiredInventory) != null) {
			BankHandler.addBankEvent(new WithdrawItemEvent(getItemToWithdraw(requiredInventory)));
		}else if(getNpc(name) == null) {
			Log.fine("lets walk to " + name);
			walkEvent.execute();
		}else if(pendingContinue()) {
			selectContinue();
		}else if(pendingOption()) {
			selectOption(options);
		}else if(!Dialog.isOpen()) {
			talkToNpc(name);
		}
		
	}
	public void walkAndTalkTo(Position position,String name, NexInventory requiredInventory, String... options) {
		if(getItemToWithdraw(requiredInventory) != null) {
			BankHandler.addBankEvent(new WithdrawItemEvent(getItemToWithdraw(requiredInventory)).setBankArea(BuyItemHandler.getGEArea()));
		}else if(getNpc(name) == null) {
			Log.fine("lets walk to ");
			walkTo(position);
		}else if(pendingContinue()) {
			selectContinue();
		}else if(pendingOption()) {
			selectOption(options);
		}else if(!Dialog.isOpen()) {
			talkToNpc(name);
		}
		
	}
	
	protected boolean createItem(InventoryItem finalItem, InventoryItem subItem1, InventoryItem subItem2) {
		NexInventory reqInv = new NexInventory().addItem(subItem1).addItem(subItem2);
		if(Inventory.contains(finalItem.getItem().getName())) {
			return true;
		}
		if(getItemToWithdraw(reqInv) != null) {
			BankHandler.addBankEvent(new WithdrawItemEvent(getItemToWithdraw(reqInv)));
			return false;
		}
		if(Inventory.contains(subItem1.getItem().getName()) && Inventory.contains(subItem2.getItem().getName())) {
			if(Inventory.isItemSelected()) {
				QuestAction.interactInventory("Use", subItem1.getItem().getName());
			}else {
				QuestAction.interactInventory("Use", subItem2.getItem().getName());
			}
		}
		Time.sleep(1000);
		return false;
	}
	
	protected boolean selectOption(String... options) {
		return 	Dialog.process(options);
	}

	protected Npc getNpc(String name) {
		Npc npc = Npcs.getNearest(p -> p.getName().contains(name) && p.isPositionInteractable());
		return npc;
	}

	protected boolean pendingContinue() {
		return getContinueWidget() != null || Dialog.canContinue();
	}
	

	
	protected boolean pendingOption() {
		return Dialog.isViewingChatOptions();
	}
	
	protected boolean inArea(Area area) {
		return area.contains(Players.getLocal());
	}
	


	protected boolean selectContinue() {

		InterfaceComponent cantReach = Interfaces.getFirst(p -> p.getText().contains("reach that") || p.getText().contains("fighting that"));
		if (cantReach != null && cantReach.isVisible()) {
			Log.fine("Fire event 299");
			Game.getClient().fireScriptEvent(299, 1, 1);
		}

		boolean wasSuccessful = false;
		for(int i = 0; i < 10 && (Dialog.canContinue() || Dialog.isProcessing()); i++) {
			Time.sleepWhile(Dialog::isProcessing, 100, 1000);
			if(Dialog.canContinue()) {
				Dialog.processContinue();
				continue;
			}
			break;
		}
		return wasSuccessful;
	}


	
	protected InventoryItem getItemToWithdraw(NexInventory inventory) {
		for (InventoryItem item : inventory.getItems()) {
			if (Inventory.getCount(item.getItem().getId()) < item.getAmount()) {
				return item;
			}
		}
		return null;
	}
	
	protected InterfaceComponent getContinueWidget() {
		InterfaceComponent con = Interfaces.getFirst(p -> p.isVisible() && (p.getText().contains("Click here to con")) || p.getText().contains("Click to con"));
		
		if(con != null && con.isVisible()) {
			return con;
		}
		return null;

	}


	protected int getCurrentSection() {
		return getVarbit(getQuestConfig());
	}
	public boolean isFinished() {
		return getQuest().isCompleted();
	}

	public int getVarbit(int i) {
		return Varps.get(i);
	}
	
	public boolean inCutScene() {
		return Game.isInCutscene();
	}
	  
	    
}
