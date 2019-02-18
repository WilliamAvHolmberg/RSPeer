package com.nex.task.tanning.actions;

import com.nex.script.walking.WalkTo;
import com.nex.task.tanning.TanningTask;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.script.task.Task;

public class WalkToTanner extends Task {

    @Override
    public boolean validate() {
        // True if player is far away from the tanner
        return !Conditions.nearTanner() && Conditions.gotCowhide() && Conditions.gotEnoughCoins();
    }

    @Override
    public int execute() {
        WalkTo.execute(TanningTask.TANNER_AREA.getCenter());
        return 600;
    }
}
