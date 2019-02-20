package com.nex.task.tanning.actions;

import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class WalkToBank extends Task {
    @Override
    public boolean validate() {
        // True if player is far away from the bank
        return !Conditions.atBank() && (!Conditions.gotCowhide() || !Conditions.gotEnoughCoins());
    }

    @Override
    public int execute() {
        // walk to Al Kharid bank
        WalkTo.execute(BankLocation.AL_KHARID.getPosition());
        Log.fine("lets walk");
        return 600;
    }
}
