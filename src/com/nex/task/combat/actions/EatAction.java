package com.nex.task.combat.actions;

import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.scene.Players;

public class EatAction {
	
	public static boolean shallEat() {
		return Players.getLocal().getHealthPercent() < 50;
	}

}
