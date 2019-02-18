package com.nex.script.grandexchange;

import java.util.ArrayList;
import java.util.List;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.runetek.providers.RSGrandExchangeOffer.Progress;
import org.rspeer.runetek.providers.RSGrandExchangeOffer.Type;
import org.rspeer.ui.Log;

import com.nex.communication.NexHelper;
import com.nex.communication.message.request.MuleRequest;
import com.nex.script.Exchange;
import com.nex.script.Nex;
import com.nex.script.handler.TaskHandler;
import com.nex.script.items.GEItem;
import com.nex.script.items.GESellItem;
import com.nex.script.items.RSItem;
import com.nex.task.mule.WithdrawFromPlayerTask;

public class BuyItemEvent {

	private boolean checkedValueableItems = false;
	public static boolean withdrawnMoney = false;
	private GEItem item;

	public BuyItemEvent(GEItem item) {
		this.item = item;
	}

	public void execute() {
		Log.fine("Buy item event");
		Log.fine("Item id:" + item.getItemID());
		Log.fine("Price:" + Exchange.getPrice(item.getItemID()));
		Log.fine("Item: " + item.getItemName() + ":" + item.getAmount() + ":" + item.getTotalPrice());
		if (!checkedValueableItems) {
			checkValuableItems();
		} else if (Inventory.getCount(true, 995) == 0 || (!withdrawnMoney
				&&Inventory.getCount(true, 995) < item.getTotalPrice())) {
			Log.fine("lets withdraw");
			withdrawMoney();
		} else {
			if (GrandExchange.isOpen()) {
				Log.fine("lets buy");
				List<RSGrandExchangeOffer> offers = getNonEmptyOffers();
				if (offers != null && !offers.isEmpty()) {
					handleExistingOffers(offers);
				} else if (Inventory.contains(item.getItemID()) || Inventory.contains(item.getItemName())) {
					BuyItemHandler.removeItem(this);
				} else {
					buyItem();
				}
			} else {
				openGe();
			}
		}
	}

	private void openGe() {
		SceneObject booth = SceneObjects.getNearest(10061);
		if (booth != null) {
			Log.fine("booth exist");
			booth.interact("Exchange");
		}
	}

	private void buyItem() {
		if (Inventory.getCount(true, 995) < item.getTotalPrice()) {
			withdrawnMoney = false;
		} else if (GrandExchangeSetup.isOpen()) {
			int freeSlots = Inventory.getFreeSlots();
			GrandExchangeSetup.setItem(item.getItemName());
			if(!Time.sleepUntil(() -> GrandExchangeSetup.getItem() != null, 500, 1500))
				return;
			GrandExchangeSetup.setPrice(item.getItemPrice());
			if(!Time.sleepUntil(() -> GrandExchangeSetup.getPricePerItem() == item.getItemPrice(), 500, 1500))
				return;
			GrandExchangeSetup.setQuantity(item.getBuyAmount());
			if(!Time.sleepUntil(() -> GrandExchangeSetup.getQuantity() == item.getBuyAmount(), 500, 1500))
				return;
			GrandExchangeSetup.confirm();
			Time.sleepUntil(() -> Inventory.getFreeSlots() < freeSlots, 3500);

		} else {
			GrandExchange.createOffer(RSGrandExchangeOffer.Type.BUY);
			Time.sleepUntil(GrandExchangeSetup::isOpen, 500, 1500);
		}
	}

	private void handleExistingOffers(List<RSGrandExchangeOffer> offers) {
		
		for (RSGrandExchangeOffer offer : offers) {
			if (offer.getProgress() == Progress.FINISHED) {
				GrandExchange.collectAll(false);
			}else if (offer.getProgress() == Progress.IN_PROGRESS) {
				if (offer.getItemId() == item.getItemID()) {
					if(item.getItemPrice() > 5 * Exchange.getPrice(item.getItemID())) {
						//send banned message
						//in the future, send error message saying that this is bad and that we should remove item 
						Log.fine("Sleeping because we are  buying over expensive item");
						Time.sleep(60000);
						break;
					}
					item.raiseItemPrice();
					withdrawnMoney = false;
				}
				offer.abort();
			} 
		}
		Log.fine("existing offers");
	}

	private void withdrawMoney() {
		if (!Bank.isOpen()) {
			Bank.open();
		}else {
			Time.sleepUntil(()->Bank.contains(995), Random.low(1000, 3000));
			if (Inventory.getCount(true, 995) >= item.getTotalPrice()) {
				withdrawnMoney = true;
			} else if (Bank.getCount(995) + Inventory.getCount(true, 995) >= item.getTotalPrice()) {
				Bank.withdrawAll(995);
			} else {
				int amount = item.getTotalPrice() - Inventory.getCount(true, 995);
				if (amount < 20000) {
					amount = 20000;
				}
				Log.fine("AMOUNT:" + amount);
				NexHelper.pushMessage(new MuleRequest("MULE_WITHDRAW:995:" + amount));
				Time.sleepUntil(() -> TaskHandler.getCurrentTask() != null
						&& TaskHandler.getCurrentTask() instanceof WithdrawFromPlayerTask, 16000);
			}
		}
	}

	private void checkValuableItems() {
		if (Bank.isOpen()) {
			if (!Inventory.isEmpty()) {
				Bank.depositInventory();
			} else {
				for (Item item : Bank.getItems()) {
					RSItem rsItem = RSItem.getItem(item.getName(), item.getId());
					int itemValue = rsItem.getItemPrice() * Bank.getCount(item.getId());
					Log.fine(rsItem.getName() + ":  price: " + itemValue);
					if (TaskHandler.canSellItem(item) && itemValue > 5000) {
						SellItemHandler.addItem(new SellItemEvent(new GESellItem(rsItem)));
					}
				}
				checkedValueableItems = true;
			}
		} else {
			Bank.open();
		}
	}

	public List<RSGrandExchangeOffer> getNonEmptyOffers() {
		List<RSGrandExchangeOffer> offers = new ArrayList<RSGrandExchangeOffer>();
		for (RSGrandExchangeOffer offer : GrandExchange.getOffers()) {
			if (offer.getType() != Type.EMPTY) {
				offers.add(offer);
			}
		}
		return offers;

	}

}
