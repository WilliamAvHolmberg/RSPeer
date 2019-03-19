package com.nex.task.quests.tutorial.sections;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;

import com.nex.task.action.QuestAction;

public final class FightingSection extends TutorialSection {

	
	private static final Area INSIDE_RAT_CAGE_GATE_AREA = Area.rectangular(3107, 9517, 3110, 9520);
	private static final Area OUTSIDE_RAT_CAGE_GATE_AREA = Area.rectangular(3111, 9516, 3113, 9521);

	

	public FightingSection() {
		super("Combat Instructor");
	}

	@Override
	public final void onLoop() {
		if (pendingContinue()) {
			selectContinue();
			return;
		}
		switch (getProgress()) {
		case 370:
			talkToInstructor();
			break;
		case 390:
			Tabs.open(Tab.EQUIPMENT);
			break;
		case 400:
			QuestAction.interactAction("View equipment stats");
			break;
		case 405:
			for(InterfaceComponent comp: Interfaces.get(p -> p.containsAction("Close"))) {
				comp.interact("Close");
			}
				Time.sleep(1000);
				wieldItem("Bronze dagger");
			
			break;
		case 410:
			talkToInstructor();
			break;
		case 420:
			if (!Equipment.contains("Bronze sword")) {
				wieldItem("Bronze sword");
			} else if (!Equipment.contains("Wooden shield")) {
				wieldItem("Wooden shield");
			}
			break;
		case 430:
			Tabs.open(Tab.COMBAT);
			break;
		case 440:
			WalkTo.execute(INSIDE_RAT_CAGE_GATE_AREA.getCenter());
			break;
		case 450:
		case 460:
			if (!inRatCage()) {
				WalkTo.execute(INSIDE_RAT_CAGE_GATE_AREA.getCenter());
			} else if (!isAttackingRat()) {
				attackRat();
			}
			break;
		case 470:
			if (inRatCage()) {
				WalkTo.execute(OUTSIDE_RAT_CAGE_GATE_AREA.getCenter());
			} else {
				talkToInstructor();
			}
			break;
		case 480:
		case 490:
			if (!Equipment.contains("Shortbow")) {
				wieldItem("Shortbow");
			} else if (!Equipment.contains("Bronze arrow")) {
				wieldItem("Bronze arrow");
			} else if (!isAttackingRat()) {
				attackRat();
			}
			break;
		case 500:
			if(getLadder().distance() < 5) {
				int z = Players.getLocal().getPosition().getFloorLevel();
				getLadder().interact("Climb-up");
				
				Time.sleepUntil(() -> Players.getLocal().getPosition().getFloorLevel() != z, 5000);
			}else {
				WalkTo.execute(getLadder().getPosition());
			}
			break;
		}
	}
	
	private SceneObject getLadder() {
		return SceneObjects.getNearest("Ladder");
	}

	private boolean inRatCage() {
		return !getInstructor().isPositionInteractable() || INSIDE_RAT_CAGE_GATE_AREA.contains(Players.getLocal());
	}

	

	private boolean isAttackingRat() {
		return Players.getLocal().isAnimating() || Players.getLocal().getTarget() != null && Players.getLocal().getTarget().getName().equals("Giant rat");
	}

	private void attackRat() {
		// noinspection unchecked
		Npc giantRat = Npcs.getNearest(npc -> npc.getName().equals("Giant rat") && npc.getTarget() == null);
		if (giantRat != null && giantRat.interact("Attack")) {
			Time.sleepUntil(() -> Players.getLocal().isAnimating(), 800, 6000);
		}
	}

	private boolean wieldItem(String name) {
		if (QuestAction.interactInventory("Wield", name)) {
			return Time.sleepUntil(() -> Equipment.contains(name), 1500);
		}
		return false;
	}
}
