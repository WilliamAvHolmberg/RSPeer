package com.nex.task.quests.tutorial.sections;



import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.rspeer.RSPeer;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;

public final class SurvivalSection extends TutorialSection {

    private final Position GATE_POSITION = new Position(3092, 3091, 0);

    public SurvivalSection() {
        super("Survival Expert");
    }

    int fishToCatch = Random.nextInt(1, 3);
    @Override
    public final void onLoop() {
        if (pendingContinue()) {
            selectContinue();
            return;
        }
        switch (getProgress()) {
            case 20:
                talkToInstructor();
                break;
            case 30:
            	Tabs.open(Tab.INVENTORY);
                break;
            case 40:
                fish();
                break;
            case 50:
            	Tabs.open(Tab.STATS);
                break;
            case 60:
                talkToInstructor();
                break;
            case 70:
                chopTree();
                for(int i = Random.nextInt(0, 3); i>=0; i--)
                    chopTree();//Help prevent insta-banning
                break;
            case 80:
            case 90:
            case 100:
            case 110:
            	Log.fine("case 8000");
                if (!Tabs.isOpen(Tab.INVENTORY)) {
                	Log.fine("inv");
                    Tabs.open(Tab.INVENTORY);
                } else if (!Inventory.contains("Shrimps") && Inventory.getCount("Raw shrimps") < fishToCatch) {
                	Log.fine("fish");
                    fish();
                } else if (SceneObjects.getNearest("Fire") == null) {
                	Log.fine("fire null");
                    if (!Inventory.contains("Logs")) {
                        chopTree();
                    } else {
                        lightFire();
                    }
                } else {
                	Log.fine("cook");
                    cook();
                }
                break;
            case 120:
                SceneObject gate = SceneObjects.getNearest("Gate");
                if (gate != null && gate.distance() < 10) {
                    if (gate.interact("Open")) {
                        Time.sleepUntil(() -> getProgress() == 130, 800, 6000);
                    }
                } else {
                    WalkTo.execute(GATE_POSITION);
                }
                break;
        }
    }

    private void chopTree() {
        final int logCount = Inventory.getCount("Logs");
        SceneObject tree = SceneObjects.getNearest("Tree");
        if (tree != null && tree.interact("Chop down")) {
            Time.sleepUntil(() -> Inventory.getCount("Logs") != logCount, 600, 10000);
        }
    }

    private void fish() {
        Npc fishingSpot = Npcs.getNearest("Fishing spot");
        if (fishingSpot != null && fishingSpot.interact("Net")) {
            long rawShrimpCount = Inventory.getCount("Raw shrimps");
            Time.sleepUntil(() -> Inventory.getCount("Raw shrimps") > rawShrimpCount, 600, 10000);
        }
    }

    private void lightFire() {
    	Log.fine("offf");
        if (standingOnFire()) {
        	Log.fine("on fire");
            WalkTo.execute(Players.getLocal().getPosition().randomize(3));
        } else if (Inventory.getSelectedItem() != null && !"Tinderbox".equals(Inventory.getSelectedItem().getName())) {
        	Log.fine("Tinder");
            Inventory.getFirst("Tinderbox").interact("Use");
        } else if (Inventory.getFirst("Logs").interact("Use")) {
            Position playerPos = Players.getLocal().getPosition();
            Time.sleepUntil(() -> !Players.getLocal().getPosition().equals(playerPos), 600, 10000);
        }
    }

    private boolean standingOnFire() {
        return SceneObjects.getNearest(p-> p.getName().equals("Fire") && p.getPosition().equals(Players.getLocal().getPosition())) != null;
    }

    private Position getEmptyPosition() {
        List<Position> allPositions = Area.surrounding(Players.getLocal().getPosition(), 10).getTiles();

        // Remove any position with an object (except ground decorations, as they can be walked on)
        for (SceneObject object : SceneObjects.getLoaded()) {
        	 if (object.isPositionWalkable()) {
                 continue;
             }
            allPositions.removeIf(position -> object.getPosition().equals(position));
        }

        allPositions.removeIf(position -> !position.isPositionWalkable());

        return allPositions.get(0);
    }

    private void cook() {
        if (Inventory.getSelectedItem() == null) {
            Inventory.getFirst("Raw shrimps").interact("Use");
        } else {
            SceneObject fire = SceneObjects.getNearest("Fire");
            if (fire != null && fire.interact("Use")) {
                long rawShrimpCount = Inventory.getCount("Raw shrimps");
                Time.sleepUntil(() -> Inventory.getCount("Raw shrimps") < rawShrimpCount, 800, 6000);
            }
        }
    }
}
