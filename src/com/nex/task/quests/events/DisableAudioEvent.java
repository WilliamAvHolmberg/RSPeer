package com.nex.task.quests.events;


import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;

import com.nex.task.action.QuestAction;
import com.nex.task.quests.tutorial.sections.CachedWidget;
import com.nex.task.quests.tutorial.sections.RuneScapeGuideSection;

public final class DisableAudioEvent{

    private final CachedWidget soundSettingsWidget = new CachedWidget("Audio");
    private final CachedWidget musicVolumeWidget = new CachedWidget("Adjust Music Volume");
    private final CachedWidget soundEffectVolumeWidget = new CachedWidget("Adjust Sound Effect Volume");
    private final CachedWidget areaSoundEffectVolumeWidget = new CachedWidget("Adjust Area Sound Effect Volume");

    private static final int musicVolumeConfig = 168;
    private static final int soundEffectVolumeConfig = 169;
    private static final int areaSoundEffectVolumeConfig = 872;

    public final static int execute() {
        if (!Tabs.isOpen(Tab.OPTIONS)) {
            Tabs.open(Tab.OPTIONS);
        } else if(!isVolumeDisabled(musicVolumeConfig) && QuestAction.interactButton(261,45)) {
        	
        }else if (!isVolumeDisabled(soundEffectVolumeConfig)&& QuestAction.interactButton(261,51)) {
            
        } else if (!isVolumeDisabled(areaSoundEffectVolumeConfig)&& QuestAction.interactButton(261,57)) {
           RuneScapeGuideSection.isAudioDisabled = true;
        }else {
        	QuestAction.interactButton(261,1,2);
        }
        return 200;
    }

    private static boolean isVolumeDisabled(final int config) {
        return Varps.get(config) == 4;
    }
}
