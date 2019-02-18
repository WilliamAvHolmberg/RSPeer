package com.nex.task.quests.tutorial.sections;



import java.util.Arrays;
import java.util.List;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
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

 
    private static final Area CHICKEN_AREA = Area.rectangular(3139, 3091, 3140, 3090);

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
                    walkToChickenArea();
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
        return Movement.setWalkFlagWithConfirm(CHICKEN_AREA.getCenter().randomize(1));//More accurate, less chance of walking outside
       //return WalkTo.execute(CHICKEN_AREA.getCenter());
    }

    private boolean attackChicken() {
        Npc chicken = Npcs.getNearest("Chicken");
        if (chicken != null && Magic.cast(Spell.Modern.WIND_STRIKE, chicken)) {
            Time.sleepUntil(() -> getProgress() != 650, 3000, 600);
            return true;
        }
        return false;
    }
}
