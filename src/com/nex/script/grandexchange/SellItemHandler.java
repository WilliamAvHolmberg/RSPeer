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



public class SellItemHandler {
	public static Deque<SellItemEvent> sellItemEvents = new ArrayDeque<>();
	//A dequeue offers items in a 3,2,1,0 when enumerated

	public static void execute(SellItemEvent sellItemEvent) {
		if(playerAtGrandExchange()) {
			sellItemEvent.execute();
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

	public static void addItem(SellItemEvent sellItemEvent) {
		if(!sellItemEvents.contains(sellItemEvent)) {
			sellItemEvents.push(sellItemEvent);
			TaskHandler.addHandler(sellItemEvent);
		}
	}

	public static void removeItem(SellItemEvent sellItemEvent) {
		sellItemEvents.remove(sellItemEvent);
		TaskHandler.removeHandler(sellItemEvent);
	}

	public static SellItemEvent getSellItemEvent() {
		for (SellItemEvent item : sellItemEvents) {
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
