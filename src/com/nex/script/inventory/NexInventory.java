package com.nex.script.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;

import com.nex.handler.gear.GearItem;
import com.nex.script.items.RSItem;


public class NexInventory {
	
	private ArrayList<InventoryItem> items;
	
	public NexInventory() {
		items = new ArrayList<InventoryItem>();
	}
	
	public NexInventory addItem(InventoryItem item) {
		items.removeIf(i->i.getItem().getId() == item.getItem().getId());
		items.add(item);
		return this;
	}
	
	public NexInventory addItem(RSItem item, int amount) {
		items.removeIf(i->i.getItem().getId() == item.getId());
		items.add(new InventoryItem(amount, item, 1));
		return this;
	}
	
	public ArrayList<InventoryItem> getItems(){
		return items;
	}
	public ArrayList<Integer> getItemIds(){
		ArrayList<Integer> itemIds = new ArrayList<Integer>();
		items.forEach(item -> {
			itemIds.add(item.getItem().getId());
		});
		return itemIds;
	}
	
	public ArrayList<String> getItemNames(){
		ArrayList<String> itemNames = new ArrayList<String>();
		items.forEach(item -> {
			itemNames.add(item.getItem().getName());
		});
		return itemNames;
	}
	
	
	public static boolean inventoryOnlyContainsRequiredItems(NexInventory inventory) {
		for(Item item : Inventory.getItems()) {
			if(item != null && !inventory.getItemIds().contains(item.getId())) {
				return false;
			}
		}
		return true;
	}

	public InventoryItem find(int itemID) {
		for(InventoryItem item : items) {
			if(item.getItem().getId() == itemID) {
				return item;
			}
		}
		
		return null;
	}
	
	public NexInventory setItems(ArrayList<InventoryItem> arrayList) {
		items = arrayList;
		Collections.shuffle(items);
		return this;
	}

	public InventoryItem getItemToWithdraw() {
		if(items == null || items.isEmpty()) {
			return null;
		}
		for(InventoryItem item : items) {
			if(item != null) {
			if(Inventory.getCount(item.getItem().getName()) != item.getAmount() ) {
				return item;
			}
			}
		}
		return null;
	}
	
	public Item getItemToDeposit() {
		for(Item item :Inventory.getItems()) {
			if(items == null || !getItemIds().contains(item.getId()) || hasTooManyOfItem(item)) {
				return item;
			}
		}
		return null;
	}
	
	public boolean hasTooManyOfItem(Item item) {
		return find(item.getId()) != null && Inventory.getCount(true, item.getId()) > find(item.getId()).getAmount();
	}
}
