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
import org.rspeer.runetek.event.listeners.ObjectSpawnListener;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.ui.Log;

import com.nex.task.action.Action;

public class CutTreeAction extends Action implements ObjectSpawnListener{

	private static final String CUT_ACTION = "Chop down";
	public static CutTreeAction instance = new CutTreeAction();
	public static SceneObject currentTree;
	

	static Position targetPos = null;
	static Area lastArea = null;

	public static CutTreeAction get() {
		return instance;
	}
	public static void cutTree(Area actionArea, String treeName) {
		if(actionArea.contains(Players.getLocal())) {
			interactWithTree(actionArea, treeName);
		}else {
			WalkTo.execute(actionArea.getCenter());
		}
	}
	private static void interactWithTree(Area actionArea, String treeName) {
		if(currentTree == null || shouldCut()) {
			currentTree = SceneObjects.getNearest(getAvailableTrees(actionArea, treeName));
			if(currentTree != null) {
				currentTree.interact(CUT_ACTION);
			}	
		}
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
