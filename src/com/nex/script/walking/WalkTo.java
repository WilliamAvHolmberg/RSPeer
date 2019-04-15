package com.nex.script.walking;

import com.nex.script.grandexchange.BuyItemHandler;

import org.rspeer.runetek.adapter.component.Item;
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
		execute(area.getCenter(),4);
	}

	static Area lumbrdigeCastle = Area.rectangular(3204, 3228, 3216, 3207);
	static Area lumbridgeGoblin =Area.rectangular(3265, 3264, 3239, 3216);
	static Area cowfield =  Area.rectangular(3135, 3310, 3204, 3339);
	static Area Alkharid = Area.rectangular(3266, 3144, 3341, 3322);
	static Area Riverbank = Area.rectangular(3105, 3338, 3151, 3390);
	static Area FaladorSouthWall = Area.rectangular(3006, 3316, 3035, 3331);
	static Area DraynorManorBotLeft = Area.rectangular(3080, 3326, 3117, 3342);
	static Area DraynorManorHouse = Area.rectangular(3091, 3352, 3126, 3374);
	static Area KaramjaIsland = Area.rectangular(2881, 3132, 2964, 3195);
	static Area Varrock = Area.rectangular(3140, 3395, 3286, 3518);
	static Area taverleyDungeon = Area.polygonal(
		    new Position[] {
		            new Position(2803, 9858, 0),
		            new Position(2965, 9861, 0),
		            new Position(2961, 9799, 0),
		            new Position(2990, 9791, 0),
		            new Position(2957, 9709, 0),
		            new Position(2910, 9659, 0),
		            new Position(2790, 9689, 0)
		        }
		    );
	
	static Area taverleyStairsUpside = Area.rectangular(2880, 3400, 2888, 3392);
	
	static Position[] GoblinToLumbridgeCastle = {
		    new Position(3256, 3240, 0),
		    new Position(3257, 3238, 0),
		    new Position(3258, 3233, 0),
		    new Position(3259, 3232, 0),
		    new Position(3259, 3229, 0),
		    new Position(3256, 3226, 0),
		    new Position(3251, 3225, 0),
		    new Position(3246, 3225, 0),
		    new Position(3242, 3225, 0),
		    new Position(3237, 3225, 0)
		};

		static Position[] GoblinToVarrock = {
		    new Position(3255, 3224, 0),
		    new Position(3257, 3227, 0),
		    new Position(3259, 3227, 0),
		    new Position(3259, 3230, 0),
		    new Position(3259, 3232, 0),
		    new Position(3259, 3235, 0),
		    new Position(3259, 3239, 0),
		    new Position(3257, 3244, 0),
		    new Position(3254, 3248, 0),
		    new Position(3248, 3254, 0),
		    new Position(3248, 3254, 0),
		    new Position(3249, 3262, 0)
		};
	static Position[] LumbridgeToChickenCoop = {
			new Position(3207, 3210, 0),
			new Position(3207, 3210, 0),
			new Position(3215, 3212, 0),
			new Position(3218, 3218, 0),
			new Position(3227, 3218, 0),
			new Position(3232, 3223, 0),
			new Position(3232, 3232, 0),
			new Position(3232, 3245, 0),
			new Position(3232, 3248, 0),
			new Position(3232, 3254, 0),
			new Position(3235, 3261, 0),
			new Position(3241, 3263, 0),
			new Position(3240, 3272, 0),
			new Position(3238, 3284, 0),
			new Position(3239, 3292, 0),
			new Position(3238, 3296, 0),
			new Position(3238, 3302, 0),
			new Position(3236, 3306, 0)
	};
	static Area LumbrdigeToChickenArea = Area.rectangular(3207, 3210, 3245, 3296);

	static PathExecutor executor;
	static Path path;
	static Position lastPosition;
	public static boolean execute(Position position) {
		return execute(position, 2);
	}
	public static boolean execute(Position position, int random) {

		if(Players.getLocal().isMoving()) {
			Log.fine("WALKING");
			return true;
		}
		
		Position curPos = Players.getLocal().getPosition();
		
	  if(taverleyDungeon.contains(position) && !taverleyDungeon.contains(curPos)) {
		  Log.fine("TO TAVERLEY");
		  return walkToTaverleyDungeon(position);
		}
	  
	  if(!taverleyDungeon.contains(position) && taverleyDungeon.contains(curPos)) {
		  Log.fine("FROM TAVERLEY");
			return walkFromTaverleyDungeon(position);
		}
	  
	  
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
		
		if(lumbridgeGoblin.contains(curPos) && lumbridgeGoblin.contains(position)&& position.getY() > 3255){
			return execute(GoblinToVarrock);
		}else if(lumbridgeGoblin.contains(curPos) && !lumbridgeGoblin.contains(position) && position.getY() < 3255){
			return execute(GoblinToLumbridgeCastle);

		}
		if(Varrock.contains(position) && LumbrdigeToChickenArea.contains(curPos)){
			return execute(LumbridgeToChickenCoop);
		}
		else if(lumbrdigeCastle.contains(position) && LumbrdigeToChickenArea.contains(curPos) && !lumbrdigeCastle.contains(curPos)){
			return executeRev(LumbridgeToChickenCoop);
		}
		else if(cowfield.contains(curPos) && position.getY() > curPos.getY()){
			position = new Position(3141, 3350);//Avoid getting stuck in the cow field
		}
		else if(Alkharid.contains(position) && !Alkharid.contains(curPos)){
			position = new Position(3281, 3319);
		}
		else if(Riverbank.contains(curPos) && position.getY() > curPos.getY()) {
			position = new Position(3100, 3419);
		}
		else if(FaladorSouthWall.contains(curPos) && BuyItemHandler.getGEArea().contains(position)){
			position = new Position(3065, 3322);
		}
		else if(DraynorManorBotLeft.contains(curPos)){
			position = new Position(3070, 3329);
		}
		else if(KaramjaIsland.contains(curPos) && !KaramjaIsland.contains(position)){
			if(!Tabs.open(Tab.MAGIC))
				Tabs.open(Tab.MAGIC);
			if (Magic.cast(Spell.Modern.HOME_TELEPORT))
				Time.sleepUntil(()-> !KaramjaIsland.contains(Players.getLocal()), 1000, 60000);
		}

		if(executor == null) {
			executor = new PathExecutor();
			path = null;
		}
		else if(lastPosition == null || lastPosition.distance(position) > random) {
			lastPosition = position;
			executor = new PathExecutor();
			path = null;
		}

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

		if(position.distance(curPos) < 16 && random != 0)
			position = position.randomize(random);

		boolean result = false;
		Log.fine("Walking to " + position.toString());

//		if(path == null)
//			path = Movement.buildPath(position);
//		if (path != null) {
//			result = path.walk();
//		}
		//if(Movement.buildPath(position) != null) {
			result = Movement.walkTo(position, executor);
		//}
		if(!result)
			Log.fine("Failed to walk...");
		return result;
	}

	
	private static boolean walkFromTaverleyDungeon(Position position) {
		Item faladorTeleport = Inventory.getFirst("Falador teleport");
		if(faladorTeleport != null) {
			Position myPos = Players.getLocal().getPosition();
			faladorTeleport.click();
			Time.sleepUntil(() -> !taverleyDungeon.contains(Players.getLocal()), 10000);
			return true;
		}else {
			Log.fine("NO TELEPORT");
		}return false;
	}

	static Area taverleyAgilityShortcut = Area.rectangular(2936, 3356, 2939, 3353);
	private static boolean walkToTaverleyDungeon(Position position) {
		Position myPos = Players.getLocal().getPosition();
		if(taverleyStairsUpside.contains(myPos)) {
			SceneObject obstacle = SceneObjects.getNearest("Ladder");
			if(obstacle != null) {
				obstacle.interact("Climb-down");
				return true;
			}
		}else if(myPos.getX() < 2935) {
		  return execute(taverleyStairsUpside.getCenter());
		}else if(taverleyAgilityShortcut.contains(myPos)) {
			SceneObject obstacle = SceneObjects.getNearest("Crumbling Wall");
			if(obstacle != null) {
				obstacle.interact("Climb-over");
				return true;
			}
		}else {
			return execute(taverleyAgilityShortcut.getCenter());
		}
		return true;
	}

	static int run_at = Random.nextInt(9,16);
	public static void checkRun(){
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
	public static boolean executeRev(Position[] path) {

		checkRun();

		Position pos = Players.getLocal().getPosition();
		int nearest = 0;
		double closestDist = path[nearest].distance(pos);
		for(int i = 0; i < path.length; i++){
			double tmpDist = path[i].distance(pos);
			if (tmpDist < closestDist){
				closestDist = tmpDist;
				nearest = i;
			}
		}
		if (nearest > 0)
			nearest -= 1;
		for (; nearest > 0; nearest--)
			if(path[nearest - 1].distance(pos) > 14) break;
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

	public static Position[] createReverse(Position[] positions){
		int n = positions.length;
		Position[] b = new Position[n];
		int j = n;
		for (int i = 0; i < n; i++) {
			b[j - 1] = positions[i];
			j = j - 1;
		}
		return b;
	}

}
