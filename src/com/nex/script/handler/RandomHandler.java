package com.nex.script.handler;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

public class RandomHandler {

    public static boolean handleRandom() {
        Npc event = Npcs.getNearest(npc -> npc.containsAction("Dismiss") && npc.getTarget() == Players.getLocal());
        if(event == null)
            return false;

        String eventName = event.getName();
        if ((eventName.equals("Genie") || eventName.equals("Drunken Dwarf") || eventName.equals("Dr Jekyll") || eventName.equals("Rick Turpentine")) && event.isPositionWalkable()) {
            if (!Dialog.isOpen()) {
                event.interact(x -> true);
                Time.sleepUntil(Dialog::isOpen, 1000, 10000);
            }
            while (Dialog.canContinue()) {
                Dialog.processContinue();
                Time.sleepWhile(()->Dialog.isOpen() || Dialog.isProcessing(), 1000);
            }
            //NEEDS TESTING
        } else if (eventName.equals("Frog")) {
            Npc frogPrince = Npcs.getNearest(npc -> !npc.containsAction("Dismiss") && npc.getName().equals("Frog") && npc.containsAction("Talk-to"));
            if (!Dialog.isOpen()) {
                frogPrince.interact(x -> true);
                Time.sleepUntil(Dialog::isOpen, 1000, 10000);
            }
            if (Dialog.canContinue()) {
                Dialog.processContinue();
                Time.sleep(200, 500);
            }
            if (Dialog.isViewingChatOptions()) {
                Dialog.process("I suppose so, sure.");
                //  Dialog.process(opt->opt.contains("sure"));
                //Dialog.process(0);//Yes?
                Time.sleep(200, 500);
            }
        } else {
            Time.sleep(200, 500);
            event.interact("Dismiss");
            Time.sleep(300, 500);
        }
        return true;
    }

    static void checkLamp() {
        if (Inventory.contains("Lamp")) {
            if (!Interfaces.isOpen(134)) {
                Inventory.getFirst("Lamp").interact("Rub");
                Time.sleepUntil(() -> Interfaces.isOpen(134), 500, 10000);
            }
            if (Interfaces.isOpen(134)) {
                InterfaceComponent prayer = Interfaces.getComponent(134, 9);//Get prayer XP from the lamp
                InterfaceComponent confirm = Interfaces.getComponent(134, 26);
                prayer.interact("Advance Prayer");
                Time.sleep(1000, 1500);
                confirm.interact("Ok");
                Time.sleepUntil(() -> !Interfaces.isOpen(134), 500, 10000);
            }
            if (Dialog.canContinue()) {
                Dialog.processContinue();
                Time.sleep(200, 500);
            }
        }
    }
}
