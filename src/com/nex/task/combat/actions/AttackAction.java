package com.nex.task.combat.actions;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Combat;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

import com.nex.task.action.Action;

public class AttackAction {

	public static void execute(String monsterName, Area area) {
		if(getAttackingNpc(monsterName, area) != null && !playerIsAttacking()) {
			Log.fine("we have to attack npc that is attacking us");
		}else if(!playerIsAttacking()) {
			Npc npc = getAvailableNpc(monsterName, area);
			if(npc != null) {
			npc.interact("Attack");
			Log.fine("health: " + npc.getHealthPercent());
			Time.sleepUntil(()-> npc.getHealthPercent() == 0, 1000);
			}
		}else {
			Log.fine("all good. we are in combat");
		}
		//already attacking
		//under attack but not attacking
		//attack
	}
	
	public static Npc getAvailableNpc(String monsterName, Area area) {
		return Npcs.getNearest(p-> area.contains(p) && p.getName().contains(monsterName) && p.getTarget() == null);
	}
	public static boolean playerIsAttacking() {
		return Players.getLocal().getTarget() != null;
	}
	
	public static Npc getAttackingNpc(String monsterName, Area area) {
		return Npcs.getNearest(p-> area.contains(p) && p.getName().contains(monsterName) && p.getTarget() != null && p.getTarget().equals(Players.getLocal()));
	}
}
