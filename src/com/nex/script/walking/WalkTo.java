package com.nex.script.walking;

import com.nex.script.grandexchange.BuyItemHandler;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import com.nex.script.walking.WalkTo;
import com.nex.task.quests.GoblinDiplomacyQuest;

import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;
public class WalkTo {

	public static void execute(Area area) {
		execute(area.getCenter());
	}

	static Area lumbrdigeCastle = Area.rectangular(3204, 3228, 3216, 3207);
	static Area cowfield =  Area.rectangular(3135, 3310, 3204, 3339);
	static Area Alkharid = Area.rectangular(3266, 3144, 3341, 3322);
	static Area Riverbank = Area.rectangular(3083, 3328, 3151, 3392);
	static Area FaladorSouthWall = Area.rectangular(3006, 3316, 3035, 3331);

	static PathExecutor executor;
	static Position lastPosition;
	public static boolean execute(Position position) {
		return execute(position, 2);
	}
	public static boolean execute(Position position, int random) {
		position = position.randomize(random);
		if(Players.getLocal().isMoving())
			return true;
		if(executor == null)
			executor = new PathExecutor();
		else if(lastPosition == null || lastPosition.distance(position) > 5)
			executor = new PathExecutor();
		else if(position.distance() > 10)
			position = lastPosition;

		lastPosition = position;

		Position curPos = Players.getLocal().getPosition();
		lumbrdigeCastle.setIgnoreFloorLevel(true);
		while (curPos.getFloorLevel() > 0 && position.getFloorLevel() == 0 && lumbrdigeCastle.contains(curPos)){
			SceneObject stairs = SceneObjects.getNearest("Staircase");
			if(stairs == null)
				break;
			final int curFloor = curPos.getFloorLevel();
			if(stairs.interact("Climb-down"))
				Time.sleepWhile(()->Players.getLocal().getFloorLevel() == curFloor, 10000);
			Time.sleep(200, 400);
			curPos = Players.getLocal().getPosition();
		}
		if(cowfield.contains(curPos) && position.getY() > curPos.getY()){
			position = new Position(3141, 3350);//Avoid getting stuck in the cow field
		}
		if(Alkharid.contains(position) && !Alkharid.contains(curPos)){
			position = new Position(3281, 3319);
		}
		if(Riverbank.contains(curPos) && position.getY() > curPos.getY()) {
			position = new Position(3100, 3419);
		}
		if(FaladorSouthWall.contains(curPos) && BuyItemHandler.getGEArea().contains(position)){
			position = new Position(3065, 3322);
		}

		//idiot special exception. get rid asap
		if(curPos.distance(GoblinDiplomacyQuest.goblinPos) < 40) {
			SceneObject goblinDoor = SceneObjects.getNearest(p -> GoblinDiplomacyQuest.goblinPos.distance() < 30 && p.containsAction("Open") && p.getName().contains("Large door"));
			if (goblinDoor != null) {
				goblinDoor.interact("Open");
				Time.sleepUntil(() -> SceneObjects.getNearest(p -> p.containsAction("Open") && p.getName().contains("Large door")) == null, 4000);
			}
		}
		if(Movement.getRunEnergy() > 10 && !Movement.isRunEnabled()) {
			Movement.toggleRun(true);
			Time.sleepUntil(() -> Movement.isRunEnabled(), 5000);
		}
		if(Movement.buildPath(position) != null) {
			return Movement.walkTo(position, executor);
		}
		return false;
	}

	public static void execute(SceneObject object) {
		execute(object.getPosition());
	}

}
