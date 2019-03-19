package com.nex.task.quests.tutorial.sections;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Production;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;

import com.nex.task.action.QuestAction;

public final class MiningSection extends TutorialSection {

	private static final Area SMITH_AREA = Area.rectangular(3076, 9497, 3082, 9504);

	private int TIN = 10080;
	private int COPPER = 10079;

	private static final Position GATE_POS = new Position(3099, 9503, 0);

	public MiningSection() {
		super("Mining Instructor");
	}

	@Override
	public final void onLoop() {
		if (pendingContinue()) {
			selectContinue();
			return;
		}

		switch (getProgress()) {
		case 260:
			if (WalkTo.execute(SMITH_AREA.getCenter())) {
				talkToInstructor();
			}
			break;
		case 270:
			prospect(TIN);
			break;
		case 280:
			prospect(COPPER);
			break;
		case 290:
			talkToInstructor();
			break;
		case 300:
			mine(TIN);
			for(int i = Random.nextInt(-2, 4); i >= 0; i--)
				mine(TIN);//Help prevent insta-banning
			break;
		case 310:
			mine(COPPER);
			for(int i = Random.nextInt(-2, 4); i >= 0; i--)
				mine(COPPER);//Help prevent insta-banning
			break;
		case 320:
			if (Tabs.open(Tab.INVENTORY)) {
				smelt();
			}
			break;
		case 330:
			talkToInstructor();
			break;
		case 340:
			if (Tabs.open(Tab.INVENTORY)) {
				smith();
			}
			break;
		case 350:
			InterfaceComponent daggerWidgetOpt = getDaggerWidget();
			if (daggerWidgetOpt != null) {
				Log.fine("lets click");
				if (daggerWidgetOpt.click()) {
					Time.sleepUntil(() -> Inventory.contains("Bronze dagger"), 6000, 600);
				}
			} else {
				smith();
			}
			break;
		case 360:
			WalkTo.execute(GATE_POS);
			break;
		}
	}

	private void smith() {
		if (!SMITH_AREA.contains(Players.getLocal().getPosition())) {
			WalkTo.execute(SMITH_AREA.getCenter());
		} else if (Inventory.getSelectedItem() == null) {
			QuestAction.interactInventory("Use", "Bronze bar");
		} else if (SceneObjects.getNearest("Anvil").interact("Use")) {
			Time.sleepUntil(() -> getDaggerWidget() != null, 5000, 600);
		}
	}

	private InterfaceComponent getDaggerWidget() {
		return QuestAction.get(312,2);
	}

	private void smelt() {
		if (Inventory.getSelectedItem() == null) {
			QuestAction.interactInventory("Use", "Tin ore");
		} else if (SceneObjects.getNearest("Furnace").interact("Use")) {
			Time.sleepUntil(() -> Inventory.contains("Bronze bar"), 800, 30000);
			if(Production.isOpen()) {
				Production.initiate();
				Time.sleepUntil(() -> Inventory.contains("Bronze bar"), 800, 30000);
			}
		}
	}

	private void prospect(int id) {
		SceneObject closestRock = SceneObjects.getNearest(id);
		if (closestRock != null && closestRock.interact("Prospect")) {
			Time.sleepUntil(this::pendingContinue, 600, 6000);
		}
	}

	private void mine(int id) {
		int count = Inventory.getCount(id);
		SceneObject closestRock = SceneObjects.getNearest(id);
		if (closestRock != null && closestRock.interact("Mine")) {
			Time.sleepUntil(()->Inventory.getCount(id) != count, 600, 6000);
			Time.sleepUntil(this::pendingContinue, 600, 6000);
		}
	}

}
