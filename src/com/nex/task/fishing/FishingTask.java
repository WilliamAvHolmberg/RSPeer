package com.nex.task.fishing;



import java.awt.*;
import java.util.HashSet;
import java.util.List;

import com.nex.script.inventory.InventoryItem;
import com.nex.task.fishing.Fish;
import com.nex.task.fishing.actions.FishSpotAction;
import com.nex.task.helper.InteractionHelper;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.InterfaceOptions;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.event.listeners.ChatMessageListener;
import org.rspeer.runetek.event.listeners.SkillListener;
import org.rspeer.runetek.event.types.*;
import org.rspeer.ui.Log;

import com.nex.handler.gear.GearHandler;
import com.nex.handler.gear.GearItem;
import com.nex.script.Exchange;
import com.nex.script.Nex;
import com.nex.script.banking.BankHandler;
import com.nex.script.banking.DepositAll;
import com.nex.script.banking.DepositAllExcept;
import com.nex.script.banking.WithdrawItemEvent;
import com.nex.script.items.RSItem;
import com.nex.script.items.RequiredItem;
import com.nex.script.items.WithdrawItem;
import com.nex.task.IMoneyTask;
import com.nex.task.SkillTask;
import com.nex.task.woodcutting.actions.CutTreeAction;

public class FishingTask extends SkillTask implements ChatMessageListener, IMoneyTask, SkillListener {

    int fishCaught;
    int priceEa;
    int profit;

    RSItem equipment;
    RSItem bait;
    
    InventoryItem item;

    Fish selectedFish;

    boolean autoFish = false;
    boolean powerFish = false;

    HashSet<String> dropList = new HashSet<>();

    void calcAutoFish(){
        int fishLvl = Skills.getCurrentLevel(Skill.FISHING);
        if(fishLvl < 20){
            powerFish = true;
            selectedFish = Fish.SHRIMP;
            setActionArea(Area.rectangular(3242, 3150, 3247, 3159));
            setBankArea(Area.surrounding(BankLocation.GRAND_EXCHANGE.getPosition(), 10));
        }else if(fishLvl < 40){
            powerFish = true;
            selectedFish = Fish.TROUT;
            setActionArea(Area.rectangular(3099, 3422, 3112, 3435));
            setBankArea(Area.surrounding(BankLocation.GRAND_EXCHANGE.getPosition(), 10));
        }else if(fishLvl < 60){
            powerFish = false;
            selectedFish = Fish.LOBSTER;
            setActionArea(Area.rectangular(2922, 3174, 2927, 3181));
            setBankArea(Area.surrounding(BankLocation.PORT_SARIM_DB.getPosition(), 10));
        }
        else if(fishLvl <= 99){
            powerFish = false;
            selectedFish = Fish.SWORDFISH;
            setActionArea(Area.rectangular(2922, 3174, 2927, 3181));
            setBankArea(Area.surrounding(BankLocation.PORT_SARIM_DB.getPosition(), 10));
        }
    }
    void compileData(){
        equipment = RSItem.fromName(selectedFish.getEquipment());
        addRequiredItem(equipment);
        requiredInventory.addItem(new InventoryItem(1, equipment, 1));
        if(selectedFish.getBait().length() > 0) {
            bait = RSItem.fromName(selectedFish.getBait());
            Log.fine("Gonna use " + bait.getName() + ":" + bait.getId());
            item = new InventoryItem(500, bait, 1300);
            requiredInventory.addItem(item);
        }
        Log.fine("Going to fish for " + selectedFish.getRawName());
        this.priceEa = Exchange.getPrice(Exchange.getID(selectedFish.getRawName()));
        this.startExperience = Skills.getExperience(getSkill());
    }

