package com.nex.task.boredmule;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.nex.script.grandexchange.BuyItemEvent;
import com.nex.script.grandexchange.BuyItemHandler;
import com.nex.script.grandexchange.GrandExchangeHandler;
import com.nex.script.handler.TaskHandler;
import com.nex.script.items.GEItem;
import com.nex.task.actions.mule.DepositItemToPlayer;
import com.nex.task.helper.InteractionHelper;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Trade;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.event.listeners.ChatMessageListener;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ChatMessageType;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.task.Task;
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

    public class BoredMuleTask extends Task implements ChatMessageListener {

    public BoredMuleTask() {
        Game.getEventDispatcher().register(this);
    }
    String[] sellableItems = new String[]{"Trout", "Salmon", "Chicken"};

    @Override
    public boolean validate() {
        return TaskHandler.getCurrentTask() == null;
    }
    public void deregister(){
        Game.getEventDispatcher().deregister(this);
    }

    DepositItemToPlayer tradeAction;
    BuyItemEvent buyer;
    @Override
    public int execute() {
        if (Inventory.getCount(sellableItems) < 10) {
            if(buyer == null) {
                int count = Random.nextInt(10, 15);
                RSItem item = RSItem.fromName(sellableItems[Random.nextInt(0, sellableItems.length - 1)]);
                buyer = new BuyItemEvent(new GEItem((new WithdrawItem(item, count, count))));
            }
            BuyItemHandler.execute(buyer);
            return 200;
        }
//        Game.getRemainingMembershipDays()
        buyer = null;

        if(tradeAction != null) {
            if (Trade.isOpen(false) && Trade.getMyItems().length > 0 && Trade.getTheirItems().length == 0)
                return Random.mid(90, 300);
            return tradeAction.execute();
        }
        else if(tradeRequestedBy != null){
            Player randomPlayer = Players.getNearest(tradeRequestedBy);
            if(randomPlayer != null){
                int itemId = Inventory.getFirst(sellableItems).getId();
                tradeAction = new DepositItemToPlayer(tradeRequestedBy,itemId, Inventory.getCount(itemId));
            }
        } else tradeAction = null;

        spam();
        return Random.mid(90, 300);
    }

    long lastSpammed = 0;
    int spamInterval = Random.mid(10000, 17000);
    boolean spam(){
        if(System.currentTimeMillis() - lastSpammed < spamInterval)
            return false;
        Item item = Inventory.getFirst(sellableItems);
        int count = Inventory.getCount(item.getId());
        String message = "Selling " + count + " " + item.getName();
        SendText(glitchString(message));
        lastSpammed = System.currentTimeMillis();
        spamInterval = Random.mid(10000, 17000);
        if(Random.nextInt(0, 100) < 10)
            spamInterval *= 3;
        return true;
    }
    public static String glitchString(String text){
        if(Random.nextInt(0, 100) < 40)
            return text;
        return swapCharacters(text);
    }
    public static String swapCharacters(String text){
        int index = Random.nextInt(1, text.length() - 2);
        char c1 = text.charAt(index);
        char c2 = text.charAt(index + 1);
        return text.substring(0, index) + c1 + c2 + text.substring(index + 2);
    }

    public static void SendText(String text){
        Game.getClient().fireScriptEvent(96, text, 0);
//        Keyboard.sendText(text);
//        Keyboard.pressEnter();
    }


    String tradeRequestedBy;
    @Override
    public void notify(ChatMessageEvent msg) {
        String text = msg.getMessage();
        if(text.contains("Accepted trade") || text.contains("Other player is busy") || text.contains("Other player declined")) {
            tradeAction = null;
        }
        else if (msg.getType().equals(ChatMessageType.TRADE)) {
            tradeRequestedBy = msg.getSource();
        }
    }

}
