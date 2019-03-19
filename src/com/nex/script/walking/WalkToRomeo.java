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
		SceneObject object = null;
		if(Movement.buildPath(romeoPosition) != null) {
			Movement.walkTo(romeoPosition);
		}else if((object = getObject(entranceDoor)) != null){
			object.interact("Open");
		}else if((object = getObject(upperSecondDoor)) != null) {
			object.interact("Open");
		}else if((object = getObject(upperFirstDoor)) != null) {
			object.interact("Open");
		}else if((object = getObject(upperStairs)) != null) {
			object.interact("Climb-down");
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
