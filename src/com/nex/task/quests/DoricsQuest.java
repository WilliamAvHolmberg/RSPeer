package com.nex.task.quests;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import com.nex.script.grandexchange.BuyItemHandler;
import com.nex.task.SkillTask;
import com.nex.task.helper.InteractionHelper;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;

import com.nex.script.Quest;
import com.nex.script.banking.BankHandler;
import com.nex.script.banking.WithdrawItemEvent;
import com.nex.script.inventory.InventoryItem;
import com.nex.script.inventory.NexInventory;
import com.nex.script.items.RSItem;
import com.nex.task.QuestTask;



public class DoricsQuest extends QuestTask {

    private final Area doricsArea = Area.rectangular(2950, 3449, 2952, 3452);
    private final InventoryItem clay = new InventoryItem(6, new RSItem("Clay", 434), 6);
    private final InventoryItem ironOre = new InventoryItem(2, new RSItem("Iron ore", 440), 2);
    private final InventoryItem copperOre = new InventoryItem(4, new RSItem("Copper ore", 436), 4);
    public NexInventory requiredInventory = new NexInventory()
            .setItems(new ArrayList<InventoryItem>(Arrays.asList(clay, ironOre, copperOre )));


    public int loop() {
        if (pendingOption()) {
            selectOption("anvil", "material", "right back");
        } else if (pendingContinue()) {
            selectContinue();
        }
        else if (getItemToWithdraw(requiredInventory) != null) {
            BankHandler.addBankEvent(new WithdrawItemEvent(getItemToWithdraw(requiredInventory)).setBankArea(BuyItemHandler.getGEArea()));
            return 500;
        }
        else if (!inArea(doricsArea)) {
            walkTo(doricsArea);
            if(doricsArea.getCenter().distance() < 10){
                SceneObject door = SceneObjects.getNearest(so->so.getName() == "Door" && so.isPositionInteractable());
                if(door != null && door.interact("Open"))
                    Time.sleepWhile(()-> Players.getLocal().isMoving() || Players.getLocal().isAnimating(), 200, 3000);
            }
        }else if(!Dialog.isOpen()) {
            if(!talkToNpc("Doric"))
                InteractionHelper.HopRandomWorld();
        }

        return 200;
    }

    @Override
    public String getLog() {
        return SkillTask.getLog(getTaskID(), 0, 0);
    }

    @Override
    public void removeTask() {
        // TODO queue tutorial island is done. Add RunTime
        //TaskHandler.getCurrentTask() = null;

    }



    public int getQuestConfig() {
        return 29;
    }


    public Quest getQuest() {
        return Quest.DORICS_QUEST;
    }

    public static Quest getThisQuest() {
        return Quest.DORICS_QUEST;
    }

    @Override
    public void notify(RenderEvent arg0) {
        // TODO Auto-generated method stub

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
