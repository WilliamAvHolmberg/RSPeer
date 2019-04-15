package com.nex.task.quests.tutorial.sections;



import java.util.Arrays;
import java.util.List;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Magic;
import org.rspeer.runetek.api.component.tab.Spell;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;

public final class WizardSection extends TutorialSection {

    private static final Area WIZARD_BUILDING = Area.polygonal(
            new Position(3140, 3085), new Position(3143, 3088),
            new Position(3140, 3083), new Position(3141, 3084),
            new Position(3140, 3089), new Position(3143, 3089),
            new Position(3137, 3091), new Position(3141, 3091),
            new Position(3138, 3090), new Position(3142, 3090),
            new Position(3139, 3089), new Position(3140, 3089),
            new Position(3141, 3084), new Position(3143, 3084),
            new Position(3141, 3083), new Position(3142, 3083),
            new Position(3138, 3082), new Position(3141, 3082),
            new Position(3138, 3083), new Position(3140, 3083),
            new Position(3139, 3084), new Position(3141, 3084)
    );

    private static final Area CHICKEN_AREA = Area.rectangular(3139, 3090, 3141, 3091);

    public WizardSection() {
        super("Magic Instructor");
    }

    @Override
    public final void onLoop() {
        if (pendingContinue()) {
            selectContinue();
            return;
        }

        if (getInstructor() == null) {
            WalkTo.execute(WIZARD_BUILDING.getCenter());
        }

        switch (getProgress()) {
            case 620:
                talkToInstructor();
                break;
            case 630:
                Tabs.open(Tab.MAGIC);
                break;
            case 640:
                talkToInstructor();
                break;
            case 650:
                if (!CHICKEN_AREA.contains(Players.getLocal().getPosition())) {
                	if(!Players.getLocal().isMoving()) {
                		walkToChickenArea();
                	}else {
                		Log.fine("ALREADY MOVING TOWARDS CHICKENS");
                	}
                } else {
                    attackChicken();
                }
                break;
            case 670:
                if (Dialog.isViewingChatOptions()) {
                    Dialog.process("No, I'm not planning to do that.", "Yes.", "I'm fine, thanks.");
                } else if (Magic.isSpellSelected()) {
                    WalkTo.execute(Players.getLocal().getPosition().randomize(2));
                } else {
                    talkToInstructor();
                }
                break;
        }
    }

    private boolean walkToChickenArea() {
        SceneObject table = SceneObjects.getNearest("Table");
        Position pos = table.getPosition();
        pos = new Position(pos.getX() - Random.nextInt(1, 2), pos.getY() + Random.nextInt(4, 5));
        return Movement.setWalkFlagWithConfirm(pos);//More accurate, less chance of walking outside
       //return WalkTo.execute(CHICKEN_AREA.getCenter());
    }

    private boolean attackChicken() {
        Npc chicken = Npcs.getNearest("Chicken");
        if (chicken != null && Magic.cast(Spell.Modern.WIND_STRIKE, chicken)) {
            Time.sleepUntil(() -> getProgress() != 650, 300, 6000);
            return true;
        }
        return false;
    }
}
