package com.nex.task.combat;

import java.awt.Graphics;

import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.ui.Log;

import com.nex.handler.gear.Gear;
import com.nex.handler.gear.GearHandler;
import com.nex.script.banking.BankHandler;
import com.nex.script.banking.DepositAll;
import com.nex.script.banking.DepositAllOfItem;
import com.nex.script.banking.WithdrawItemEvent;
import com.nex.script.inventory.InventoryItem;
import com.nex.script.inventory.NexInventory;
import com.nex.script.items.RSItem;
import com.nex.script.items.RequiredItem;
import com.nex.script.walking.WalkTo;
import com.nex.task.IMoneyTask;
import com.nex.task.SkillTask;
import com.nex.task.combat.actions.AttackAction;
import com.nex.task.combat.actions.AttackStyleAction;
import com.nex.task.combat.actions.EatAction;
import com.nex.task.combat.actions.LootAction;
import com.nex.task.helper.AreaHelper;
import com.nex.task.helper.BankHelper;
import com.nex.task.tracker.LootTracker;

public class CombatTask extends SkillTask implements IMoneyTask {

	private String monsterName;
	private RSItem food;
	private int lootThreshold;
	private LootAction lootAction;
	private LootTracker lootTracker;
	

	public CombatTask(Area actionArea, Area bankArea, String monsterName, Gear gear, RSItem food, NexInventory inv,
			Skill skill, int lootThreshold) {
		setActionArea(actionArea);
		setBankArea(bankArea);
		setGear(gear);
		setRequiredInventory(inv);
		this.monsterName = monsterName;
		this.food = food;
		this.skill = skill;
		this.lootThreshold = lootThreshold;
		this.startExperience = Skills.getExperience(skill);
		this.lootTracker = new LootTracker(System.currentTimeMillis());
		this.lootAction = new LootAction(actionArea, lootThreshold, lootTracker);
	}

	@Override
	public int loop() {
		if(Movement.getRunEnergy() > 20 && !Movement.isRunEnabled()) {
			Movement.toggleRun(true);
		}else if (!AttackStyleAction.isCorrect(skill)) {
			AttackStyleAction.execute(skill);
		} else if (getGear().getItemToEquip() != null) {
			GearHandler.addItem(getGear().getItemToEquip());
		} else if (shallDepositItem()) {
			BankHandler.addBankEvent(new DepositAllOfItem(getRequiredInventory().getItemToDeposit().getId()));
		} else if (shallWithdrawItem()) {
			BankHandler.addBankEvent(new WithdrawItemEvent(getRequiredInventory().getItemToWithdraw()));
		} else if (EatAction.shallEat()) {
			if (Inventory.contains(food.getId())) {
				int hp = Players.getLocal().getHealthPercent();
				Inventory.getFirst(food.getId()).interact("Eat");
				Time.sleepUntil(()-> Players.getLocal().getHealthPercent() > hp, 3000);
			} else {
				WalkTo.execute(getBankArea());
			}
		} else if (!AreaHelper.inArea(actionArea) && !Players.getLocal().isAnimating() && !Players.getLocal().isMoving()) {
			WalkTo.execute(actionArea);
		} else if(lootAction.shouldLoot() && !AttackAction.playerIsAttacking() ){
			if(Inventory.isFull() && Inventory.contains(food.getId())) {
				Inventory.getFirst(food.getId()).interact("Eat");
				Time.sleepUntil(()-> !Inventory.isFull(), 3000);
			}else {
				lootAction.execute();	
			}
		}else {
			AttackAction.execute(monsterName, actionArea);
		}
		// loot
		// food support
		// player has gear
		// player has inv
		Log.fine("in combatloop");
		return 0;
	}

	private boolean shallDepositItem() {
		return BankHelper.isAvailable() && getRequiredInventory().getItemToDeposit() != null || (Inventory.isFull() && !Inventory.contains(food.getId()));
	}

	private boolean shallWithdrawItem() {
		return BankHelper.isAvailable() && getRequiredInventory().getItemToWithdraw() != null;
	}

	@Override
	public void notify(RenderEvent event) {
		Graphics g = event.getSource();
		g.drawString("Current Task: " + getSkill() + "->" + getWantedLevel(), 300, 300);
		g.drawString("Current Enemy: " + monsterName, 300, 325);
		g.drawString("Loot threshold: " + lootThreshold, 300, 350);
		g.drawString("Money Gained: " + lootTracker.getGained(), 300, 375);
		g.drawString("Money Per Hour: " + lootTracker.getPerHour(), 300, 400);

	}

	@Override
	public void notify(ChatMessageEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notify(ObjectSpawnEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFinished() {
		return getWantedLevel() <= Skills.getCurrentLevel(getSkill());
	}

	@Override
	public Skill getSkill() {
		return skill;
	}

	@Override
	public void removeTask() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public String getLog() {
		return getLog(getTaskID(), getExperiencePerHour(), getMoneyPerHour());
	}

	@Override
	public int getMoneyPerHour() {
		return lootTracker.getPerHour();
	}



}
