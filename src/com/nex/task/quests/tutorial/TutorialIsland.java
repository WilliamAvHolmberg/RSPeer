package com.nex.task.quests.tutorial;


import java.awt.Graphics2D;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.ui.Log;

import com.nex.script.Nex;
import com.nex.script.Quest;
import com.nex.task.QuestTask;
import com.nex.task.quests.events.EnableFixedModeEvent;
import com.nex.task.quests.tutorial.sections.*;



public final class TutorialIsland extends QuestTask {
	static Area WHOLE_ISLAND_AREA = Area.rectangular(3048, 3145, 3165, 3040);
    private final TutorialSection rsGuideSection = new RuneScapeGuideSection();
    private final TutorialSection survivalSection = new SurvivalSection();
    private final TutorialSection cookingSection = new CookingSection();
    private final TutorialSection questSection = new QuestSection();
    private final TutorialSection miningSection = new MiningSection();
    private final TutorialSection fightingSection = new FightingSection();
    private final TutorialSection bankSection = new BankSection();
    private final TutorialSection priestSection = new PriestSection();
    private final TutorialSection wizardSection = new WizardSection();



    public int loop(){
    	Log.fine("we are in tut");
    	 if (pendingContinue()) {
    		 Log.fine("lets cotinue");
             selectContinue();
             return 0;
         }
    	if(!EnableFixedModeEvent.isFixedModeEnabled()) {
            EnableFixedModeEvent.execute();
        }
        switch (getTutorialSection()) {
            case 0:
            case 1:
                rsGuideSection.onLoop();
                break;
            case 2:
            case 3:
            	survivalSection.onLoop();
            	break;
            case 4:
            case 5:
            	cookingSection.onLoop();
            	break;
            case 6:
            case 7:
            	questSection.onLoop();
            	break;
            case 8:
            case 9:
            	miningSection.onLoop();
            	break;
            case 10:
            case 11:
            case 12:
            	fightingSection.onLoop();
            	break;
            case 14:
            case 15:
            	bankSection.onLoop();
            	break;
            case 16:
            case 17:
            	priestSection.onLoop();
            	break;
            case 18:
            case 19:
            case 20:
            	wizardSection.onLoop();
            	break;
        }
        return 200;
    }

    private boolean stuckContinueIsVisible() {
		return getWidgetContainingText("Click to continue") != null;
	}

	private int getTutorialSection() {
        return Varps.get(406);
    }
    

    public static boolean isTutorialIslandCompleted() {
        return 	!WHOLE_ISLAND_AREA.contains(Players.getLocal()) && getWidgetContainingText("Tutorial Island Progress") == null;
    }
    public boolean isFinished() {
    	return isTutorialIslandCompleted();
    }
    
    public static InterfaceComponent getWidgetContainingText(String text) {
    	return Interfaces.getFirst(p -> p.getText().contains(text));
    }

	@Override
	public String getLog() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public void removeTask() {
		//TODO queue tutorial island is done. Add RunTime
		
	}

	@Override
	public void notify(RenderEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notify(ChatMessageEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notify(ObjectSpawnEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public Quest getQuest() {
		return Quest.TUTORIAL_ISLAND;
	}

	@Override
	public int getQuestConfig() {
		return getQuest().getVarpId();
	}

	

}
