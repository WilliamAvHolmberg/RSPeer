package com.nex.script;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Varps;

public enum Quest {

   RESTLESS_GHOST(107,5), ERNEST_THE_CHICKEN(32,3),ROMEO_JULIET(144, 100),
    GOBLIN_DIPLOMACY(62,6), TUTORIAL_ISLAND(281,1000), COOKS_ASSISTANT(29,2);

    private final int varpId;
	private int completedValue;

    Quest(int varpId, int completedValue) {
        this.varpId = varpId;
        this.completedValue = completedValue;
    }

    public int getVarpId() {
        return varpId;
    }

    public int getVarpValue() {
        return Game.isLoggedIn() ? Varps.get(varpId) : -1;
    }
    
    public boolean isCompleted() {
    	return getVarpValue() == completedValue;
    }

    @Override
    public String toString() {
        String fixed = name().toLowerCase().replace("_", " ");
        return fixed.substring(0, 1).toUpperCase().concat(fixed.substring(1));
    }
    
    public static int getQuestPoints() {
    	return Varps.get(101);
    }
}
