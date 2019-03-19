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

    static long startedOpeningOptions = 0;
    @Override
    public final void onLoop() {
        if (pendingContinue()) {
            selectContinue();
            return;
        }
        switch (getProgress()) {
    
            case 0:
                if(Interfaces.isOpen(269)){
                    Interfaces.getComponent(269, 125).interact(a -> true);
                    Time.sleep(500);
                    Interfaces.getComponent(269, 125).interact(a -> true);
                    Time.sleep(500);
                    Interfaces.getComponent(269, 99).interact("");
                } else if (Dialog.isOpen()) {
                    Dialog.process(0);
                } else {
                    doDefault();
                }
            case 1:
            case 2:
                InterfaceComponent nameComponent = Interfaces.getComponent(558, 11);
            	if (getConfig(1042) != 21 && nameComponent != null) {
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
            	else
            	    doDefault();
                break;
            case 3:
                if(startedOpeningOptions == 0) startedOpeningOptions = System.currentTimeMillis();
                if(System.currentTimeMillis() - startedOpeningOptions > 10000) {
                    talkToInstructor();
                    for(int i = 0; i < 10 && Dialog.isOpen(); i++)
                        if(Dialog.processContinue() || Dialog.process(0))
                            Time.sleepWhile(Dialog::isProcessing, 800, 6000);
                }
                if (!EnableFixedModeEvent.isFixedModeEnabled()) {
                    EnableFixedModeEvent.execute();
                } else {
                    if (!Tabs.isOpen(Tab.OPTIONS))
                	    Tabs.open(Tab.OPTIONS);
                }
                break;
            case 7:
            	talkToInstructor();
            	break;
            case 10:
                if (!isAudioDisabled) {
                    DisableAudioEvent.execute();
                }  else if (SceneObjects.getNearest("Door").interact("Open")) {
                    Time.sleepUntil(() -> getProgress() != 10, 800, 6000);
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
            	    InterfaceComponent nameComponent = Interfaces.getComponent(558, 11);
            	    if(nameComponent != null)
            	        lastName = nameComponent.getText().trim();
            		if (isVisible("not available") && lastName.equalsIgnoreCase(Nex.USERNAME)) {
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
	                    Time.sleep(800, 1200);
	            	}else if (lastName != Nex.USERNAME) {
                        Time.sleep(800, 1200);
	            		lastName = Nex.USERNAME;
	                    Keyboard.sendText(Nex.USERNAME);
	                    Time.sleep(1500, 2200);
	                    Keyboard.pressEnter();
	                }
            	}
                break;
            case 4:
                if (interactAction("Set name")) {
                    Time.sleep(6600, 7500);
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
        Time.sleep(1800, 3200);

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
           Time.sleepUntil(() -> !isVisible("Head"), 300, 3000);
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
