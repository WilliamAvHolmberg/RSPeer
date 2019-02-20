package com.nex.script.grandexchange;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Stack;

import com.nex.script.handler.TaskHandler;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;

import com.nex.script.items.GEItem;
import com.nex.script.items.WithdrawItem;
import com.nex.script.walking.WalkTo;



public class BuyItemHandler {
	public static Deque<BuyItemEvent> buyItemEvents = new ArrayDeque<>();
	//A dequeue offers items in a 3,2,1,0 when enumerated

	public static void execute(BuyItemEvent buyItemEvent) {
		if(playerAtGrandExchange()) {
			buyItemEvent.execute();
		}else {
			WalkTo.execute(getGEArea().getCenter());
		}
	}

	private static boolean purchaseAmountIsWrong(GEItem geItem, WithdrawItem withdrawItem) {
		return Bank.isOpen() && (withdrawItem.getAmount() - Bank.getCount(geItem.getItemID())) != geItem.getAmount();
	}

	private static boolean purchaseIsCompleted(GEItem geItem, WithdrawItem withdrawItem) {
		return Bank.isOpen() && Bank.getCount(geItem.getItemID()) >= withdrawItem.getAmount()
				|| Inventory.getCount(geItem.getItemName()) >= withdrawItem.getAmount();
	}

	public static void addItem(BuyItemEvent buyItemEvent) {
		if(!buyItemEvents.contains(buyItemEvent)) {
			buyItemEvents.push(buyItemEvent);
			TaskHandler.addHandler(buyItemEvent);
		}
	}

	public static void removeItem(BuyItemEvent buyItemEvent) {
		buyItemEvents.remove(buyItemEvent);
		TaskHandler.removeHandler(buyItemEvent);
	}

	public static BuyItemEvent getBuyItemEvent() {
		for (BuyItemEvent item : buyItemEvents) {
			return item;
		}
		return null;
	}

	private static boolean playerAtGrandExchange() {
		return getGEArea().contains(Players.getLocal());
	}
	
	public static Area getGEArea() {
		return Area.surrounding(BankLocation.GRAND_EXCHANGE.getPosition(),20);
	}






}
