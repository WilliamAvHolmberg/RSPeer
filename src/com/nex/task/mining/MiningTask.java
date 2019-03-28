package com.nex.task.mining;

import java.awt.Graphics;
import java.util.Arrays;
import java.util.List;

import org.rspeer.runetek.adapter.scene.SceneObject;
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
import com.nex.task.SkillTask;
import com.nex.task.mining.actions.MineOreAction;
import com.nex.task.woodcutting.actions.CutTreeAction;

public class MiningTask extends SkillTask implements ChatMessageListener {
	List<Integer> rocks;
	RSItem pickaxe;
	int oreID;
	int oresMined;
	int orePrice;

	public MiningTask(Area actionArea, Area bankArea, Integer[] rocks,RSItem axe) {
		setActionArea(actionArea);
		setBankArea(bankArea);
		this.rocks = Arrays.asList(rocks);
		setPickAxe(axe);	
		addRequiredItem(axe);
		this.orePrice = Exchange.getPrice(getLogID());
		this.startExperience = Skills.getExperience(getSkill());
	}

	public MiningTask(Area actionArea, Area bankArea, List<Integer> rocks, RSItem axe) {
		setActionArea(actionArea);
		setBankArea(bankArea);
		this.rocks = rocks;
		setPickAxe(axe);	
		addRequiredItem(axe);
		this.orePrice = Exchange.getPrice(getLogID());
	}

	// TODO - More generic. Do not check if player is in area all the time.
	@Override
	public int loop() {
		GearItem itemToEquip = requiredGear.getItemToEquip();
		if(itemToEquip != null) {
			GearHandler.addItem(itemToEquip);
		}else if (playerNeedAxe()) {
			BankHandler.addBankEvent(new WithdrawItemEvent(new WithdrawItem(pickaxe, 1,1)).setBankArea(bankArea));
		} else if (Inventory.isFull()) {
			BankHandler.addBankEvent(new DepositAllExcept(pickaxe.getName()).setBankArea(bankArea));
		} else {
			MineOreAction.execute(actionArea, rocks);
		}
		return 600;
	}

	private boolean playerNeedAxe() {
		return !Inventory.contains(getPickAxe().getName()) && !Equipment.contains(getPickAxe().getName());
	}



	

	private void setPickAxe(RSItem axe) {
		this.pickaxe = axe;
	}

	private RSItem getPickAxe() {
		return this.pickaxe;
	}
	
	private void setoreID(int logID) {
		this.oreID= logID;
	}
	
	public int getLogID() {
		//TODO
		//switch(oreName) {
		//case "Tree":
		//	return 1511;
		//}
		return 438;
	}
	@Override
	public Skill getSkill() {
		return Skill.MINING;
	}

	@Override
	public boolean isFinished() {
		return Skills.getCurrentLevel(getSkill()) >= getWantedLevel();
	}
	@Override
	public void notify(ChatMessageEvent event) {
		//TODO
		if(event.getType() == ChatMessageType.FILTERED &&
				event.getMessage().contains("You manage to")) {
			oresMined ++;
			
		}
		
	}

	@Override
	public void notify(ObjectSpawnEvent spawnEvent) {
		//CutTreeAction.get().notify(spawnEvent);
	}
	
	@Override
	public void notify(RenderEvent event) {
		Graphics g = event.getSource();
		g.drawString("Current Task: " + getSkill() + "->" + getWantedLevel(), 300, 300);
		g.drawString("Ran for: " + getTimeRanString(), 300, 375);
		g.drawString("Ores per hour: " + getPerHour(oresMined), 300, 400);
		g.drawString("Money per hour: " + getPerHour(oresMined) * orePrice, 300, 425);
	}

	@Override
	public void removeTask() {
		//TASK LOG TODO
		
	}



}
