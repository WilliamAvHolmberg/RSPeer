package com.nex.task.mule;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.function.BooleanSupplier;

import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.ui.Log;

import com.nex.task.actions.mule.WithdrawItemFromPlayer;

public class WithdrawFromPlayerTask extends Mule {

	protected WithdrawItemFromPlayer tradeWithSlave;
	public WithdrawFromPlayerTask(int world, int itemID, int itemAmount, int startAmount, String tradeName) {
		super(world, itemID, itemAmount, startAmount, tradeName);
		tradeWithSlave = new WithdrawItemFromPlayer(tradeName, itemID, itemAmount);
	}
	

	@Override
	public void notify(RenderEvent event) {
		Graphics g = event.getSource();
		g.drawString("Started: " + timeStarted, 250, 250);
		g.drawString("Time til new request " + getTimeLeft() , 250, 270);
	}

	public int loop() {
		if(world > 0 && Worlds.getCurrent() != world) {
			Log.fine("lets hop");
			if(Bank.isOpen()) {
				Bank.close();
			}else if(GrandExchange.isOpen()) {
				Movement.walkTo(Players.getLocal().getPosition().randomize(5));
			}else {
			WorldHopper.hopTo(world);
			Time.sleepUntil(() ->Worlds.getCurrent() == world, 15000);
			Time.sleep(10000);
			}
		}
		else if (getMule(getTradeName()) != null) {
			Log.fine("Mule is available within a distance of:"
					+ getMule(getTradeName()).getPosition().distance(Players.getLocal()));
			return tradeWithSlave.execute();
		}else {
			Log.fine("mule name is not avialable");
		}
		return 200;
	}

	@Override
	public String getLog() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void notify(ChatMessageEvent e) {
		//Log.fine(e.getMessage());
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
