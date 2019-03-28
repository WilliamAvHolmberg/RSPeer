package com.nex.task.woodcutting;

import java.awt.Graphics;
import java.util.List;

import com.nex.task.helper.InteractionHelper;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.event.listeners.ChatMessageListener;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ChatMessageType;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.ui.Log;

import com.nex.handler.gear.GearHandler;
import com.nex.handler.gear.GearItem;
import com.nex.script.Exchange;
import com.nex.script.Nex;
import com.nex.script.banking.BankHandler;
import com.nex.script.banking.DepositAll;
import com.nex.script.banking.DepositAllExcept;
import com.nex.script.banking.WithdrawItemEvent;
import com.nex.script.items.RSItem;
import com.nex.script.items.RequiredItem;
import com.nex.script.items.WithdrawItem;
import com.nex.task.IMoneyTask;
import com.nex.task.SkillTask;
import com.nex.task.woodcutting.actions.CutTreeAction;

public class WoodcuttingTask extends SkillTask implements ChatMessageListener, IMoneyTask {
	String treeName;
	RSItem axe;
	int logID;
	int logsChopped;
	int logPrice;
	CutTreeAction cutTreeAction = new CutTreeAction();

	public WoodcuttingTask(Area actionArea, Area bankArea, String treeName, RSItem axe) {
		setActionArea(actionArea);
		setBankArea(bankArea);
		setTreeName(treeName);
		setAxe(axe);	
		addRequiredItem(axe);
		this.logPrice = Exchange.getPrice(getLogID());
		this.startExperience = Skills.getExperience(getSkill());
	}

	int needToChangeWorld = 0;
	long lastCountedPlayers = 0;

	// TODO - More generic. Do not check if player is in area all the time.
	@Override
	public int loop() {
		GearItem itemToEquip = requiredGear.getItemToEquip();
		if(itemToEquip != null) {
			GearHandler.addItem(itemToEquip);
		} else if (playerNeedAxe()) {
			BankHandler.addBankEvent(new WithdrawItemEvent(new WithdrawItem(axe, 1,1)).setBankArea(bankArea));
		}
		else if (Inventory.isFull()) {
			BankHandler.addBankEvent(new DepositAllExcept(axe.getName()).setBankArea(bankArea));
		} else if(checkChangeWorld()) {
		}
		else {
			return cutTreeAction.cutTree(actionArea, treeName);
		}
		return 600;
	}

	static int jumpThreadhold = 2;
	boolean checkChangeWorld(){
		if(needToChangeWorld >= jumpThreadhold){
			if(InteractionHelper.HopRandomWorld())
				needToChangeWorld = 0;
			return true;
		}
		if(System.currentTimeMillis() - lastCountedPlayers < 10000)
			return false;
		lastCountedPlayers = System.currentTimeMillis();
		Player localPlayer = Players.getLocal();
		if(localPlayer.isMoving() || !localPlayer.isAnimating()) return false;
		SceneObject obj = InteractionHelper.getSceneObjectLookingAt(localPlayer, false);
		if (obj != null && obj.getName() == treeName){
			if (InteractionHelper.countPlayersOnMySceneObject(false) > 1)
				needToChangeWorld++;
			else
				needToChangeWorld--;
		}
		if(needToChangeWorld >= jumpThreadhold)
			return checkChangeWorld();
		return false;
	}

	private boolean playerNeedAxe() {
		return !Inventory.contains(getAxe().getName()) && !Equipment.contains(getAxe().getName());
	}


	private WoodcuttingTask setTreeName(String treeName) {
		this.treeName = treeName;
		return this;
	}

	

	private void setAxe(RSItem axe) {
		this.axe = axe;
	}

	private RSItem getAxe() {
		return this.axe;
	}
	
	private void setLogID(int logID) {
		this.logID= logID;
	}
	
	public int getLogID() {
		switch(treeName) {
		case "Tree":
			return 1511;
		case "Yew":
			return 1515;
		}
		return 1511;
	}
	@Override
	public Skill getSkill() {
		return Skill.WOODCUTTING;
	}

	@Override
	public boolean isFinished() {
		return Skills.getCurrentLevel(getSkill()) >= getWantedLevel();
	}
	@Override
	public void notify(ChatMessageEvent event) {
		if(event.getType() == ChatMessageType.FILTERED &&
				event.getMessage().contains("You get")) {
			Log.fine("We get log");
			logsChopped ++;
		}
	}

	@Override
	public void notify(ObjectSpawnEvent spawnEvent) {
		cutTreeAction.notify(spawnEvent);
	}
	
	@Override
	public void notify(RenderEvent event) {
		Graphics g = event.getSource();
		g.drawString("Current Task: " + getSkill() + "->" + getWantedLevel(), 300, 300);
		g.drawString("Log price: " + logPrice, 300, 325);
		g.drawString("Logs chopped: " + logsChopped, 300, 350);
		g.drawString("Ran for: " + getTimeRanString(), 300, 375);
		g.drawString("Logs per hour: " + getPerHour(logsChopped), 300, 400);
		g.drawString("Money per hour: " + getMoneyPerHour(), 300, 425);

	}
	
	@Override
	public String getLog() {
		return getLog(getTaskID(), getExperiencePerHour(), getMoneyPerHour());
	}

	@Override
	public void removeTask() {
		//TASK LOG TODO
		
	}

	@Override
	public int getMoneyPerHour() {
		return (int) (getPerHour(logsChopped) * logPrice);
	}



}
