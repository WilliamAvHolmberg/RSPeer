package com.nex.script.banking;

import com.nex.communication.message.request.RequestAccountInfo;
import com.nex.script.Nex;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.ui.Log;

import com.nex.communication.NexHelper;
import com.nex.communication.message.request.MuleRequest;
import com.nex.handler.gear.GearItem;
import com.nex.script.banking.BankEvent.Type;
import com.nex.script.grandexchange.BuyItemEvent;
import com.nex.script.grandexchange.BuyItemHandler;
import com.nex.script.handler.TaskHandler;
import com.nex.script.inventory.InventoryItem;
import com.nex.script.items.GEItem;
import com.nex.script.items.RSItem;
import com.nex.script.items.RequiredItem;
import com.nex.script.items.WithdrawItem;
import com.nex.task.mule.WithdrawFromPlayerTask;

public class WithdrawItemEvent extends BankEvent {

	private int amount;
	private int buyAmount;
	private int id;
	private RequiredItem requiredItem;

	public WithdrawItemEvent(RequiredItem requiredItem) {
		this.requiredItem = requiredItem;
		this.amount = requiredItem.getAmount();
		this.id = requiredItem.getItemID();
	}
	public WithdrawItemEvent(InventoryItem item) {
		this.requiredItem = new WithdrawItem(item.getItem().getId(), item.getAmount(), item.getItem().getName(), item.getBuyAmount());
		this.amount = item.getAmount();
		this.buyAmount = item.getBuyAmount();
		Log.fine("AMOUNT WHEN ADDED: " + amount);
		Log.fine("REQ ITEM: " + requiredItem.getItemName() + ":" + requiredItem.getAmount());
		this.id = item.getItem().getId();
	}

	public WithdrawItemEvent(GearItem item) {
		this.requiredItem = new WithdrawItem(item.getItem().getId(),1,item.getItem().getName(), 1);
		this.amount = requiredItem.getAmount();
		this.id = requiredItem.getItemID();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return  false;
		if (!(obj instanceof WithdrawItemEvent)) return false;
		WithdrawItemEvent other = (WithdrawItemEvent)obj;
		if (other.buyAmount != buyAmount ||
			other.id != id ||
			other.amount != amount)
			return false;
		return true;
	}

	public void execute() {
		if (Bank.isOpen()) {
			Item notedItem = Inventory.getFirst(id+1);
			if(notedItem != null && notedItem.isNoted()) {
				Bank.deposit(notedItem.getId(), notedItem.getStackSize());
			}
			Time.sleepUntil(()->Bank.contains(id), 100, Random.low(1000, 3000));
			if (Bank.getCount(id) >= amount) {
				if(amount > 28 && id != 995 && Bank.getWithdrawMode() != Bank.WithdrawMode.NOTE){
					Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
					if (!Time.sleepUntil(()->Bank.getWithdrawMode() == Bank.WithdrawMode.NOTE, 3000))
						return;
				}
				Bank.withdraw(id, amount);
				Time.sleepUntil(() -> Inventory.contains(id), 5000);
			} else if (id == 995) {
				if(amount < Nex.MIN_WITHDRAW) {
					amount = Nex.MIN_WITHDRAW;
				}
				if("MULE".equalsIgnoreCase(RequestAccountInfo.account_type)) {
					if(amount < 100_000)
						amount = 100_000;
				}else if(amount > 30000) {
					System.exit(1);
				}
				Log.fine("We don't have enough coins, lets get from mule");
				NexHelper.pushMessage(new MuleRequest("MULE_WITHDRAW:995:" + amount));
				Time.sleepUntil(() -> TaskHandler.getCurrentTask() != null
						&& TaskHandler.getCurrentTask() instanceof WithdrawFromPlayerTask, 16000);

			} else {
				// TODO STOP SCRIPT
				Log.fine("bank does not contain" + id + " lets stop" + Bank.getCount(id));
				BuyItemHandler.addItem(new BuyItemEvent(new GEItem(requiredItem)));

			}
		}else {
			Bank.open();
		}
	}

	@Override
	public Type getBankType() {
		return Type.WITHDRAW;
	}

	public RequiredItem getRequiredItem() {
		return requiredItem;
	}

	@Override
	public boolean isFinished() {
		if (Inventory.getCount(true, id) >= amount) {
			return true;
		}
		return false;
	}

}
