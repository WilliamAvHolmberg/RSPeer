package com.nex.script.banking;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.ui.Log;

public class DepositAll extends BankEvent{

	public void execute() {
		if(Bank.isOpen()) {
			Bank.depositInventory();
			Time.sleepUntil(()-> Inventory.isEmpty(), 5000);
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
		return Inventory.isEmpty();
	}



}
