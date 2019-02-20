package com.nex.task.action;

import java.util.Arrays;
import java.util.List;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import org.rspeer.ui.Log;

public abstract class QuestAction extends Action{

	 public static boolean nameIsEntered(String name) {
	    	return Interfaces.firstByText(p -> p != null && p.contains(name)) != null;
		}

		public static boolean interactIfNotVisible(String visible, String action) {
	    	return !isVisible(visible) && interactAction(action);
	    }
		public static boolean interactIfVisible(String visible, String action) {
	    	return isVisible(visible) && interactAction(action);
	    }
	    
		public static boolean isVisible(String contains) {
	    	return Interfaces.getFirst(p -> p.getText().contains(contains) && p.isVisible()) != null;

	    }
		
		public static boolean isVisible(int id) {
	    	return Interfaces.getFirst(p -> p.getId() == id && p.isVisible()) != null;

	    }
		
		public static boolean isVisible(int i, int j) {
	    	InterfaceComponent comp = Interfaces.lookup(new InterfaceAddress(i,j));

			return comp != null && comp.isVisible();
		}
		public static boolean interactAction(String text) {
	    	InterfaceComponent comp = Interfaces.getFirst(p -> p.getType() == 0 && Arrays.asList(p.getActions()).contains(text));
	    	if(comp == null) {
	    		return false;
	    	}
	    	
	
	    	
	    	if(comp.click()) {
	    		return true;
	    	}
	    	
	    	
	    	return false;
	    }
		
		public static boolean interactButton(String text) {
	    	InterfaceComponent comp = Interfaces.getFirst(p -> p.getType() != 0 && p.getToolTip().contains(text));
	    	if(comp == null) {
	    		Log.fine("button does not exist");
	    		return false;
	    	}
	    	
	 
	    	
	    	if(comp.click()) {
	    		Log.fine("click");
	    		return true;
	    	}
	    	
	    	Log.fine("no click");
	    	return false;
	    }

		public static boolean interactButton(int i, int j) {
			InterfaceComponent comp = Interfaces.lookup(new InterfaceAddress(i,j));
	    	if(comp == null) {
	    		Log.fine("button does not exist");
	    		return false;
	    	}
	    	
	 
	    	
	    	if(comp.click()) {
	    		Log.fine("click");
	    		return true;
	    	}
	    	
	    	Log.fine("no click");
	    	return false;
			
		}

		public static boolean interactButton(int i, int j, int k) {
			InterfaceComponent comp = Interfaces.lookup(new InterfaceAddress(i,j, k));
	    	if(comp == null) {
	    		Log.fine("button does not exist");
	    		return false;
	    	}
	    	
	 
	    	
	    	if(comp.click()) {
	    		Log.fine("click");
	    		return true;
	    	}
	    	
	    	Log.fine("no click");
	    	return false;
			
		}

		public static InterfaceComponent get(String[] widgetTexts) {
			List<String> list = Arrays.asList(widgetTexts);
			return Interfaces.getFirst(p -> list.contains(p.getText()));
		}
		
		
		public static InterfaceComponent get(int i) {
			return Interfaces.lookup(new InterfaceAddress(i));
		}
		public static InterfaceComponent get(int i, int j) {
			return Interfaces.lookup(new InterfaceAddress(i,j));
		}
		public static InterfaceComponent get(int i, int j, int k) {
			return Interfaces.lookup(new InterfaceAddress(i,j, k));
		}

		
		public static boolean interactInventory(String action, String name) {
			Item item = Inventory.getFirst(p->p.getName().equals(name) && !p.isNoted());
			if(item == null) {
				return false;
			}
			if(!Tabs.isOpen(Tab.INVENTORY)){
				Tabs.open(Tab.INVENTORY);
				Time.sleep(100, 200);
			}
			return item.interact(action);
		}

	
}
