package com.nex.task.quests.events;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

import com.nex.task.action.QuestAction;

import javax.swing.plaf.nimbus.State;

public class EnableFixedModeEvent {

	public static boolean EXIT_ON_CREATE = true;

	public static boolean isFixedModeEnabled() {
		return !QuestAction.isVisible(164,29) || !QuestAction.isVisible("Options");
	}

	public static void execute() {
		Log.fine("resize");
		if (QuestAction.interactAction("Fixed mode")) {
			Time.sleepUntil(() -> isFixedModeEnabled(), 100,3000);
		} else if (!QuestAction.isVisible("Fixed mode")) {
			QuestAction.interactButton("Display");
		}else if (QuestAction.isVisible("Options")) {
			if (QuestAction.interactButton("Options")) {
				Time.sleepUntil(() -> QuestAction.isVisible("Display"), 3000);
			}
		} 

		if (isFixedModeEnabled() && EXIT_ON_CREATE) {
			System.exit(1);
		}
	}
}
