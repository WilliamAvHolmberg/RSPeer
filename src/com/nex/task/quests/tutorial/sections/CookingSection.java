package com.nex.task.quests.tutorial.sections;


import java.util.Arrays;
import java.util.List;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Inventory;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;

import com.nex.task.action.QuestAction;

public class CookingSection extends TutorialSection {

    private static final Area COOK_BUILDING = Area.rectangular(3073, 3083, 3078, 3086);
    private static final Position COOK_DOOR = new Position(3080, 3083, 0);

    public CookingSection() {
        super("Master Chef");
    }

    @Override
    public final void onLoop(){
        if (pendingContinue()) {
            selectContinue();
            return;
        }
        switch (getProgress()) {
            case 130:
                Npc instructor = getInstructor();
                if (!COOK_BUILDING.contains(Players.getLocal()) && (instructor == null || !instructor.isPositionInteractable())) {
                	WalkTo.execute(COOK_BUILDING.getCenter());
                }
                break;
            case 140:
                talkToInstructor();
                break;
            case 150:
                makeDough();
                break;
            case 160:
                bakeDough();
                break;
            case 170:
                WalkTo.execute(new Position(3060, 3090, 0));
                break;
        }
    }

    private void makeDough() {
        if (Inventory.getSelectedItem() == null) {
        	QuestAction.interactInventory("Use", "Pot of flour");
        } else if (QuestAction.interactInventory("Use", "Bucket of water")) {
            Time.sleepUntil(() -> Inventory.contains("Bread dough"), 300, 6000);
        }
    }

    private void bakeDough() {
        if (Inventory.getSelectedItem() == null) {
            QuestAction.interactInventory("Use", "Bread dough");
        } else if (SceneObjects.getNearest("Range").click()) {
            Time.sleepUntil(() -> Inventory.contains("Bread"), 800, 6000);
        }
    }
}
