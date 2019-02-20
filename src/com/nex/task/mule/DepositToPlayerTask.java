package com.nex.task.mule;

import java.awt.Graphics;
import java.awt.Graphics2D;

import com.nex.communication.message.respond.TannerRespond;
import com.nex.script.Exchange;
import com.nex.script.items.GEItem;
import com.nex.script.items.RequiredItem;
import com.nex.task.actions.mule.CheckIfWeShallSellItems;
import com.nex.task.tanning.TanningTask;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.Trade;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;

import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.ui.Log;

import com.nex.communication.NexHelper;
import com.nex.communication.message.CustomLog;
import com.nex.script.Nex;
import com.nex.script.banking.BankHandler;
import com.nex.script.banking.WithdrawItemEvent;
import com.nex.script.grandexchange.BuyItemHandler;
import com.nex.script.handler.TaskHandler;
import com.nex.script.items.RSItem;
import com.nex.script.items.WithdrawItem;
import com.nex.script.walking.WalkTo;
import com.nex.task.actions.mule.DepositItemToPlayer;
import com.nex.task.actions.mule.WithdrawItemFromPlayer;

import javax.swing.text.Position;


public class DepositToPlayerTask extends Mule {

	DepositItemToPlayer tradeWithMule;
	public DepositToPlayerTask(int world, int itemID, int itemAmount, String tradeName, Area playerPos) {
		super(world, itemID, itemAmount, tradeName, playerPos);
		tradeWithMule = new DepositItemToPlayer(tradeName, itemID, itemAmount);
	}

	@Override
	public void notify(RenderEvent event) {
		Graphics g = event.getSource();
		g.drawString("Trade to mule: " + tradeName, 350, 15);
	}

	@Override
	public int loop(){

		String name = Exchange.getName(itemID);

		int coinsToGive = getItemAmount();
		if(itemID != 995){//Not coins
			coinsToGive = 0;

			CheckIfWeShallSellItems.dontSell = true;
			int qtyOfLoadsToBuy = 10;
			int count = Inventory.getCount(true, name);
			boolean doWeNeedToBuyMoreItems = !Trade.isOpen() && count < getItemAmount();
			Log.fine("We have " + count + " of " + getItemAmount() + " " + name);

			int numToBuy = getItemAmount();
			//Make sure we have enough money to buy hides AND give to the players
			if(TanningTask.ANY_HIDE_NAME.test(name)) {
				final int pocketMoneyEa = 1;
				coinsToGive = numToBuy * pocketMoneyEa;
				if(doWeNeedToBuyMoreItems)
					numToBuy *= qtyOfLoadsToBuy;
				if(doWeNeedToBuyMoreItems) {
					WithdrawItem withdrawal = new WithdrawItem(new RSItem(name, itemID), numToBuy, numToBuy);
					GEItem geItem = new GEItem(withdrawal);
					int moneyNeeded = geItem.getTotalPrice();
					moneyNeeded -= Inventory.getCount(true, 995);
					if (!CheckIfWeShallSellItems.untradeableItems.contains(name))//We dont want to sell what we already have. Would waste money
						CheckIfWeShallSellItems.untradeableItems.add(name);
					if(this.requiredItems.isEmpty()) {
						this.requiredItems.add(new RSItem(name, itemID));
						this.requiredItems.add(new RSItem(name, itemID + 1));
					}
					if (moneyNeeded > 0) {
						Log.fine("Need " + moneyNeeded + " money. Withdrawing from mule");
						BankHandler.addBankEvent(new WithdrawItemEvent(new WithdrawItem(new RSItem("Coins", 995), moneyNeeded, moneyNeeded)));
						return 200;
					}
				}
			}

			//Do we have enough of the item?
			if (doWeNeedToBuyMoreItems) {
				numToBuy = getItemAmount() * qtyOfLoadsToBuy;
				Log.fine("We need to buy " + numToBuy);
				if (!BuyItemHandler.getGEArea().contains(Players.getLocal())) {
					WalkTo.execute(BuyItemHandler.getGEArea().getCenter());
					return 200;
				}
				BankHandler.addBankEvent(new WithdrawItemEvent(new WithdrawItem(new RSItem(name, itemID), numToBuy, numToBuy)));
				return 200;
			}

			if(coinsToGive < Inventory.getCount(true, 995)){
				BankHandler.addBankEvent(new WithdrawItemEvent(new WithdrawItem(new RSItem("Coins", 995), coinsToGive, coinsToGive)));
				return 200;
			}
		}

		if (this.playerPos != null && this.playerPos.getCenter().distance() > 10) {
			Log.fine("Walking to player...");
			WalkTo.execute(this.playerPos.getCenter());
		}
		else if(world > 0 && Worlds.getCurrent() != world) {

			if(Bank.isOpen()) {
				Bank.close();
			}else if(GrandExchange.isOpen()) {
				Movement.walkTo(Players.getLocal().getPosition().randomize(5));
			}else {
				WorldHopper.hopTo(world);
				Time.sleepUntil(() ->Worlds.getCurrent() == world, 15000);
				Time.sleep(10000);
			}
			return 200;
		} else if (!Trade.isOpen() && Inventory.getCount(true, name) < getItemAmount()) {
			Log.fine("Withdrawing " + name + " from bank...");
			BankHandler.addBankEvent(new WithdrawItemEvent(new WithdrawItem(new RSItem(name, itemID), getItemAmount(), getItemAmount())));
		} else if (getMule(getTradeName()) != null) {
			Log.fine("Mule is available within a distance of:" + getMule(getTradeName()).getPosition().distance(Players.getLocal()));
			return tradeWithMule.execute();
		}else{
			Log.fine("Looking out for " + getTradeName() + "...");
		}
		return 200;
	}

	@Override
	public String getLog() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void removeTask() {
		// TODO send mule done with info

		if(tradeIsCompleted) {
			String respond = "mule_log:" + Players.getLocal().getName() + ":" + getItemAmount() + ":" + tradeName;
			Log.fine("removing task and sending message." + respond);
			NexHelper.pushMessage(new CustomLog(respond));
		}
	}
	@Override
	public void notify(ChatMessageEvent e) {
		if(e.getMessage().contains("Accepted trade")) {
			Log.fine("ACCEPTED TRADDDDDDDDDDDDDDDDDDDDE");
			tradeIsCompleted = true;
		}
		
	}
	@Override
	public void notify(ObjectSpawnEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	






}
