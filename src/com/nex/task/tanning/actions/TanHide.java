package com.nex.task.tanning.actions;

import com.nex.script.Exchange;
import com.nex.task.tanning.TanningTask;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.script.task.Task;

public class TanHide extends Task {

    public static int TANNER_ID = 3231;

    private TanningTask taskRunner;
    public TanHide(TanningTask taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public boolean validate() {
        return Conditions.nearTanner() && Conditions.gotEnoughCoins() && Conditions.gotCowhide();
    }

    @Override
    public int execute() {
        if (Conditions.tanInterfaceIsOpen()) {
            InterfaceComponent leatherComponent = Interfaces.getComponent(324, 124);

            if (leatherComponent.interact(ActionOpcodes.INTERFACE_ACTION)) {
                // wait for all cowhides to turn into leather
                if (Time.sleepUntil(() -> !Conditions.gotCowhide(), 3000)) {
                    Item leather = Inventory.getFirst(taskRunner.ANY_TANNED);
                    int count = Inventory.getCount(taskRunner.ANY_TANNED);
                    taskRunner.totalTanned += count;
                    taskRunner.totalProfit += Exchange.getPrice(leather.getId()) * count;
                    return 300;
                }
            }
            return 600;
        } else {
            Npc tanner = Npcs.getNearest(TANNER_ID);
            if (tanner.interact("Trade")) {
                Time.sleepUntil(Conditions::tanInterfaceIsOpen, 7000);
                return 400;
            }
            return 600;
        }
    }
}
