package com.nex.task;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
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
import com.nex.task.combat.actions.AttackAction;
import com.nex.task.combat.actions.AttackStyleAction;
import com.nex.task.combat.actions.EatAction;
import com.nex.task.helper.AreaHelper;
import com.nex.task.helper.BankHelper;

public class CombatTask extends SkillTask {

	private String monsterName;
	private RSItem food;
	private int lootThreshold;

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
	}

	@Override
	public int loop() {
		if (!AttackStyleAction.isCorrect(skill)) {
			AttackStyleAction.execute(skill);
		} else if (getGear().getItemToEquip() != null) {
			GearHandler.addItem(getGear().getItemToEquip());
		} else if (shallDepositItem()) {
			BankHandler.addBankEvent(new DepositAllOfItem(getRequiredInventory().getItemToDeposit().getId()));
		} else if (shallWithdrawItem()) {
			BankHandler.addBankEvent(new WithdrawItemEvent(getRequiredInventory().getItemToWithdraw()));
		} if (EatAction.shallEat()) {
			if (Inventory.contains(food.getId())) {
				int hp = Players.getLocal().getHealthPercent();
				Inventory.getFirst(food.getId()).interact("Eat");
				Time.sleepUntil(()-> Players.getLocal().getHealthPercent() > hp, 3000);
			} else {
				WalkTo.execute(getBankArea());
			}
		} else if (!AreaHelper.inArea(actionArea)) {
			WalkTo.execute(actionArea);
		} else {
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
		return BankHelper.isAvailable() && getRequiredInventory().getItemToDeposit() != null;
	}

	private boolean shallWithdrawItem() {
		return BankHelper.isAvailable() && getRequiredInventory().getItemToWithdraw() != null;
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



}
