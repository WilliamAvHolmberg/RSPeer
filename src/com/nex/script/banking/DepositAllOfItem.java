package com.nex.script.banking;

import org.rspeer.runetek.api.commons.Time;
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
import com.nex.script.items.RequiredItem;
import com.nex.script.items.WithdrawItem;
import com.nex.task.mule.WithdrawFromPlayerTask;

public class DepositAllOfItem extends BankEvent {

	
	private int id;

	public DepositAllOfItem(int itemID) {
		this.id = itemID;
	}



	public void execute() {
		if (Bank.isOpen()) {
			Bank.deposit(id, Inventory.getCount(true, id));
			Time.sleepUntil(()-> !Inventory.contains(id), 5000);
		}else {
			Bank.open();
		}
	}

	@Override
	public Type getBankType() {
		return Type.DEPOSIT;
	}

	@Override
	public boolean isFinished() {
		if (!Inventory.contains(id)) {
			return true;
		}
		return false;
	}

}
