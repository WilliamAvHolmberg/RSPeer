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

    public static boolean ENABLED = false;

    static long lastCheckedRandoms = 0;
    public static boolean handleRandom() {
        if (!ENABLED)
            return false;
        if(System.currentTimeMillis() - lastCheckedRandoms < 5000)
            return false;
        if(checkLamp())
            return true;

        Npc frogPrinceStuck = Npcs.getNearest(npc -> npc.getName() == "Frog" && npc.getId() == 5431);
        Npc event = Npcs.getNearest(npc -> npc.containsAction("Dismiss") && npc.getTarget() == Players.getLocal());
        if(event == null && frogPrinceStuck == null) {
            lastCheckedRandoms = System.currentTimeMillis();
            return false;
        }

        String eventName = event.getName();
        if(frogPrinceStuck != null){
            if (!Dialog.isOpen()) {
                event.interact(x -> true);
                Time.sleepUntil(Dialog::isOpen, 1000, 10000);
            }
            for(int i = 0; i < 10 && Dialog.isOpen(); i++) {
                while (Dialog.canContinue()) {
                    Dialog.processContinue();
                    Time.sleepWhile(() -> Dialog.isOpen() || Dialog.isProcessing(), 1000);
                }
                if (Dialog.isViewingChatOptions()) {
                    if (!Dialog.process("sorry", "please"))
                        Dialog.process(0);
                    Time.sleepWhile(() -> Dialog.isOpen() || Dialog.isProcessing(), 1000);
                }
            }
        }
        else if ((eventName.equals("Genie") || eventName.equals("Drunken Dwarf") || eventName.equals("Dr Jekyll") || eventName.equals("Rick Turpentine")) && event.isPositionWalkable()) {
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

    static boolean checkLamp() {
        if (Inventory.contains("Lamp")) {
            if (!Interfaces.isOpen(134)) {
                Inventory.getFirst("Lamp").interact("Rub");
                Time.sleepUntil(() -> Interfaces.isOpen(134), 800, 10000);
            }
            if (Interfaces.isOpen(134)) {
                InterfaceComponent prayer = Interfaces.getComponent(134, 9);//Get prayer XP from the lamp
                InterfaceComponent confirm = Interfaces.getComponent(134, 26);
                prayer.interact("Advance Prayer");
                Time.sleep(1000, 1500);
                confirm.interact("Ok");
                Time.sleepUntil(() -> !Interfaces.isOpen(134), 800, 10000);
            }
            if (Dialog.canContinue()) {
                Dialog.processContinue();
                Time.sleep(200, 500);
            }
            return true;
        }
        return false;
    }
}
