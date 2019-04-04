package com.nex.task.quests.tutorial.sections;

import com.nex.script.Nex;
import com.nex.task.CombatTask;
import com.nex.task.quests.tutorial.TutorialIsland;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.*;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;

import com.nex.task.action.QuestAction;
import com.nex.task.combat.CombatTask;

public final class NoobSection extends TutorialSection {

    static final Area GOBLINS_AREA = Area.rectangular(3244, 3227, 3258, 3242);
    static final Area CHICKENS_AREA = Area.rectangular(3225, 3292, 3236, 3300);

    Area chosenArea;
    String chosenMonster;
    boolean ranged = false;

    public NoobSection() {
        super("Fighting Low level Monsters");
        if (Random.nextInt(0, 100) > 50){
            chosenArea = GOBLINS_AREA;
            chosenMonster = "Goblin";
        }else{
            chosenArea = CHICKENS_AREA;
            chosenMonster = "Chicken";
        }
        ranged = Random.nextInt(0, 100) > 50;
    }

    @Override
    public final void onLoop() {
        if (pendingContinue()) {
            selectContinue();
            return;
        }
        if(ranged)
            equipRanged();
        else
            equipMelee();

        if(isAttacking())
            return;
        else if(!chosenArea.contains(Players.getLocal()))
            WalkTo.execute(chosenArea);
        else if(Pickup("Bones", 1))
            return;
        else if(Pickup("Feather", 0))
            return;
        else if(Pickup("Goblin mail", 8))
            return;
        else if(Inventory.contains("Bones"))
            Inventory.getFirst("Bones").interact("Bury");
        else
            attack();
    }

    boolean Pickup(String name, int maxDistance){
        Pickable item = Pickables.getNearest(name);
        if(item == null) return false;
        if(item.distance() > maxDistance)
            return false;
        if(!item.interact("Take"))
            return  false;
        int count = Inventory.getCount(true, name);
        Time.sleepUntil(()->count != Inventory.getCount(true, name), 200, 2000);
        return true;
    }


    void equipMelee() {
        if (Inventory.contains("Bronze sword")) {
            wieldItem("Bronze sword");
        }
        else if (!Equipment.contains("Bronze sword") && Inventory.contains("Bronze pickaxe")) {
            wieldItem("Bronze pickaxe");
        }
        else if (Inventory.contains("Wooden shield")) {
            wieldItem("Wooden shield");
            Time.sleep(100, 800);
            Tabs.open(Tab.COMBAT);
            Time.sleep(100, 800);
            Combat.select(Random.nextInt(0, 3));
        }
    }
    void equipRanged() {
        if (Inventory.contains("Shortbow")) {
            wieldItem("Shortbow");
        } else if (!Equipment.contains("Shortbow")){
            ranged = false;
        } else if (!Equipment.contains("Bronze arrow")) {
            if(Inventory.getCount(true, "Bronze arrow") == 0)
                ranged = false;
            else
                wieldItem("Bronze arrow");
            Time.sleep(100, 800);
            Tabs.open(Tab.COMBAT);
            Time.sleep(100, 800);
            Combat.select(Random.nextInt(0, 3));
        }
    }

    private boolean isAttacking() {
        return Players.getLocal().isAnimating() || Players.getLocal().getTarget() != null && Players.getLocal().getTarget().getName().equals(chosenMonster);
    }

    private void attack() {
        // noinspection unchecked
        Npc giantRat = Npcs.getNearest(npc -> npc.getName().equals(chosenMonster) && npc.getTarget() == null);
        if (giantRat != null && giantRat.interact("Attack")) {
            Time.sleepUntil(() -> Players.getLocal().isAnimating(), 800, 6000);
        }
    }

    private void wieldItem(String name) {
        if (QuestAction.interactInventory("Wield", name)) {
            Time.sleepUntil(() -> Equipment.contains(name), 800,2500);
        }
    }

    public static boolean isFinished(){
        if (!TutorialIsland.DO_NOOB_FIGHTING)
            return true;
        int reqLvl = Math.max(3, 6 - (int)Math.floor(Nex.timeSinceBanWave()));
        return Skills.getCurrentLevel(Skill.ATTACK) > reqLvl || Skills.getCurrentLevel(Skill.STRENGTH) > reqLvl || Skills.getCurrentLevel(Skill.DEFENCE) > reqLvl || Skills.getCurrentLevel(Skill.WOODCUTTING) > reqLvl;
    }
}
