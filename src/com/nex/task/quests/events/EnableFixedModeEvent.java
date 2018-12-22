package com.nex.task.quests.events;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

import com.nex.task.action.QuestAction;

public class EnableFixedModeEvent {

	public static boolean isFixedModeEnabled() {
		return !QuestAction.isVisible(164,29) || !QuestAction.isVisible("Options");
	}

	public static void execute() {
		Log.fine("resize");
		if (QuestAction.interactAction("Fixed mode")) {
			Time.sleepUntil(() -> isFixedModeEnabled(), 3000);
		} else if (!QuestAction.isVisible("Fixed mode")) {
			QuestAction.interactButton("Display");
		}else if (QuestAction.isVisible("Options")) {
			if (QuestAction.interactButton("Options")) {
				Time.sleepUntil(() -> QuestAction.isVisible("Display"), 3000);
			}
		} 

		if (isFixedModeEnabled()) {
			System.exit(1);
		}
	}
}
