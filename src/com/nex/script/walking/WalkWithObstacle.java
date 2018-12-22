package com.nex.script.walking;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import com.nex.script.walking.WalkTo;

import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.path.Path;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;

import com.mashape.unirest.http.options.Option;
import com.nex.script.Nex;

public class WalkWithObstacle implements WalkEvent {

	private Position obstaclePosition;
	private Position destination;
	private String obstacleName;

	public WalkWithObstacle(Position obstaclePosition, Position destination, String obstacleName) {
		this.obstaclePosition = obstaclePosition;
		this.destination = destination;
		this.obstacleName = obstacleName;
	}

	@Override
	public void execute() {

		/*
		 * if(!Movement.isRunEnabled()) { Movement.toggleRun(true); Time.sleepUntil(()->
		 * Movement.isRunEnabled(), 10000); }
		 */
		SceneObject object = SceneObjects.getNearest(p -> 
				Area.surrounding(obstaclePosition, 20).contains(p) && Area.surrounding(obstaclePosition, 20).contains(Players.getLocal()) && p.getName().equals(obstacleName));

		if (Movement.buildPath(destination) != null) {
			Nex.failedWalk = 0;
			Log.fine("walk dest");
			WalkTo.execute(destination);
		} else if (object != null && object.distance(Players.getLocal()) < 40 &&  object.isPositionWalkable() && object.isPositionInteractable() ) {
			WalkTo.execute(object);
			Log.fine("itneract");
			Log.fine(object.getName());
			object.interact(object.getActions()[0]);
			Time.sleepUntil(() -> !Players.getLocal().isMoving(), 6000);
		}else if (Movement.buildPath(obstaclePosition) != null || (object != null && !object.isPositionInteractable()) && !object.isPositionWalkable()) {
			Log.fine("walk obs");
			Movement.buildPath(obstaclePosition);
			WalkTo.execute(obstaclePosition);
		}  else {
			Log.fine("not walkable");
			Nex.failedWalk++;
			Log.fine("Door handler");
			SceneObject door = getClosest(destination);
			if (door != null) {
				door.interact("Open");
			}
		}

	}

	public SceneObject getClosest(Position destination) {
		SceneObject closest = null;
		SceneObject[] doors = SceneObjects.getLoaded(p -> p.distance() < 10 && p.containsAction("Open"));
		if (doors != null) {
			for (SceneObject obj : doors) {
				if (closest == null) {
					closest = obj;
				} else {
					if (obj.distance() < closest.distance()) {
						closest = obj;
					}
				}
			}
		}
		return closest;

	}

}
