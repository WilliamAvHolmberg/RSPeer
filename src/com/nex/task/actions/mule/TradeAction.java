package com.nex.task.actions.mule;




import java.util.List;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.Trade;
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
			Time.sleepUntil(() -> Trade.isOpen(), 25000);
		}
		
	}
	
	public boolean firstTradeScreenIsOkay(int itemID, int itemAmount) {
		if(getAmount(true, itemID) >= itemAmount){
			Log.fine("wanted:" + itemAmount + " current:" + getAmount(true,itemID));
			return true;
		}
		
		if(getAmount(false, itemID) >= itemAmount){
			Log.fine("wanted:" + itemAmount + " current:" + getAmount(false,itemID));

			return true;
		}
		return false;
	}
	
	public void acceptFirstScreen() {
			Trade.accept();
			Time.sleepUntil(() -> Trade.isOpen(true), 7500);
	}
	
	public void addItemToTrade(int itemID, int itemAmount) {
		if(Trade.isOpen(false)) {
			if(!Interfaces.isVisible(162,44)) {
			Trade.offer(995, p -> p.contains("Offer-X"));
			}else {
				Log.fine("lets enter amount");
				Keyboard.sendText(Integer.toString(itemAmount));
				Time.sleep(3000);
				Keyboard.pressEnter();
			}
			Time.sleepUntil(() -> getAmount(true,995) >= itemAmount, 7500);
		}
	}
	
	public int getAmount(boolean myItem, int id) {
		Item[] offers = Trade.getTheirItems();
		if(myItem) {
			offers = Trade.getMyItems();
		}
		for(Item item :offers) {
			if(item.getId() == id) {
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
