package com.nex.handler.gear;

import java.util.ArrayList;
import java.util.HashMap;

import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.EquipmentSlot;

import com.nex.script.items.RSItem;



public class Gear {
	
	public HashMap<EquipmentSlot, GearItem> gear;
	
	public Gear() {
		this.gear = new HashMap<EquipmentSlot, GearItem>();
		for(EquipmentSlot slot : EquipmentSlot.values()) {
			gear.putIfAbsent(slot, null);
		}
	}
	
	public void addGear(EquipmentSlot slot, GearItem equipment) {	
		gear.put(slot, equipment);
	}
	
	public void addGear(EquipmentSlot slot, RSItem equipment) {	
		gear.put(slot, new GearItem(slot,equipment));
	}

	public GearItem getGearInSlot(EquipmentSlot slot) {
		return gear.get(slot);
	}
	
	public HashMap<EquipmentSlot, GearItem> getGear(){
		return gear;
	}
	
	public GearItem getItemToEquip() {
		if(gear == null || gear.isEmpty()) {
			return null;
		}
		for(GearItem item : gear.values()) {
			if(item != null) {
			if(!Equipment.contains(item.getItem().getId())) {
				return item;
			}
			}
		}
		return null;
	}

}
