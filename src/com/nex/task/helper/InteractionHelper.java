package com.nex.task.helper;

import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.providers.RSWorld;

public class InteractionHelper {

    public static int countPlayersOnMySceneObject(boolean animating){
        Player me = Players.getLocal();
        SceneObject myTarget = getSceneObjectLookingAt(me, animating);
        if(myTarget == null)
            return 0;
        int matched = 0;
        for(Player player : Players.getLoaded()){
            if(player == me) continue;
            if(getSceneObjectLookingAt(player, animating) == myTarget)
                matched++;
        }
        return matched;
    }

    public static SceneObject getSceneObjectLookingAt(Player player, boolean animating){
        if (animating && !player.isAnimating())
            return null;
        Position fwd = GetForwardTile(player.getPosition(), player.getOrientation());
        SceneObject[] targets = SceneObjects.getAt(fwd);
        for (SceneObject target : targets) {
            if(target.getActions().length == 0 || target.getName() == null) continue;
            return target;
        }
        return null;
    }

    public static Position GetForwardTile(Position pos, int orientation){
        //Log.fine("Orientation: ", orientation);
        orientation = (int)(orientation / 256.0) * 256;
        switch (orientation){
            case 1024: return pos.translate(0, 1); //N
            case 1536: return pos.translate(1, 0); //E
            case 0: return pos.translate(0, -1);   //S
            case 512: return pos.translate(-1, 0); //W

            case 1792: return pos.translate(1, -1); //SE
            case 256: return pos.translate(-1, -1); //SW
            case 768: return pos.translate(-1, 1);  //NW
            case 1280: return pos.translate(1, 1);  //NE
        }
        return pos.translate(0, 1);
    }

    static int lastWorld;
    public static boolean HopRandomWorld(){
        if(!WorldHopper.isOpen()) {
            WorldHopper.open();
            if (!Time.sleepUntil(()->WorldHopper.isOpen(), 2000))
                return false;
        }
        if(hasSwappedWorld()) return true;
        RSWorld curWorld = Worlds.get(Worlds.getCurrent());
        boolean members = curWorld.isMembers();
        boolean deadman = curWorld.isDeadman();
        if(WorldHopper.randomHop( (w)->w.isMembers() == members && w.isDeadman() == deadman )){
            Time.sleepWhile(()->Worlds.getCurrent() == curWorld.getId(), 5000);
            if(hasSwappedWorld()) return true;
        }
        return false;
    }

    static boolean hasSwappedWorld(){
        int curWorldIndex = Worlds.getCurrent();
        if(lastWorld == 0) lastWorld = curWorldIndex;
        if(lastWorld != curWorldIndex) return true;
        return false;
    }

}
