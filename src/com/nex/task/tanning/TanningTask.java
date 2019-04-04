package com.nex.task.tanning;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.function.Predicate;

import com.nex.task.tanning.actions.BankLeatherWithdrawCowhide;
import com.nex.task.tanning.actions.TanHide;
import com.nex.task.tanning.actions.WalkToBank;
import com.nex.task.tanning.actions.WalkToTanner;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.event.listeners.ChatMessageListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import com.nex.task.SkillTask;

import javax.imageio.ImageIO;

public class TanningTask extends SkillTask implements ImageObserver {

    public static final Predicate<String> ANY_HIDE_NAME = name -> name.contains("Cowhide") || name.contains("dragonhide");
    public static final Predicate<String> ANY_TANNED_NAME = name -> name.contains("Leather") ||name.contains("Hard leather") || name.contains("dragon leather");
    public static final Predicate<Item> ANY_HIDE = item -> ANY_HIDE_NAME.test(item.getName());
    public static final Predicate<Item> ANY_HIDE_RAW = item -> ANY_HIDE_NAME.test(item.getName()) && !item.isNoted();
    public static final Predicate<Item> ANY_TANNED = item -> ANY_TANNED_NAME.test(item.getName());

    public static final Area TANNER_AREA = Area.rectangular(3271, 3191, 3277, 3193);
    private final Task[] TASKS = {
            new WalkToBank(),
            new BankLeatherWithdrawCowhide(this),
            new WalkToTanner(),
            new TanHide(this)
    };

    public int totalTanned = 0;
    public int totalProfit = 0;
    public int HideID;
    public int WithdrawQty;

    public TanningTask(int hideID, int withdrawQty) {
        HideID = hideID;
        WithdrawQty = withdrawQty;
        setActionArea(TANNER_AREA);
        setBankArea(Area.surrounding(BankLocation.AL_KHARID.getPosition(), 6));
        this.startExperience = Skills.getExperience(getSkill());
    }

    // TODO - More generic. Do not check if player is in area all the time.
    @Override
    public int loop() {
        for (Task task : TASKS) {
            if(task.validate())
                return task.execute();
        }
        return 600;
    }

    @Override
    public Skill getSkill() {
        return Skill.WOODCUTTING;
    }

    @Override
    public boolean isFinished() {
        return Skills.getCurrentLevel(getSkill()) >= getWantedLevel();
    }

    private int getHourlyRate(Duration sw) {
        double hours = sw.getSeconds() / 3600.0;
        double tannedPerHour = this.totalTanned / hours;
        return (int) tannedPerHour;
    }

    private void logStats() {
        int[] stats = this.getStats();
        String statsString = "Tanned: "
                + stats[0]
                + "  |  Total profit: " + stats[1]
                + "  |  Hourly profit: " + stats[2];
        Log.info(statsString);
    }

    static Image getImage(String url){
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e){
            return null;
        }
    }

    // painting
    private static final Font font = new Font("Arial",Font.BOLD, 24).deriveFont(14f);
    private static final Font fontSmall = font.deriveFont(font.getSize2D() + 2f);
    private static final Font fontBigger = font.deriveFont(font.getSize2D() + 5f);
    private static final DecimalFormat formatNumber = new DecimalFormat("#,###");
    private static final String imageUrl = "https://i.imgur.com/1qEl73a.png";
    private static final Image image1 = getImage(imageUrl);

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return false;
    }

    @Override
    public void notify(RenderEvent renderEvent) {
        Graphics g = renderEvent.getSource();

        // render the paint layout
        if(image1 != null)
            g.drawImage(image1, 0, 0, this);

        // render time running
        g.setFont(fontSmall);
        this.drawStringWithShadow(
                g,
                durationRunning.toString(),
                242,
                21,
                Color.YELLOW.darker()
        );

        // render tanned and profit
        int[] stats = this.getStats();
        if(stats.length >= 3) {
            int totalCowhideTanned = stats[0];
            int totalProfit = stats[1];
            int hourlyProfit = stats[2];

            g.setFont(fontBigger);

            int adjustY = -5;

            this.drawStringWithShadow(g, formatNumber.format(totalCowhideTanned), 68, 229 + adjustY, Color.YELLOW);
            this.drawStringWithShadow(g, formatNumber.format(totalProfit), 68, 274 + adjustY, Color.WHITE);
            this.drawStringWithShadow(g, formatNumber.format(hourlyProfit), 68, 319 + adjustY, Color.WHITE);
        }
    }

    private void drawStringWithShadow(Graphics g, String str, int x, int y, Color color) {
        if(str == null) return;
        //g.setColor(Color.BLACK);
        //g.drawString(str, x + 2, y + 2); // draw shadow
        g.setColor(color);
        g.drawString(str, x, y); // draw string
    }

    Duration durationRunning = Duration.ofMillis(0);

    private int[] getStats() {
        durationRunning = Duration.ofMillis(getTimeRanMS());

        //int totalLeatherValue = this.totalTanned * this.leatherPrice;
        int hourlyProfit = this.getHourlyRate(durationRunning) * totalProfit;
        int[] stats = {
                this.totalTanned,
                totalProfit,
                hourlyProfit
        };
        return stats;
    }

    @Override
    public void removeTask() {
        //TASK LOG TODO
    }


    @Override
    public void notify(ObjectSpawnEvent objectSpawnEvent) {
    }

    @Override
    public void notify(ChatMessageEvent chatMessageEvent) {
    }
}
