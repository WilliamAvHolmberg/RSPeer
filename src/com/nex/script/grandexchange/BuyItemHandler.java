package com.nex.script.grandexchange;

import java.util.Arrays;
import java.util.Stack;

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
	public static Stack<BuyItemEvent> buyItemEvents = new Stack<BuyItemEvent>();
	
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
		buyItemEvents.add(buyItemEvent);
	}

	public static void removeItem(BuyItemEvent buyItemEvent) {
		buyItemEvents.remove(buyItemEvent);
	}

	public static BuyItemEvent getBuyItemEvent() {
		if (buyItemEvents.isEmpty()) {
			return null;
		}
		return buyItemEvents.peek();
	}

	private static boolean playerAtGrandExchange() {
		return getGEArea().contains(Players.getLocal());
	}
	
	public static Area getGEArea() {
		return Area.surrounding(BankLocation.GRAND_EXCHANGE.getPosition(),20);
	}






}
