package com.nex.task.quests.tutorial.sections;



import java.awt.event.KeyEvent;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.scene.Npcs;

import com.nex.task.action.QuestAction;

public abstract class TutorialSection extends QuestAction{

    private final String INSTRUCTOR_NAME;

    public TutorialSection(final String INSTRUCTOR_NAME) {
        this.INSTRUCTOR_NAME = INSTRUCTOR_NAME;
    }

    public abstract void onLoop();

    protected final int getProgress() {
        return Varps.get(281);
    }
    
    protected int getConfig(int id) {
    	return Varps.get(id);
    }
    
 

    protected final void talkToInstructor() {
        if (!Dialog.isOpen() && getInstructor() != null && getInstructor().interact("Talk-to")) {
            Time.sleepUntil(this::pendingContinue, 5000, 600);
        }
    }

    protected Npc getInstructor() {
        return Npcs.getNearest(INSTRUCTOR_NAME);
    }

    protected boolean pendingContinue() {
        if(Dialog.canContinue()) return true;
        if(Dialog.isProcessing()) return true;
        InterfaceComponent continueWidget = getContinueWidget();
        return continueWidget!= null && continueWidget.isVisible();
    }

    protected boolean selectContinue() {
        InterfaceComponent comp = getContinueWidget();
    	if(comp == null) {
    		return false;
    	}else {
    	    if(Dialog.canContinue())
                return Dialog.processContinue();
    	    else
                return comp.click();
    	}
    }
    
    public int random(int lowerBound, int upperBound) {
    	return Random.nextInt(lowerBound, upperBound);
    }

    private InterfaceComponent getContinueWidget() {
        InterfaceComponent irregularContinue = Interfaces.getFirst(162, (i)->i.getText().contains("continue"));
        if(irregularContinue != null) return irregularContinue;
        return Interfaces.getContinue();
    }
}
