package com.nex.task.combat.actions;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Combat;
import org.rspeer.runetek.api.component.tab.Skill;

public class AttackStyleAction {
	
	
	public static void execute(Skill skill) {
		Combat.select(getStyle(skill));
		Time.sleepUntil(() -> isCorrect(skill), 5000);
	}
	
	public static boolean isCorrect(Skill skill) {
		return Combat.getSelectedStyle() == getStyle(skill);
	}
	private static int getStyle(Skill skill) {
		switch (skill) {
		case ATTACK:
			return 0;
		case DEFENCE:
			return 3;
		case STRENGTH:
			return 1;
		default:
			return Combat.getSelectedStyle();
		}
	}
}
