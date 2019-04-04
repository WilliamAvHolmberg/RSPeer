package com.nex.task.fishing.actions;

import com.nex.script.walking.WalkTo;
import com.nex.task.action.Action;
import com.nex.task.fishing.Fish;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.util.Arrays;

public class FishSpotAction extends Action {

    public static int fish(Fish fish) {

        if (Dialog.canContinue()) {
            Dialog.processContinue();
        }

        if (Inventory.isFull()) {
            for (Item item : Inventory.getItems()) {
                if (item.getName().equals(fish.getEquipment())) {
                    // Don't do anything
                } else if (item.getName().equals(fish.getBait())) {
                    // Do nothing
                }else if (item.getId() == 995) {
                    // Do nothing
                } else {
                    item.interact("Drop");
                    Time.sleep(Random.low(150, 250));
                }
            }
        } else if(!Players.getLocal().isAnimating()) {
            final String spotName = fish.getSpot();
            final String fishAction = fish.getAction();
            Log.fine("Looking for " +  fishAction + " " + spotName);
            Npc fishSpot = Npcs.getNearest(o-> o.getName().contains(spotName) && Arrays.stream(o.getActions()).anyMatch((s)->s.contains(fishAction)));
            if (fishSpot != null) {
                Log.fine(fishSpot.getName() + "->" + fish.getAction());
                fishSpot.interact(fish.getAction());
                Time.sleepUntil(() -> Players.getLocal().isAnimating() || Dialog.isOpen(), 200, 1000);
            }

        }
        return 300;
    }

    static boolean shouldDrop(String itemName){
        switch (itemName){
            case "Raw Shrimp":
                return true;
        }
        return false;
    }
}
