package com.nex.script.walking;

import com.nex.script.grandexchange.BuyItemHandler;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import com.nex.script.walking.WalkTo;
import com.nex.task.quests.GoblinDiplomacyQuest;

import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.tab.*;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.path.Path;
import org.rspeer.runetek.api.movement.path.PredefinedPath;
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
	static Area Riverbank = Area.rectangular(3105, 3338, 3151, 3402);
	static Area FaladorSouthWall = Area.rectangular(3006, 3316, 3035, 3331);
	static Area DraynorManorBotLeft = Area.rectangular(3080, 3326, 3117, 3342);
	static Area DraynorManorHouse = Area.rectangular(3091, 3352, 3126, 3374);
	static Area KaramjaIsland = Area.rectangular(2881, 3132, 2964, 3195);

	static PathExecutor executor;
	static Path path;
	static Position lastPosition;
	public static boolean execute(Position position) {
		return execute(position, 2);
	}
	public static boolean execute(Position position, int random) {
		if(random > 0)
			position = position.randomize(random);

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
		if(DraynorManorBotLeft.contains(curPos)){
			position = new Position(3070, 3329);
		}
		if(KaramjaIsland.contains(curPos) && !KaramjaIsland.contains(position)){
			if(!Tabs.open(Tab.MAGIC))
				Tabs.open(Tab.MAGIC);
			if (Magic.cast(Spell.Modern.HOME_TELEPORT))
				Time.sleepUntil(()-> !KaramjaIsland.contains(Players.getLocal()), 1000, 60000);
		}

		if(executor == null)
			executor = new PathExecutor();
		else if(lastPosition == null || lastPosition.distance(position) > random) {
			lastPosition = position;
			executor = new PathExecutor();
		}
		else if(position.distance(lastPosition) < random)
			position = lastPosition;

		lastPosition = position;

		//idiot special exception. get rid asap
		if(curPos.distance(GoblinDiplomacyQuest.goblinPos) < 40) {
			SceneObject goblinDoor = SceneObjects.getNearest(p -> GoblinDiplomacyQuest.goblinPos.distance() < 30 && p.containsAction("Open") && p.getName().contains("Large door"));
			if (goblinDoor != null) {
				goblinDoor.interact("Open");
				Time.sleepUntil(() -> SceneObjects.getNearest(p -> p.containsAction("Open") && p.getName().contains("Large door")) == null, 4000);
			}
		}
		checkRun();

		boolean result = false;
		Log.fine("Walking...");
		if(Movement.buildPath(position) != null) {
			result = Movement.walkTo(position, executor);
		}
		if(!result)
			Log.fine("Failed to walk...");
		return result;
	}

	static int run_at = Random.nextInt(9,16);
	static void checkRun(){
		int add = Inventory.isFull() ? 20 : 0;
		if(Movement.getRunEnergy() > run_at + add && !Movement.isRunEnabled()) {
			run_at = Random.nextInt(9,16);
			Movement.toggleRun(true);
			Time.sleepUntil(() -> Movement.isRunEnabled(), 5000);
		}
	}

	public static boolean execute(Position[] path) {

		checkRun();

		Position pos = Players.getLocal().getPosition();
		int nearest = path.length - 1;
		double closestDist = path[nearest].distance(pos);
		for(int i = nearest - 1; i >= 0; i--){
			double tmpDist = path[i].distance(pos);
			if (tmpDist < closestDist){
				closestDist = tmpDist;
				nearest = i;
			}
		}
		if (nearest < path.length - 1)
			nearest += 1;
		for (; nearest < path.length - 1; nearest++)
			if(path[nearest + 1].distance(pos) > 14) break;
		if(path[nearest].distance(pos) < 17)
			Movement.setWalkFlag(path[nearest].randomize(2));
		else
			Movement.setWalkFlag(towards(pos, path[nearest], 15).randomize(2));
		return true;
	}

	public static Position towards(Position a, Position target, int distance){
		int distx = target.getX() - a.getX();
		int disty = target.getY() - a.getY();
		double len = Math.sqrt(distx * distx + disty + disty);
		double nx = distx / len;
		double ny = disty / len;
		return new Position(a.getX() + (int)(nx * distance), a.getY() + (int)(ny * distance));
	}

	public static void execute(SceneObject object) {
		execute(object.getPosition());
	}

}
