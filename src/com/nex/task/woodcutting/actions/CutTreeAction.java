package com.nex.task.woodcutting.actions;

import java.util.*;
import java.util.function.Predicate;

import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.event.listeners.AnimationListener;
import org.rspeer.runetek.event.listeners.ObjectSpawnListener;
import org.rspeer.runetek.event.types.AnimationEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.ui.Log;

import com.nex.task.action.Action;

public class CutTreeAction extends Action implements ObjectSpawnListener {

	private final String CUT_ACTION = "Chop down";
	//public static CutTreeAction instance = new CutTreeAction();
	public SceneObject currentTree;
	

	Position targetPos = null;
	Area lastArea = null;

//	static boolean isNearby(Area area, int distance){
//		Position pos = Players.getLocal().getPosition();
//		List<Position> tiles = area.getTiles();
//		int step = tiles
//		for(int i = 0; i < tiles.size(); i+= 4){
//			if (tiles.get(i).distance(pos) <= distance)
//				return true;
//		}
//		return false;
//	}

//	public CutTreeAction get() {
//		return instance;
//	}

	Position getOldestSeenTree(){
		Map.Entry<Position, Long> oldest = null;
		for (Map.Entry<Position, Long> kvp : treePositions.entrySet()) {
			if(oldest == null || kvp.getValue() < oldest.getValue())
				oldest = kvp;
		}
		if (oldest != null)
			return oldest.getKey();
		return null;
	}
	boolean walkToATree(){
		if(Players.getLocal().isMoving() || Players.getLocal().isAnimating())
			return false;
		Position pos = getOldestSeenTree();
		if(pos != null) {
			Position target = standPositions.getOrDefault(pos, pos);
			if(target.distance() > 3) {
				Movement.setWalkFlag(target);
				return true;
			}
		}
		return false;
	}

	HashMap<Position, Long> treePositions = new HashMap<>();
	HashMap<Position, Position> standPositions = new HashMap<>();
	public int cutTree(Area actionArea, String treeName) {
		if(lastArea != actionArea)
			targetPos = null;
		lastArea = actionArea;
		if(actionArea.contains(Players.getLocal()) || (targetPos != null && targetPos.distance() < 15)) {
			if(!interactWithTree(actionArea, treeName)) {
				if(walkToATree())
					return 300;
				return 50;
			}else{
				WalkTo.checkRun();
			}
			return 400;
		} else {

			targetPos = actionArea.getCenter();
			WalkTo.execute(targetPos, 4);
			return 600;
		}
	}

	private boolean interactWithTree(Area actionArea, String treeName) {
		if(currentTree == null || shouldCut()) {
			currentTree = SceneObjects.getNearest(getAvailableTrees(actionArea, treeName));
			if(currentTree != null) {
				return currentTree.interact(CUT_ACTION);
			}
		}
		return false;
	}
	
	private static boolean shouldCut() {
		Player local = Players.getLocal();
		return !local.isMoving() && !local.isAnimating();
	}
	
	private static Predicate<SceneObject> getAvailableTrees(Area actionArea, String treeName){
		return tree -> actionArea.contains(tree) && tree.getName().equals(treeName);
    };


	
	@Override
	public void notify(ObjectSpawnEvent e) {
		if(currentTree != null && e.getPosition().equals(currentTree.getPosition())) {
			Log.fine("Our tree disappeared");
			if(currentTree.getPosition().distance() <= 4 && currentTree.getName() != "Tree") {
				standPositions.put(currentTree.getPosition(), Players.getLocal().getPosition());
				treePositions.put(currentTree.getPosition(), System.currentTimeMillis());
			}
			currentTree = null;
		}
		
	}

}
