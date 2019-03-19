package com.nex.task.actions.mule;




import java.util.List;

import com.nex.script.Exchange;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.Trade;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

import com.nex.task.action.Action;


public abstract class TradeAction extends Action{

	protected String playerToTrade;
	protected int itemID;
	protected int itemAmount;
	
	public TradeAction(String playerToTrade, int itemID, int itemAmount) {
		this.playerToTrade = playerToTrade;
		this.itemID = itemID;
		this.itemAmount = itemAmount;
	}

	public void initiateTrade(String name) {
		Player target = getPlayer(name);
		if(target != null) {
			target.interact("Trade with");
			Time.sleepUntil(() -> Trade.isOpen(), 100, 25000);
		}
		
	}
	
	public boolean firstTradeScreenIsOkay(int itemID, int itemAmount) {
		String name = Exchange.getName(itemID);
		if(getAmount(true, name, itemID) >= itemAmount){
			Log.fine("wanted:" + itemAmount + " current:" + getAmount(true, name, itemID));
			return true;
		}

		if(getAmount(false, name, itemID) >= itemAmount){
			Log.fine("wanted:" + itemAmount + " current:" + getAmount(false, name, itemID));

			return true;
		}
		return false;
	}
	
	public void acceptFirstScreen() {
		Trade.accept();
		Time.sleepUntil(() -> Trade.isOpen(true), 100, 7500);
	}
	
	public void addItemToTrade(int itemID, int itemAmount) {
		if(Trade.isOpen(false)) {
			int count = Inventory.getCount(true, itemID);
			if (count <= itemAmount) {
				Trade.offerAll(itemID);
			} else {
				if (!Interfaces.isVisible(162, 44)) {
					Log.fine(itemID + " Offer-X: " + itemAmount);
					Trade.offer(itemID, p -> p.contains("Offer-X"));
				}
				if(Time.sleepUntil(()->Interfaces.isVisible(162, 44), 200, 3000)) {
					Log.fine("lets enter amount");
					Keyboard.sendText(Integer.toString(itemAmount));
					Time.sleep(800, 2000);
					Keyboard.pressEnter();
				}
			}
			Time.sleepUntil(() -> getAmount(true, Exchange.getName(itemID), itemID) >= Math.min(count, itemAmount), 200, 7500);
		}
	}

	public int getAmount(boolean myItem, String name, int id) {
		Item[] offers = Trade.getTheirItems();
		if(myItem) {
			offers = Trade.getMyItems();
		}
		for(Item item :offers) {
			Log.fine("Looking for " + name + " - found " + item.getName());
			if(item.getId() == id || item.getName().equalsIgnoreCase(name)) {
				Log.fine(item.getStackSize());
				return item.getStackSize();
			}
		}
		return 0;
	}
	
	
	public Player getPlayer(String name) {
		for(Player player : Players.getLoaded()) {
			if(player.getName().toLowerCase().equals(name.toLowerCase())) {
				return player;
			}
		}
		return null;
	}




}
