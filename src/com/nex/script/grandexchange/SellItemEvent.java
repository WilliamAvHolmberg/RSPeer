package com.nex.script.grandexchange;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.nex.script.banking.WithdrawItemEvent;
import com.nex.task.IHandlerTask;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Bank.WithdrawMode;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchange.View;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.runetek.providers.RSGrandExchangeOffer.Progress;
import org.rspeer.runetek.providers.RSGrandExchangeOffer.Type;
import org.rspeer.ui.Log;

import com.nex.script.items.GEItem;
import com.nex.script.items.GESellItem;
import com.nex.script.items.RSItem;

public class SellItemEvent implements IHandlerTask {

	private GESellItem item;
	
	public SellItemEvent(GESellItem item) {
		this.item = item;
	}

	boolean finished = false;
	@Override
	public boolean isFinished() {
		return finished;
	}
	protected long timeStarted = System.currentTimeMillis();
	public long getTimeStarted() {
		return timeStarted;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return  false;
		if (!(obj instanceof SellItemEvent)) return false;
		SellItemEvent other = (SellItemEvent)obj;
		return item.equals(other.item);
	}

	public void execute() {
		if(!item.hasBeenWithdrawnFromBank()) {
			if(!Bank.isOpen()) {
				Bank.open();
			}else if(Bank.getCount(item.getItemID()) > 0) {
				withdrawItem();
			}else {
				item.setHasBeenWithdrawn(true);
			}
		}else {
			if(GrandExchange.isOpen()) {
				Log.fine("lets sell");
				List<RSGrandExchangeOffer> offers = getNonEmptyOffers();
				if(offers != null && !offers.isEmpty()) {
					handleExistingOffers(offers);
				}else if(Inventory.isEmpty()) {
					finished = true;
					SellItemHandler.removeItem(this);
				}else {
					sellItem(); 
				}
			}else {
				SceneObject booth = SceneObjects.getNearest(10061);
				if(booth != null) {
					Log.fine("booth exist");
					booth.interact("Exchange");
				}
			}
		}

	}
	private void sellItem() {
		if(GrandExchangeSetup.isOpen()) {
			GrandExchangeSetup.setItem(getID(item));
			Time.sleepUntil(() ->GrandExchangeSetup.getItem() != null, 500, 1500);
	        GrandExchangeSetup.setPrice(1);
			Time.sleepUntil(() ->GrandExchangeSetup.getPricePerItem() == 1, 500, 1500);
			GrandExchangeSetup.confirm();
		}else {
			GrandExchange.createOffer(RSGrandExchangeOffer.Type.SELL);
			Time.sleepUntil(GrandExchangeSetup::isOpen, 500, 1500);
		}
	}

	private void handleExistingOffers(List<RSGrandExchangeOffer> offers) {
		for(RSGrandExchangeOffer offer :offers) {
			if(offer.getProgress() == Progress.IN_PROGRESS) {
				offer.abort();
			}else if(offer.getProgress() == Progress.FINISHED) {
				GrandExchange.collectAll(true);
			}
		}
		Log.fine("existing offers");
	}

	private void withdrawItem() {
		if(Bank.getWithdrawMode() == WithdrawMode.NOTE) {
			Bank.withdrawAll(item.getItemID());
			Time.sleepUntil(() ->!Bank.contains(item.getItemID()), 500, 1500);
		}else {
			Bank.setWithdrawMode(WithdrawMode.NOTE);
			Time.sleepUntil(() ->Bank.getWithdrawMode() == WithdrawMode.NOTE, 500, 1500);
		}
	}

	private int getID(GESellItem item) {
		return Inventory.getFirst(item.getItemName()).getId();
	}
	public List<RSGrandExchangeOffer> getNonEmptyOffers(){
		List<RSGrandExchangeOffer> offers = new ArrayList<RSGrandExchangeOffer>();
		for(RSGrandExchangeOffer offer :GrandExchange.getOffers()) {
			if(offer.getType() != Type.EMPTY) {
				offers.add(offer);
			}
		}
		return offers;
		
	}
	
}
