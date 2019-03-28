 package com.nex.task.actions.mule;

import com.nex.communication.message.request.RequestAccountInfo;
import com.nex.script.Exchange;
import org.rspeer.runetek.api.component.Trade;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.ui.Log;

public class DepositItemToPlayer extends TradeAction{

	public DepositItemToPlayer(String playerToTrade, int itemID, int itemAmount) {
		super(playerToTrade, itemID, itemAmount);

		if(itemID == 995 && "MASTER_MULE".equalsIgnoreCase(RequestAccountInfo.account_type)) {
			int coins = Inventory.getCount(true, 995);
			if(coins > 10_000_000) this.itemAmount = 500_000;
			else if(coins > 1_000_000) this.itemAmount = 200_000;
			else if(coins > 500_000) this.itemAmount = 100_000;
		}
	}

	public int execute() {
		if(!Inventory.contains(itemID)) {//If the item is noted, the ID changes. 
			String name = Exchange.getName(itemID); 
			if(Inventory.contains(name))//Lets see if we have something with the right name
				itemID = Inventory.getFirst(name).getId();
		}
		if (!Trade.isOpen()) {
			initiateTrade(playerToTrade);
		} else if (Trade.isOpen(true)) {
			acceptFirstScreen();
		} else if (!firstTradeScreenIsOkay( itemID, itemAmount)) {
			Log.fine("lets add item");
			Log.fine("item id:" + itemID);
			addItemToTrade( itemID, itemAmount);
		} else if (Trade.isOpen(false)) {
			acceptFirstScreen();
		}
		return 200;
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Trade with Mule";
	}



}
