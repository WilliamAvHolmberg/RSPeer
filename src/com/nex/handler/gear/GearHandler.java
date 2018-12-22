package com.nex.handler.gear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.rspeer.runetek.api.component.tab.Equipment;



public class GearHandler {

	public static Stack<GearItem> itemsToEquip = new Stack<GearItem>();

	public static int execute() {
			if (isWearing(itemsToEquip.peek())) {
				itemsToEquip.pop();
			} else {
				return EquipItem.execute(itemsToEquip.peek());
			}
		
		return 200;
	}

	private static boolean isWearing(GearItem peek) {
		return Equipment.contains(peek.getItem().getId());
	}

	/*
	 * public GearItem getPreferredGearInSlot(EquipmentSlot slot) { for(GearItem
	 * item: gear) { if(item.getSlot() == slot) { return item; } } return null; }
	 */

	public static GearItem getWithdrawItem() {
		if (itemsToEquip.isEmpty()) {
			return null;
		}
		return itemsToEquip.peek();
	}

	public static void addItem(GearItem item) {
		itemsToEquip.add(item);
	}

	public static void removeItem(GearItem item) {
		itemsToEquip.remove(item);
	}




}
