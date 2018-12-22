package com.nex.task.helper;



import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;

public class AreaHelper {
	
	public static boolean inArea(Area area) {
		return area.contains(Players.getLocal());
	}

}
