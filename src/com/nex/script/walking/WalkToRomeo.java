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

public class WalkToRomeo extends WalkEventRomeoAndJuliet {




	@Override
	public void execute() {
		boolean failed = false;
		if(Movement.buildPath(romeoPosition) != null) {
			Movement.walkTo(romeoPosition);
		}else if(getObject(entranceDoor) != null){
			getObject(entranceDoor).interact("Open");	
		}else if(getObject(upperStairs) != null) {
			getObject(upperStairs).interact("Climb-down");
		}else if(getObject(upperFirstDoor) != null) {
			getObject(upperSecondDoor).interact("Open");
		}else if(getObject(upperSecondDoor) != null) {
			getObject(upperSecondDoor).interact("Open");
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
