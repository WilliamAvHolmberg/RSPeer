package com.nex.task.quests.tutorial.sections;



import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;

import com.nex.script.walking.WalkTo;

public final class BankSection extends TutorialSection {


    private static final Area BANK_AREA = Area.polygonal(
                    new Position(3125, 3121),
                    new Position(3126, 3121),
                    new Position(3126, 3119),
                    new Position(3118, 3119),
                    new Position(3118, 3121),
                    new Position(3119, 3121),
                    new Position(3119, 3123),
                    new Position(3115, 3123),
                    new Position(3115, 3128),
                    new Position(3118, 3128),
                    new Position(3118, 3126),
                    new Position(3122, 3126),
                    new Position(3122, 3130),
                    new Position(3126, 3130),
                    new Position(3126, 3128),
                    new Position(3128, 3128),
                    new Position(3128, 3126),
                    new Position(3130, 3126),
                    new Position(3130, 3123),
                    new Position(3125, 3123),
                    new Position(3125, 3121)
            
    );

    

    public BankSection() {
        super("Account Guide");
    }

    @Override
    public final void onLoop() {
        if (pendingContinue()) {
            selectContinue();
            return;
        }

        switch (getProgress()) {
            case 510:
                if (!BANK_AREA.contains(Players.getLocal().getPosition())) {
                    WalkTo.execute(BANK_AREA.getCenter());
                } else if (Dialog.isViewingChatOptions()) {
                    Dialog.process("Yes.");
                } else if (SceneObjects.getNearest("Bank booth").interact("Use")) {
                    Time.sleepUntil(this::pendingContinue, 5000, 600);
                }
                break;
            case 520:
                if (Bank.isOpen()) {
                    Bank.close();
                } else if (SceneObjects.getNearest("Poll booth").interact("Use")) {
                    Time.sleepUntil(this::pendingContinue, 5000, 600);
                }
                break;
            case 525:
                if (openDoorAtPosition(new Position(3125, 3124, 0))) {
                    Time.sleepUntil(() -> getProgress() != 525, 5000, 600);
                }
                break;
            case 530:
                talkToInstructor();
                break;
            case 531:
                openAccountManagementTab();
                break;
            case 532:
                talkToInstructor();
                break;
            case 540:
                if (openDoorAtPosition(new Position(3130, 3124, 0))) {
                    Time.sleepUntil(() -> getProgress() != 540, 5000, 600);
                }
                break;
        }
    }

    private boolean openDoorAtPosition(final Position position) {
        SceneObject door = SceneObjects.getNearest(obj -> obj.getName().equals("Door") && obj.getPosition().equals(position));
        return door != null && door.interact("Open");
    }

    private void openAccountManagementTab() {
        Tabs.open(Tab.ACCOUNT_MANAGEMENT);
        Time.sleepUntil(() ->Tabs.isOpen(Tab.ACCOUNT_MANAGEMENT), 5000);
    }

}
