package com.nex.task.combat.actions;

import java.util.ArrayList;

import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

import com.nex.script.Exchange;
import com.nex.script.walking.WalkTo;
import com.nex.task.tracker.LootTracker;

public class LootAction{

	private Area lootArea;
	private int threshold;
	private LootTracker lootTracker;
	
	public LootAction(Area lootArea, int threshold, LootTracker lootTracker) {
		this.lootArea = lootArea;
		this.threshold = threshold;
		this.lootTracker = lootTracker;
	}

	public int execute() {
		Pickable pickable = getValuableDrop();
		if(pickable == null) {
			Log.fine("Loot is not available");
		}else if(!pickable.getPosition().isPositionInteractable() || pickable.getPosition().distance(Players.getLocal()) > 15) {
			Log.fine("Cannot reach loot. Lets do obstacle.");
			WalkTo.execute(pickable.getPosition());
		}else {
			Log.fine("Loot is available");
			int preInventoryCount = Inventory.getCount(true, pickable.getId());
			pickable.interact("Take");
			Time.sleepUntil(()-> getCount(pickable) > preInventoryCount, 10000); 	//do not break the sleep until we successfully ate the food.
			int lootCount = getCount(pickable) - preInventoryCount;
			if(lootCount > 0) {
				int moneyGained = Exchange.getPrice(pickable.getId()) * lootCount;
				Log.fine("LOOTED: " + moneyGained);
				lootTracker.addGained(moneyGained);
			}
		}
		return 300;
	}
	
	public int getCount(Pickable pickable) {
		return Inventory.getCount(true, pickable.getId());
	}
	
	public boolean shouldLoot() {
		return getValuableDrop() != null;
	}
	
	public ArrayList<Pickable> getValuableDrops() {
		ArrayList<Pickable> list = new ArrayList<Pickable>();
		for (Pickable item : Pickables.getLoaded()) {
			int price = Exchange.getPrice(item.getId());
			if (item.isPositionInteractable() && price * item.getStackSize() > threshold) {
				list.add(item);
			}
		}
		if(list.isEmpty()) {
			return null;
		}
		return list;
	}
	
	public Pickable getValuableDrop() {
		for (Pickable item : Pickables.getLoaded()) {
			int price = Exchange.getPrice(item.getId());
			if (lootArea.contains(item) && item.isPositionWalkable() && price * item.getStackSize() > threshold) {
				return item;
			}
		}
		return null;
	}
	
}