    public FishingTask(Area actionArea, Area bankArea, String fishname) {

        dropList.add(Fish.SHRIMP.getRawName());
        dropList.add(Fish.ANCHOVIES.getRawName());
        dropList.add(Fish.TROUT.getRawName());
        dropList.add(Fish.SALMON.getRawName());

        if(fishname == null || fishname.length() == 0 || true) {
            calcAutoFish();
            autoFish = true;
        }
        else {
            String FISH_CODE = fishname.toUpperCase().replace(" ", "_").replace("RAW_", "");
            Log.fine("Gonna Fish: " + fishname);
            selectedFish = Fish.valueOf(FISH_CODE);
            setActionArea(actionArea);
            setBankArea(bankArea);
        }

        compileData();
    }

    static final Area PortSarim = Area.rectangular(3023, 3212, 3033, 3222);
    static final Area KaramjaPort = Area.rectangular(2953, 3145, 2960, 3147);
    static final Area KaramjaIsland = Area.rectangular(2881, 3132, 2964, 3195);
    // TODO - More generic. Do not check if player is in area all the time.
    @Override
    public int loop() {
        if(Game.isInCutscene()) return 500;
        GearItem itemToEquip = requiredGear.getItemToEquip();
        if( itemToEquip != null) {
            GearHandler.addItem(itemToEquip);
        } else if (Inventory.isFull() && !powerFish) {
            if(bait == null)
                BankHandler.addBankEvent(new DepositAllExcept(equipment.getName()).setBankArea(bankArea));
            else
                BankHandler.addBankEvent(new DepositAllExcept(equipment.getName(), bait.getName()).setBankArea(bankArea));
        } else if (playerNeedEquipment() || playerNeedsBait() || playerNeedsMoney()) {
            Area bank = bankArea;
            if (bankArea.contains(BankLocation.PORT_SARIM_DB.getPosition()))
                bank = Area.surrounding(BankLocation.DRAYNOR.getPosition(), 5);
            if (playerNeedEquipment()) {
                BankHandler.addBankEvent(new WithdrawItemEvent(new WithdrawItem(equipment, 1,1)).setBankArea(bank));
            }
            if (playerNeedsBait()) {
                BankHandler.addBankEvent(new WithdrawItemEvent(item).setBankArea(bank));
            }
            if (playerNeedsMoney()) {
                BankHandler.addBankEvent(new WithdrawItemEvent(new WithdrawItem(new RSItem("Coins", 995), 1200,1200)).setBankArea(bank));
            }
        }
        else if(!actionArea.contains(Players.getLocal()) && !Players.getLocal().isAnimating()) {
            if(HandlePortSarimTo(actionArea))
                return Random.low(400, 800);
            WalkTo.execute(actionArea.getCenter());
            return Random.low(600, 1500);
        }
        else {
            FishSpotAction.fish(selectedFish);
        }
        return 0;
    }

