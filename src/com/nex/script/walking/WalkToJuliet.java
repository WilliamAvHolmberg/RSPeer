package com.nex.script.walking;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import com.nex.script.walking.WalkTo;

import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.path.Path;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;

import com.mashape.unirest.http.options.Option;
import com.nex.script.Nex;

public class WalkToJuliet extends WalkEventRomeoAndJuliet {




	@Override
	public void execute() {
		boolean failed = false;
		if(getObject(upperSecondDoor) != null) {
			getObject(upperSecondDoor).interact("Open");
		}else if(getObject(upperFirstDoor) != null) {
			getObject(upperSecondDoor).interact("Open");
		}else if(getObject(lowerStairs) != null) {
			getObject(lowerStairs).interact("Climb-up");
		}else if(getObject(entranceDoor) != null){
			getObject(entranceDoor).interact("Open");	
		}else if(Players.getLocal().getPosition().getFloorLevel() == 0) {
			Movement.walkTo(outsideJulietHousePosition.getCenter());
		}else if(Movement.buildPath(julietPosition) != null) {
			Movement.walkTo(julietPosition);
		}else {
			failed = true;
		}
		if(failed) {
			Nex.failedWalk++;
		}else {
			Nex.failedWalk = 0;
		}
	}

}
