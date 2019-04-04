package com.nex.task.quests.tutorial.sections;



import java.awt.event.KeyEvent;
import java.util.function.Predicate;

import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;

import com.nex.task.action.QuestAction;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;

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
        Npc instructor = getInstructor();
        if(instructor == null) return;
        if (!instructor.isPositionInteractable()) {
            WalkTo.execute(instructor.getPosition());
        }
        else if (!Dialog.isOpen() && instructor != null && instructor.interact("Talk-to")) {
            Time.sleepUntil(this::pendingContinue, 800, 6000);
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

    static Predicate<String> defaultAction = a -> true;
    public void doDefault(){
        if (pendingContinue()) {
            selectContinue();
        } else if (!Dialog.isProcessing()) {
            switch (Game.getClient().getHintArrowType()) {
                case 0:
                    Log.info("no hint arrow");
                    break;
                case 1:
                    Npc npc = Npcs.getAt(Game.getClient().getHintArrowNpcIndex());
                    if(npc != null && npc.isPositionInteractable()) {
                        npc.interact(defaultAction);
                        Time.sleepWhile(() -> Players.getLocal().isMoving() || Players.getLocal().isAnimating(), 100, 6000);
                    }
                    break;
                case 2:
                    SceneObject so = getSceneObjectHint();
                    if(so != null && so.isPositionInteractable()) {
                        so.interact(defaultAction);
                        Time.sleepWhile(()->Players.getLocal().isMoving() || Players.getLocal().isAnimating(), 100, 6000);
                    }
                    break;
            }
        }
    }
    public SceneObject getSceneObjectHint(){
        if (Game.getClient().getHintArrowType() != 2)
            return null;
        Position hintPos = new Position(Game.getClient().getHintArrowX(), Game.getClient().getHintArrowY(), Players.getLocal().getFloorLevel());
        Log.info(hintPos.toString());
        for (SceneObject so : SceneObjects.getAt(hintPos)) {
            if (so.containsAction(defaultAction)) {
                return so;
            }
        }
        return null;
    }
    
    public int random(int lowerBound, int upperBound) {
    	return Random.nextInt(lowerBound, upperBound);
    }

    private InterfaceComponent getContinueWidget() {
        InterfaceComponent irregularContinue = Interfaces.getFirst(162, (i)->i.getText().contains("Click here to continue"));
        if(irregularContinue != null) return irregularContinue;
        return Interfaces.getContinue();
    }
}
