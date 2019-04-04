package com.nex.task.quests;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import com.nex.script.Nex;
import com.nex.script.walking.WalkTo;
import com.nex.task.SkillTask;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Magic;
import org.rspeer.runetek.api.component.tab.Spell;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
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
import org.rspeer.ui.Log;


public class ImpCatcherQuest extends QuestTask {

    private final Area entireTower = Area.rectangular(3102, 3153, 3115, 3171);
    private final Area wizardTowerOutside = Area.rectangular(3106, 3165, 3112, 3171, 0);
    private final Area wizardTowerLobby = Area.rectangular(3105, 3161, 3109, 3165, 0);
    private final Area wizardTowerLobby2 = Area.rectangular(3103, 3159, 3106, 3160, 0);
    private final Area wizardTowerFloor1 = Area.rectangular(3103, 3159, 3106, 3162, 1);
    private final Area wizardTowerFloor2 = Area.rectangular(3101, 3159, 3107, 3165, 2);

    private final InventoryItem whiteBead = new InventoryItem(1, new RSItem("White bead", 434), 1);
    private final InventoryItem yellowBead = new InventoryItem(1, new RSItem("Yellow bead", 434), 1);
    private final InventoryItem blackBead = new InventoryItem(1, new RSItem("Black bead", 434), 1);
    private final InventoryItem redBead = new InventoryItem(1, new RSItem("Red bead", 434), 1);

    public NexInventory requiredInventory = new NexInventory()
            .setItems(new ArrayList<InventoryItem>(Arrays.asList(whiteBead, yellowBead, blackBead, redBead )));

    public ImpCatcherQuest(){
        entireTower.setIgnoreFloorLevel(true);
    }
    @Override
    public boolean isFinished() {
        return Quest.GOBLIN_DIPLOMACY.isCompleted() && !entireTower.contains(Players.getLocal());
    }

    public int loop() {
        if(Game.isInCutscene())
            return 600;
        else if (pendingOption()) {
            selectOption("please", "I'll try.");
        } else if (pendingContinue()) {
            selectContinue();
        }
        else if(getQuest().isCompleted() && entireTower.contains(Players.getLocal())){
            if(!Tabs.open(Tab.MAGIC))
                Tabs.open(Tab.MAGIC);
            if (Magic.cast(Spell.Modern.HOME_TELEPORT))
                Time.sleepUntil(()-> !entireTower.contains(Players.getLocal()), 1000, 60000);
        }
        else if (!entireTower.contains(Players.getLocal()) && getItemToWithdraw(requiredInventory) != null) {
            BankHandler.addBankEvent(new WithdrawItemEvent(getItemToWithdraw(requiredInventory)));
        } else if (walkToWizard()) {
            return 600;
        }else if(!Dialog.isOpen()) {
            talkToNpc("Wizard Mizgog");
        }

        return 200;
    }

    boolean walkToWizard(){
        Position pos = Players.getLocal().getPosition();
        if (!entireTower.contains(pos)) {
            WalkTo.execute(wizardTowerOutside.getCenter());
        }
        else if(pos.getFloorLevel() == 2){
            return false;
        }
        else if(pos.getFloorLevel() == 0) {
            if (wizardTowerLobby2.contains(pos)) {
                climbUpStairs();
            }else if (wizardTowerLobby.contains(pos)) {
                if (!openAreaDoor(wizardTowerOutside))
                    WalkTo.execute(wizardTowerLobby.getCenter(), 0);
            } else if (wizardTowerOutside.contains(pos)) {
                if (!openAreaDoor(wizardTowerOutside))
                    WalkTo.execute(wizardTowerLobby.getCenter(), 0);
            }
        }else if(pos.getFloorLevel() == 1){
            climbUpStairs();
        }
        return true;
    }
    SceneObject getStairs(){
        return SceneObjects.getNearest("Staircase");
    }
    boolean climbUpStairs(){
        SceneObject stairs = getStairs();
        final int curFloor = Players.getLocal().getFloorLevel();
        if(stairs.interact("Climb-up"))
            return Time.sleepWhile(()->Players.getLocal().getFloorLevel() == curFloor, 10000);
        return false;
    }
    boolean openAreaDoor(Area area){
        SceneObject door = SceneObjects.getNearest(o->o.getName() == "Door" && Arrays.asList(o.getActions()).contains("Open") && area.contains(o.getPosition()));
        if(door == null) return false;
        return door.interact("Open");
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
        return Quest.IMP_CATCHER;
    }

    public static Quest getThisQuest() {
        return Quest.IMP_CATCHER;
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
