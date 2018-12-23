package com.nex.script.walking;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.SceneObjects;

public abstract class WalkEventRomeoAndJuliet implements WalkEvent{
	protected final Position julietPosition = new Position(3158,3425,1);
	protected final Position basementStairs = new Position(3156,3435);
	protected final Position romeoPosition = new Position(3213,3423,0);
	protected final Area outsideJulietHousePosition = Area.rectangular(3165, 3431,3168,3435);
	protected final Position upperSecondDoor = new Position(3158,3426,1);
	protected final Position upperFirstDoor = new Position(3157,3430,1);
	protected final Position lowerStairs = new Position(3156,3435,0);
	protected final Position upperStairs = new Position(3156,3435,1);
	protected final Position entranceDoor = new Position(3165,3433,0);
	
	public SceneObject getObject(Position position) {
		return SceneObjects.getNearest(p -> p.distance() < 20 && p.isPositionInteractable() && p.getPosition().equals(position));
	}
}
