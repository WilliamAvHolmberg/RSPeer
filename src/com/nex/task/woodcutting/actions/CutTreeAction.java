package com.nex.task.woodcutting.actions;

import java.util.List;
import java.util.function.Predicate;

import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import com.nex.script.walking.WalkTo;
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

	public int cutTree(Area actionArea, String treeName) {
		if(lastArea != actionArea)
			targetPos = null;
		lastArea = actionArea;
		if(actionArea.contains(Players.getLocal()) || (targetPos != null && targetPos.distance() < 15)) {
			interactWithTree(actionArea, treeName);
			return 600;
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
			currentTree = null;
		}
		
	}

}
