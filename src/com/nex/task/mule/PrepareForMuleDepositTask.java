package com.nex.task.mule;

import java.awt.Graphics;
import java.awt.Graphics2D;


import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;

import com.nex.script.grandexchange.BuyItemHandler;
import com.nex.script.walking.WalkTo;
import com.nex.task.actions.mule.PrepareForMuleDeposit;

public class PrepareForMuleDepositTask extends Mule {
	private PrepareForMuleDeposit prepareForMuleDepositNode = new PrepareForMuleDeposit();
	public PrepareForMuleDepositTask() {
		super(0, 0, 0, 0, null);
		this.setBreakAfterTime(10);
	}
	



	@Override
	public void notify(RenderEvent e) {
		Graphics g = e.getSource();
		g.drawString("Preparing for mule deposit: ", 350,150);
	}



	@Override
	public int loop(){
		if(!BuyItemHandler.getGEArea().contains(Players.getLocal())) {
			WalkTo.execute(BuyItemHandler.getGEArea().getCenter());
			return 100;
		}else {
		return prepareForMuleDepositNode.execute();
		}
	}


	@Override
	public void notify(ChatMessageEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void notify(ObjectSpawnEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	





}
