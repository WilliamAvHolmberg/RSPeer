package com.nex.task.quests.tutorial.sections;



import java.util.Arrays;
import java.util.List;

import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import com.nex.script.walking.WalkTo;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;

public final class PriestSection extends TutorialSection {

    private static final Area CHURCH_AREA = Area.rectangular(3120, 3103, 3128, 3110);

   

    public PriestSection() {
        super("Brother Brace");
    }

    @Override
    public final void onLoop() {
        if (pendingContinue()) {
            selectContinue();
            return;
        }

        switch (getProgress()) {
            case 550:
                if (getInstructor() == null) {
                    WalkTo.execute(CHURCH_AREA.getCenter());
                } else {
                    talkToInstructor();
                }
                break;
            case 560:
                Tabs.open(Tab.PRAYER);
                break;
            case 570:
                talkToInstructor();
                break;
            case 580:
            	Tabs.open(Tab.FRIENDS_LIST);
                break;
            case 590:
            	Tabs.open(Tab.IGNORE_LIST);
                break;
            case 600:
                talkToInstructor();
                break;
            case 610:
                WalkTo.execute(new Position(3122,3099));
                break;
        }
    }
}
