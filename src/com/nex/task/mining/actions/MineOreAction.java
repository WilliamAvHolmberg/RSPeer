package com.nex.task.mining.actions;

import java.util.List;
import java.util.function.Predicate;

import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.event.listeners.ObjectSpawnListener;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.ui.Log;

import com.nex.task.action.Action;

public class MineOreAction extends Action implements ObjectSpawnListener{

	private static final String ACTION = "Mine";
	public static MineOreAction instance = new MineOreAction();
	public static SceneObject currentOre;
	

	public static MineOreAction get() {
		return instance;
	}
	public static void execute(Area actionArea, List<Integer> rocks) {
		if(actionArea.contains(Players.getLocal())) {
			interactWithOre(actionArea, rocks);
		}else {
			WalkTo.execute(actionArea.getCenter());
		}
	}
	private static void interactWithOre(Area actionArea, List<Integer> rocks) {
		if(currentOre == null || shouldMine()) {
			currentOre = SceneObjects.getNearest(getAvailableTrees(actionArea, rocks));
			if(currentOre != null) {
				currentOre.interact(ACTION);
			}	
		}
	}
	
	private static boolean shouldMine() {
		Player local = Players.getLocal();
		return !local.isMoving() && !local.isAnimating();
	}
	
	private static Predicate<SceneObject> getAvailableTrees(Area actionArea, List<Integer> rocks){
		return rock -> actionArea.contains(rock) && rocks.contains(rock.getId());
    };


	
	@Override
	public void notify(ObjectSpawnEvent e) {
		if(currentOre != null && e.getPosition().equals(currentOre.getPosition())) {
			currentOre = null;
		}
		
	}

}
