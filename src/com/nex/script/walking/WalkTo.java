package com.nex.script.walking;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import com.nex.script.walking.WalkTo;
import com.nex.task.quests.GoblinDiplomacyQuest;

import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;
public class WalkTo {
	
	public static void execute(Area area) {
		execute(area.getCenter());
	}
	
	public static boolean execute(Position position) {
		position = position.randomize(2);
		//idiot special exception. get rid asap
		SceneObject goblinDoor = SceneObjects.getNearest(p -> GoblinDiplomacyQuest.goblinPos.distance(Players.getLocal()) < 30 && p.containsAction("Open") && p.getName().contains("Large door"));
		if(goblinDoor != null) {
			goblinDoor.interact("Open");
			Time.sleepUntil(() ->SceneObjects.getNearest(p ->  p.containsAction("Open") && p.getName().contains("Large door")) == null, 4000);
		}
		if(Movement.getRunEnergy() > 10 && !Movement.isRunEnabled()) {
			Movement.toggleRun(true);
			Time.sleepUntil(() -> Movement.isRunEnabled(), 5000);
		}
		if(Movement.buildPath(position) != null) {
			return Movement.walkTo(position);		
		}
		return false;
	}

	public static void execute(SceneObject object) {
		execute(object.getPosition());	
	}

}
