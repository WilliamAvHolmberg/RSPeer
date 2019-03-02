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

public class FishSpotAction extends Action {

    public static int fish(Fish fish) {

        Log.info(fish.getAction());
        if (Dialog.canContinue()) {
            if (Dialog.processContinue()) {
                Time.sleep(1000, 1200);
            }
        }
        Npc fishSpot = Npcs.getNearest(fish.getSpot());

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
        } else {
            if (fishSpot != null && !Players.getLocal().isAnimating()) {
                fishSpot.interact(fish.getAction());
                Time.sleepUntil(() -> Players.getLocal().isAnimating() || Dialog.isOpen(), 1000);
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