    private boolean getOffBoat() {
        if (Players.getLocal().getPosition().getFloorLevel() == 1) {
            if(Dialog.isOpen()) {
                Dialog.processContinue();
                Time.sleepWhile(Dialog::isOpen, 200, 2100);
            }
            SceneObject gangplank = SceneObjects.getNearest("Gangplank");
            if (gangplank != null) {
                gangplank.interact("Cross");
                Time.sleep(800, 1200);
                Time.sleepWhile(()->Players.getLocal().isMoving() || Players.getLocal().isAnimating(), 200, 3000);
            }
        }
        return false;
    }
    boolean HandlePortSarimTo(Area area){
        getOffBoat();
        KaramjaIsland.setIgnoreFloorLevel(true);
        Position curPos = Players.getLocal().getPosition();
        boolean targetOnIsland = KaramjaIsland.contains(area.getCenter());
        boolean imOnIsland = KaramjaIsland.contains(curPos);
        if(imOnIsland == targetOnIsland)
            return false;
        if(KaramjaIsland.contains(area.getCenter())){//Is our target on the island
            InteractWithSailor(PortSarim, "Seaman Lorris", "Captain Tobias", "Seaman Thresnor");
        }
        else {//Target is on the mainland
            InteractWithSailor(KaramjaPort, "Customs officer");
        }
        return true;
    }
    boolean InteractWithSailor(Area area, String ... names){
        Npc sailor = Npcs.getNearest(names);
        if(sailor == null) {
            WalkTo.execute(area);
            return true;
        }
        if(!sailor.interact("Pay-fare"))
            return true;
        if(!Time.sleepWhile(()->Players.getLocal().isMoving(), 200, 6000))
            return true;
        if(!Time.sleepUntil(()-> Dialog.isOpen(), 200, 6000))
            return true;
        while(Dialog.isOpen()){
            if(Dialog.isProcessing()){}
            else if(Dialog.canContinue()) Dialog.processContinue();
            else if(Dialog.getChatOptions().length > 0) {
                InterfaceComponent yes = Dialog.getChatOption(
                        option -> option.contains("Yes") ||
                                option.contains("this ship") ||
                                option.contains("Search away")  ||
                                option.contains("Ok"));
                if(yes == null) yes = Dialog.getChatOptions()[0];
                yes.click();
            }
            Time.sleepWhile(()->Dialog.isOpen() && Dialog.isProcessing(), 300,2000);
        }
        return true;
    }

    boolean playerNeedEquipment(){
        return (!Inventory.contains(equipment.getName()));
    }
    boolean playerNeedsBait(){
        return (bait != null && !Inventory.contains(bait.getId()));
    }
    boolean playerNeedsMoney(){
        if(KaramjaIsland.contains(Players.getLocal()))//Dont need money. We on the island.
            return false;
        if(selectedFish != Fish.LOBSTER && selectedFish != Fish.SWORDFISH)
            return false;
        if(Inventory.getCount(true, 995) > 30)
            return false;
        return true;
    }

    @Override
    public Skill getSkill() {
        return Skill.FISHING;
    }

    @Override
    public boolean isFinished() {
        return Skills.getCurrentLevel(getSkill()) >= getWantedLevel();
    }
    @Override
    public void notify(ChatMessageEvent event) {
        if(event.getType() == ChatMessageType.FILTERED &&
                event.getMessage().contains("You catch")) {
            Log.fine("We got a fish");
            fishCaught ++;
            Item latestFish = Inventory.getLast(i->i.getName().startsWith("Raw"));
            if(latestFish != null) {
                priceEa = Exchange.getPrice(latestFish.getId());
                profit += priceEa;
            }
        }
    }

    @Override
    public void notify(ObjectSpawnEvent spawnEvent) {
        CutTreeAction.get().notify(spawnEvent);
    }

    @Override
    public void notify(RenderEvent event) {
        Graphics g = event.getSource();
        int y = 300;
        g.drawString("Current Task: " + getSkill() + "->" + getWantedLevel(), 300, y+=25);
        g.drawString("Fish price: " + priceEa, 300, y+=25);
        g.drawString("Fish caught: " + fishCaught, 300, y+=25);
        g.drawString("Ran for: " + getTimeRanMS(), 300, y+=25);
        g.drawString("Fish per hour: " + getPerHour(fishCaught), 300, y+=25);
        g.drawString("Money per hour: " + getMoneyPerHour(), 300, y+=25);
    }

    int lastLevel = 0;
    @Override
    public void notify(SkillEvent skillEvent) {
        if (lastLevel == 0) lastLevel = Skills.getCurrentLevel(Skill.FISHING);
        if(Skills.getCurrentLevel(Skill.FISHING) != lastLevel && autoFish) {
            calcAutoFish();
            compileData();
        }
    }

    @Override
    public String getLog() {
        return getLog(getTaskID(), getExperiencePerHour(), getMoneyPerHour());
    }

    @Override
    public void removeTask() {
        //TASK LOG TODO
    }

    @Override
    public int getMoneyPerHour() {
        return (int) (getPerHour(profit));
    }

}
