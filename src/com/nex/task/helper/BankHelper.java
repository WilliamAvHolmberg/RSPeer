package com.nex.task.helper;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.SceneObjects;

public class BankHelper {
	
	public static boolean isAvailable() {
		SceneObject bank = SceneObjects.getNearest(p -> p.containsAction("Bank") && p.distance() < 10);
		if(bank != null) {
			return true;
		}
		
		Npc npcBank = Npcs.getNearest(p -> p.containsAction("Bank") && p.distance() < 10);
		if(npcBank != null) {
			return true;
		}
		return false;
	}

}
