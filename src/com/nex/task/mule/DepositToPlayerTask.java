package com.nex.task.mule;

import java.awt.Graphics;
import java.awt.Graphics2D;

import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Trade;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.component.tab.Inventory;
import com.nex.script.walking.WalkTo;
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



public class DepositToPlayerTask extends Mule {

	DepositItemToPlayer tradeWithMule;
	public DepositToPlayerTask(int world, int itemID, int itemAmount, int startAmount, String tradeName) {
		super(world, itemID, itemAmount, startAmount, tradeName);
		tradeWithMule = new DepositItemToPlayer(tradeName, itemID, itemAmount);
	}



	@Override
	public void notify(RenderEvent event) {
		Graphics g = event.getSource();
		g.drawString("Trade to mule: " + tradeName, 350, 15);
	}

	@Override
	public int loop(){
		if(world > 0 && Worlds.getCurrent() != world) {
			
			WorldHopper.hopTo(world);
			Time.sleepUntil(() ->Worlds.getCurrent() == world, 15000);
			Time.sleep(10000);
		}
		else if (!BuyItemHandler.getGEArea().contains(Players.getLocal())) {
			WalkTo.execute(BuyItemHandler.getGEArea().getCenter());
		} else if (!Trade.isOpen() && Inventory.getCount(true, 995) < getItemAmount()) {
			BankHandler.addBankEvent(new WithdrawItemEvent(new WithdrawItem(new RSItem("Coins", 995), getItemAmount(),getItemAmount())));
		} else if (getMule(getTradeName()) != null) {
			Log.fine("Mule is available within a distance of:" + getMule(getTradeName()).getPosition().distance(Players.getLocal()));
			return tradeWithMule.execute();
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
	
		String respond = "mule_log:" + Players.getLocal().getName() + ":" + getItemAmount() + ":" + tradeName;
		Log.fine("removing task and sending message." + respond);
		NexHelper.pushMessage(new CustomLog(respond));
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
