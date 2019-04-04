package com.nex.task.custom;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.function.Predicate;

import com.nex.handler.gear.GearHandler;
import com.nex.handler.gear.GearItem;
import com.nex.script.Exchange;
import com.nex.script.banking.BankHandler;
import com.nex.script.banking.DepositAllExcept;
import com.nex.script.banking.WithdrawItemEvent;
import com.nex.script.grandexchange.BuyItemHandler;
import com.nex.script.items.RSItem;
import com.nex.script.items.WithdrawItem;
import com.nex.task.helper.InteractionHelper;
import com.nex.task.tanning.actions.BankLeatherWithdrawCowhide;
import com.nex.task.tanning.actions.TanHide;
import com.nex.task.tanning.actions.WalkToBank;
import com.nex.task.tanning.actions.WalkToTanner;
import com.nex.task.woodcutting.WoodcuttingTask;
import com.nex.task.woodcutting.actions.CutTreeAction;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.ChatMessageListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ChatMessageType;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import com.nex.task.SkillTask;

import javax.imageio.ImageIO;

public class CustomTask extends SkillTask implements RenderListener, ChatMessageListener {

    int totalCount;
    int profit;

    public CustomTask() {
        setActionArea(BuyItemHandler.getGEArea());
        setBankArea(BuyItemHandler.getGEArea());

        this.startExperience = Skills.getExperience(getSkill());
    }

    int needToChangeWorld = 0;
    long lastCountedPlayers = 0;

    // TODO - More generic. Do not check if player is in area all the time.
    @Override
    public int loop() {

        return 100;
    }

    @Override
    public Skill getSkill() {
        return Skill.WOODCUTTING;
    }

    @Override
    public boolean isFinished() {
        return Skills.getCurrentLevel(getSkill()) >= getWantedLevel();
    }
    @Override
    public void notify(ChatMessageEvent event) {

    }

    @Override
    public void notify(ObjectSpawnEvent spawnEvent) {
        CutTreeAction.get().notify(spawnEvent);
    }

    @Override
    public void notify(RenderEvent event) {
        Graphics g = event.getSource();
        int y = 300;
        g.drawString("Current Task: " + getSkill() + "->" + getWantedLevel(), 300, y += 25);
//        g.drawString("Log price: " + logPrice, 300, y += 25);
//        g.drawString("Logs chopped: " + logsChopped, 300, y += 25);
        g.drawString("Ran for: " + getTimeRanMS(), 300, y += 25);
        g.drawString("Per hour: " + getPerHour(totalCount), 300, y += 25);
        g.drawString("Money per hour: " + getMoneyPerHour(), 300, y += 25);
    }

    @Override
    public String getLog() {
        return getLog(getTaskID(), getExperiencePerHour(), getMoneyPerHour());
    }

    @Override
    public void removeTask() {
        //TASK LOG TODO

    }

    public int getMoneyPerHour() {
        return (int) (getPerHour(profit));
    }

}
