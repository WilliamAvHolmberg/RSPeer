package com.nex.task.quests.tutorial.sections;


import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;

import com.nex.script.Nex;
import com.nex.task.quests.events.DisableAudioEvent;
import com.nex.task.quests.events.EnableFixedModeEvent;

import okhttp3.internal.http2.Settings;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI.TabSelectionHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public final class RuneScapeGuideSection extends TutorialSection {

    private final CachedWidget nameLookupWidget = new CachedWidget(p -> p.getText().contains("Look up name"));
    private final CachedWidget checkNameWidget = new CachedWidget(w -> w.getText().contains("What name would you like to check"));
    private final CachedWidget suggestedNameWidget = new CachedWidget("suggestions");
    private final CachedWidget setNameWidget = new CachedWidget("Set name");
    private final CachedWidget creationScreenWidget = new CachedWidget("Head");
    private final CachedWidget experienceWidget = new CachedWidget("What's your experience with Old School Runescape?");
    public static boolean isAudioDisabled = false;
    public RuneScapeGuideSection() {
        super("Gielinor Guide");
    }

    @Override
    public final void onLoop() {
        if (pendingContinue()) {
            selectContinue();
            return;
        }
        switch (getProgress()) {
    
            case 0:
            case 1:
            case 2:
            	if (getConfig(1042) != 21) {
                    setDisplayName();
            		Log.fine("lets set name");
                    Time.sleep(4000);
                }else if (isVisible("Head")) {
                	Log.fine("createscree");
                    createRandomCharacter();
                } else if (experienceWidget.get() != null) {
                	
                    if (Dialog.process(random(1, 3))) {
                       Time.sleepUntil(() -> experienceWidget.get() == null, 2000);
                    }
                }else if(experienceWidget.get() == null) {
                	Log.fine("experience wid0");
                    talkToInstructor();
                }
                break;
            case 3:
                if (!EnableFixedModeEvent.isFixedModeEnabled()) {
                    EnableFixedModeEvent.execute();
                } else {
                	Tabs.open(Tab.OPTIONS);
                   // getTabs().open(Tab.SETTINGS);
                }
                break;
            case 7:
            	talkToInstructor();
            	break;
            case 10:
                if (!isAudioDisabled) {
                    DisableAudioEvent.execute();
                }  else if (SceneObjects.getNearest("Door").interact("Open")) {
                    Time.sleepUntil(() -> getProgress() != 10, 5000, 600);
                }
                break;
            	
        }
    }
    
    private String lastName = "asdasdasdasd";
    private void setDisplayName() {
        int configID = 1042;
        int configValue = getConfig(configID);
        
        switch (configValue) {
            case 0:
            case 1:
            	if(Nex.USERNAME != null) {
            		if (isVisible("not available") && lastName == Nex.USERNAME) {
                        Log.fine("bad name, lets change");
                        InterfaceComponent suggestion = Interfaces.getComponent(558, 14);
                        if(suggestion.getText().length() > 7 && suggestion.getText().length() <= 12)
                        {
                            Nex.USERNAME = suggestion.getText();
                        } else {
                            if (Nex.USERNAME.length() < 10) {
                                Nex.USERNAME = Nex.USERNAME + random(1, 9);
                            } else {
                                Nex.USERNAME = Nex.USERNAME.substring(0, Nex.USERNAME.length() - 1) + random(1, 9);
                            }
                        }
                        Log.fine("New name:" + Nex.USERNAME);
                    } else if(interactIfNotVisible("What name woul","Look up name")) {
	                    Time.sleep(1000);
	            	}else if (!nameIsEntered(Nex.USERNAME)) {  
	            		lastName = Nex.USERNAME;
	                    Keyboard.sendText(Nex.USERNAME);
	                    Time.sleep(2000);
	                    Keyboard.pressEnter();
	                }
            	}
                break;
            case 4:
                if (interactAction("Set name")) {
                        Time.sleep(7500);
                }
            default:
                Time.sleepUntil(() -> getConfig(1042) != configValue, 1200);
        }
        
       
    }
    


    private boolean isCreationScreenVisible() {
        return isVisible("Head");
    }

    private void createRandomCharacter() {
        // letting all the widgets show up
        Time.sleep(2000);

        if (new Random().nextInt(2) == 1) {
            interactButton("Female");
        }

        InterfaceComponent[] childWidgets = getChilds("Head");
        Collections.shuffle(Arrays.asList(getChilds("Head")));

        for (final InterfaceComponent childWidget : childWidgets) {
            if (childWidget.getToolTip() == null) {
                continue;
            }
            if (childWidget.getToolTip().contains("Change") || childWidget.getToolTip().contains("Recolour")) {
                try {
					clickRandomTimes(childWidget);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }

        if (interactButton("Accept")) {
           Time.sleepUntil(() -> !isVisible("Head"), 3000, 600);
        }
    }
    
    public InterfaceComponent[] getChilds(String text) {
    	int root = Interfaces.getFirst(p -> p.getText().contains(text) && p.isVisible()).toAddress().getRoot();
    	return Interfaces.get(root);
    }

    private void clickRandomTimes(final InterfaceComponent widget) throws InterruptedException {
        int clickCount = new Random().nextInt(4);

        for (int i = 0; i < clickCount; i++) {
            if (widget.click()) {
                Time.sleep(150);
            }
        }
    }

 

    


	public String getUsername() {
		return Nex.USERNAME;
	}
	
	


}
