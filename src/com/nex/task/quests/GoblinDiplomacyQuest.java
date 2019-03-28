package com.nex.task.quests;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import com.nex.script.walking.WalkTo;
import com.nex.task.SkillTask;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;

import com.nex.script.Quest;
import com.nex.script.inventory.InventoryItem;
import com.nex.script.inventory.NexInventory;
import com.nex.script.items.RSItem;
import com.nex.task.QuestTask;
import org.rspeer.ui.Log;


public class GoblinDiplomacyQuest extends QuestTask {

	public static final Position goblinPos = new Position(2957, 3511, 0);

	private InventoryItem goblinMail = new InventoryItem(3, new RSItem("Goblin mail", 288), 3);
	private InventoryItem blueDye = new InventoryItem(1, new RSItem("Blue dye", 1767), 1);
	private InventoryItem orangeDye = new InventoryItem(1, new RSItem("Orange dye", 1769), 1);

	private InventoryItem orangeGoblinMail = new InventoryItem(1, new RSItem("Orange goblin mail", 286), 1);
	private InventoryItem blueGoblinMail = new InventoryItem(1, new RSItem("Blue goblin mail", 287), 1);

	@Override
	public boolean isFinished() {
		return Quest.GOBLIN_DIPLOMACY.isCompleted() && !(Game.isInCutscene() && SceneObjects.getNearest(16560) != null);
	}

	@Override
	public int loop() {
		if(Quest.GOBLIN_DIPLOMACY.isCompleted()) {
			Log.fine("stuck");
			WalkTo.execute(Players.getLocal().getPosition().randomize(3));
		}

		switch (getCurrentSection()) {
		case 0:
			walkAndTalkTo(goblinPos, "General Bentnoze",
					new NexInventory().addItem(goblinMail).addItem(blueDye).addItem(orangeDye),
					"Do you want me to pick an armour colour for you?", "What about a different colour?",
					"No, he doesn't look fat");
			break;
		case 3: // orange
			if (createItem(orangeGoblinMail, orangeDye, goblinMail.setAmount(3))) {
				walkAndTalkTo(goblinPos, "General Bentnoze", "I have some orange armour here", "No, he doesn't look fat");
			}
			break;
		case 4: // blue
			if (createItem(blueGoblinMail, blueDye, goblinMail.setAmount(2))) {
				walkAndTalkTo(goblinPos, "General Bentnoze", "I have some blue armour here", "No, he doesn't look fat");
			}
			return 600;
		case 5:
			if (inCutScene()) { // cutscene
				if (pendingContinue()) {
					selectContinue();
				}
			} else if (Inventory.contains("Goblin mail")) {
				walkAndTalkTo(goblinPos, "General Bentnoze", "I have some brown armour here", "No, he doesn't look fat");
			}
			break;
		}

		return 600;
	}

	public int getQuestConfig() {
		return 62;
	}

	@Override
	public String getLog() {
		return SkillTask.getLog(getTaskID(), 0, 0);
	}



	@Override
	public void removeTask() {
		// TODO queue tutorial island is done. Add RunTime
	
	}

	

	public Quest getQuest() {
		return Quest.GOBLIN_DIPLOMACY;
	}

	public static Quest getThisQuest() {
		return Quest.GOBLIN_DIPLOMACY;
	}

	@Override
	public void notify(RenderEvent event) {
		Graphics g = event.getSource();
		g.drawString("STATE:" + getCurrentSection(), 50,50);
		
	}

	@Override
	public void notify(ChatMessageEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notify(ObjectSpawnEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	

}
