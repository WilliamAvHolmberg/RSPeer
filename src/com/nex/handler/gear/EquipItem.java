package com.nex.handler.gear;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.ui.Log;

import com.nex.script.banking.BankHandler;
import com.nex.script.banking.WithdrawItemEvent;
import com.nex.task.action.QuestAction;

public class EquipItem {


	



	public static int execute(GearItem item) {
		Log.fine("Equip");
		if(Inventory.contains(item.getItem().getId())) {
			close();
			Log.fine("lets equip");
			QuestAction.interactInventory(Inventory.getFirst(item.getItem().getId()).getActions()[0],
					item.getItem().getName());
			Time.sleepUntil(() -> Equipment.contains( item.getItem().getId()), 3000);
			
		}else {
			BankHandler.addBankEvent(new WithdrawItemEvent(item));
		}
		return 200;
	}
	
	public static boolean close() {
		InterfaceComponent[] comps = Interfaces.get(p-> p.isVisible() && p.containsAction("Close"));
		if(comps == null) {
			return true;
		}else {
		for(InterfaceComponent comp : comps) {
			Log.fine("exists");
			if(comp.interact("Close")) {
				Time.sleepUntil(() -> !comp.isVisible(), 2000);
			}
		}}
		return false;
	}

	

}
