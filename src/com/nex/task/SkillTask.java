package com.nex.task;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.ChatMessageListener;
import org.rspeer.runetek.event.listeners.ObjectSpawnListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;

import com.nex.communication.NexHelper;
import com.nex.handler.gear.Gear;
import com.nex.task.woodcutting.WoodcuttingTask;

public abstract class SkillTask extends NexTask {
	
	private int wantedLevel = 99;
	protected Skill skill;
	protected Area bankArea;
	protected Area actionArea;
	protected int startExperience = 0;
	
	public abstract boolean isFinished();
	protected SkillTask setBankArea(Area bankArea) {
		this.bankArea = bankArea;
		return this;

	}

	protected SkillTask setActionArea(Area actionArea) {
		this.actionArea = actionArea;
		return this;
	}
	
	protected Area getBankArea() {
		if(bankArea == null) {
			return Area.surrounding(BankLocation.getNearest().getPosition(), 10);
		}
		return bankArea;
	}
	
	public SkillTask setWantedLevel(int wantedLevel) {
		this.wantedLevel = wantedLevel;
		return this;
	}
	public int getWantedLevel() {
		return wantedLevel;
	}
	
	public int getStartExperience() {
		return startExperience;
	}
	
	public abstract Skill getSkill();
	
	public int getExperiencePerHour() {
		if(skill != null) {
			return (int) getPerHour((Skills.getExperience(getSkill()) - getStartExperience()));
		}
		return 0;
	}
	public int getMoneyPerHour() {
		return 0;
	}
	
	@Override
	public String getLog() {
		return getLog(getTaskID(), getExperiencePerHour(), 0);
	}

	public static String getLog(String task_id, int xp, int loot) {
		String respond = task_id;
		Player player = Players.getLocal();
		if(player == null) return null;
		Position pos = player.getPosition();
		respond += ":position;" + pos.getX() + ";" + pos.getY() + ";" + pos.getFloorLevel();

		respond += ":xp;" + xp;

		respond += ":loot;" + loot;

		return respond;
	}
	


}
