package com.nex.task.quests.tutorial.sections;



import java.util.Arrays;
import java.util.List;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import com.nex.script.walking.WalkTo;

import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.SceneObjects;

import com.mashape.unirest.http.options.Options;

public final class QuestSection extends TutorialSection {

    private static final Area QUEST_BUILDING = Area.rectangular(3083, 3119, 3089, 3125);

 

    public QuestSection() {
        super("Quest Guide");
    }

    @Override
    public final void onLoop(){
        if (pendingContinue()) {
            selectContinue();
            return;
        }
        switch (getProgress()) {
            case 200:
                boolean isRunning = Movement.isRunEnabled();
                if (Movement.toggleRun(!isRunning)) {
                    Time.sleepUntil(() -> !isRunning, 1200);
                }
                break;
            case 210:
            	if (!Movement.isRunEnabled()) {
            		Movement.toggleRun(true);
                    Time.sleepUntil(() -> Movement.isRunEnabled(), 1200);
                } else {
                    WalkTo.execute(QUEST_BUILDING.getCenter());
                }
                break;
            case 220:
                talkToInstructor();
                break;
            case 230:
            	Tabs.open(Tab.QUEST_LIST);
                break;
            case 240:
                talkToInstructor();
                break;
            case 250:
                if (SceneObjects.getNearest("Ladder").interact("Climb-down")) {
                    Time.sleepUntil(() -> getProgress() != 250, 5000, 600);
                }
                break;
        }
    }
}
