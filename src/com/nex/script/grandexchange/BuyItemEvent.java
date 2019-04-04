package com.nex.script.grandexchange;

import java.util.ArrayList;
import java.util.List;

import com.nex.communication.message.request.RequestAccountInfo;
import com.nex.script.Quest;
import com.nex.script.inventory.InventoryItem;
import com.nex.script.items.DepositItem;
import com.nex.task.IHandlerTask;
import com.nex.task.actions.mule.CheckIfWeShallSellItems;
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

public class BuyItemEvent implements IHandlerTask {

	private boolean checkedValueableItems = false;
	public static boolean withdrawnMoney = false;
	private GEItem item;

	boolean finished = false;
	@Override
	public boolean isFinished(){
		return finished;
	}
	protected long timeStarted = System.currentTimeMillis();
	public long getTimeStarted() {
		return timeStarted;
	}

	public BuyItemEvent(GEItem item) {
		this.item = item;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return  false;
		if (!(obj instanceof BuyItemEvent)) return false;
		BuyItemEvent other = (BuyItemEvent)obj;
		return other.item.equals(this.item);
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
				} else if (Inventory.getCount(true, item.getItemID()) >= item.getBuyAmount() || Inventory.getCount(true, item.getItemName()) >= item.getBuyAmount()) {
					exchangeIfNoted(item);
					finished = true;
					BuyItemHandler.removeItem(this);
				} else {
					buyItem();
				}
			} else {
				openGe();
			}
		}
	}

	void exchangeIfNoted(GEItem item){
		Item invItem = Inventory.getFirst(item.getItemID());
		if (invItem == null) invItem = Inventory.getFirst(item.getItemName());
		if (invItem.isNoted() && invItem.getStackSize() < (28 - (Inventory.getCount() - 1))){
			Bank.open();
			if(Time.sleepUntil(()->Bank.isOpen(), 200, 2000))
			{
				int count = invItem.getStackSize();
				String name = invItem.getName();
				Bank.depositAll(invItem.getId());
				Time.sleepUntil(()->Bank.getCount(name) > count, 200, 2000);
				Bank.withdraw(invItem.getName(), count);
				Time.sleepUntil(()->Inventory.getCount(name) > count, 200, 2000);
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
		Log.fine(item.getTotalPrice());
		if (Inventory.getCount(true, 995) < item.getTotalPrice()) {
			Log.fine(item.getTotalPrice());
			withdrawnMoney = false;
		} else if (GrandExchangeSetup.isOpen()) {
			int freeSlots = Inventory.getFreeSlots();
			if(item.getItemName() != null)
				GrandExchangeSetup.setItem(item.getItemName());
			else
				GrandExchangeSetup.setItem(item.getItemID());
			if(!Time.sleepUntil(() -> GrandExchangeSetup.getItem() != null, 800, 1500))
				return;
			GrandExchangeSetup.setPrice(item.getItemPrice());
			if(!Time.sleepUntil(() -> GrandExchangeSetup.getPricePerItem() == item.getItemPrice(), 800, 1500))
				return;
			if(GrandExchangeSetup.getQuantity() != item.getBuyAmount()) {
				GrandExchangeSetup.setQuantity(item.getBuyAmount());
				if (!Time.sleepUntil(() -> GrandExchangeSetup.getQuantity() == item.getBuyAmount(), 800, 1500))
					return;
			}
			GrandExchangeSetup.confirm();
			Time.sleepUntil(() -> Inventory.getFreeSlots() < freeSlots, 3500);

		} else {
			GrandExchange.createOffer(RSGrandExchangeOffer.Type.BUY);
			Time.sleepUntil(GrandExchangeSetup::isOpen, 800, 1500);
		}
	}

	long lastRaisedPrice = 0;
	private void handleExistingOffers(List<RSGrandExchangeOffer> offers) {
		int waitTime = Math.min(8, item.getTimesRaised() + 2) * 1000;
		if(offers.stream().anyMatch(offer-> offer.getProgress() == Progress.IN_PROGRESS)) {
			Time.sleepUntil(()-> GrandExchange.getOffers(offer->offer.getProgress() == Progress.FINISHED).length > 0, 1000, waitTime);
			offers = getNonEmptyOffers();
		}
		for (RSGrandExchangeOffer offer : offers) {
			if (offer.getProgress() == Progress.FINISHED) {
				GrandExchange.collectAll(false);
			}else if (offer.getProgress() == Progress.IN_PROGRESS) {
				if (offer.getItemId() == item.getItemID()) {
					if(item.getItemPrice() > 5 * item.getOriginalItemPrice()) {
						if(System.currentTimeMillis() - lastRaisedPrice < 60_000) {
							Time.sleep(500);
							return;
						}
//						//send banned message
//						//in the future, send error message saying that this is bad and that we should remove item
//						Log.fine("Sleeping because we are  buying over expensive item");
//						Time.sleep(60000);
//						break;
					}
					lastRaisedPrice = System.currentTimeMillis();
					item.raiseItemPrice();
					withdrawnMoney = false;
					offer.abort();
				}
			} 
		}
		Log.fine("existing offers");
	}

	static int withdrawAmount = Random.nextInt(12, 15) * 1000;
	private void withdrawMoney() {
		if (!Bank.isOpen()) {
			Bank.open();
		}else {
			Time.sleepUntil(()->Bank.contains(995), Random.low(1000, 3000));
			Log.fine("Withdrawing money to buy " + item.getBuyAmount() + " x " + item.getItemName());
			if (Inventory.getCount(true, 995) >= item.getTotalPrice()) {
				withdrawnMoney = true;
			} else if (Bank.getCount(995) + Inventory.getCount(true, 995) >= item.getTotalPrice()) {
				Bank.withdrawAll(995);
			} else {
				int amount = item.getTotalPrice() - Inventory.getCount(true, 995);
				if (amount < withdrawAmount) {
					amount = withdrawAmount;
				}
				if(RequestAccountInfo.account_type == "MULE" && amount < 80000)
					amount = 50000;

				Log.fine("Not enough money. Requesting " + amount);
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
					if (itemValue > 3000 && TaskHandler.canSellItem(item) && (Quest.getQuestPoints() >= 7 || !CheckIfWeShallSellItems.untradeableItems.contains(item.getName()))) {
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
