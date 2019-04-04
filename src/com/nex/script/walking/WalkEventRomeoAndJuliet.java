package com.nex.script.walking;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.SceneObjects;

import java.util.Arrays;

public abstract class WalkEventRomeoAndJuliet implements WalkEvent{
	protected final Position julietPosition = new Position(3158,3425,1);
	protected final Position basementStairs = new Position(3156,3435);
	protected final Position romeoPosition = new Position(3213,3423,0);
	protected final Area outsideJulietHousePosition = Area.rectangular(3165, 3431,3168,3435);
	protected final Position upperSecondDoor = new Position(3158,3426,1);
	protected final Position upperFirstDoor = new Position(3157,3430,1);
	protected final Area lowerStairs = Area.rectangular(3154, 3434, 3157, 3436,0);
	protected final Area upperStairs = Area.rectangular(3155, 3434, 3158,3436,1);
	protected final Area entranceDoor = Area.rectangular(3163, 3432, 3165,3433,0);
	
	public SceneObject getObject(Position position) {
		return SceneObjects.getNearest(p -> p.distance() < 20 && p.isPositionInteractable() && p.getPosition().equals(position));
	}
	public SceneObject getObject(Area area, String name) {
		return SceneObjects.getNearest(p -> p.distance() < 20 && p.getName().equals(name) && area.contains(p.getPosition()));
	}
	public SceneObject getDoor(Position position) {
		return SceneObjects.getNearest(p -> p.distance() < 20 && Arrays.stream(p.getActions()).anyMatch((s)->s.equals("Open")) && p.getPosition().equals(position));
	}
	public SceneObject getDoor(Area area) {
		return SceneObjects.getNearest(p -> p.distance() < 20 && Arrays.stream(p.getActions()).anyMatch((s)->s.equals("Open")) && area.contains(p.getPosition()));
	}
}
