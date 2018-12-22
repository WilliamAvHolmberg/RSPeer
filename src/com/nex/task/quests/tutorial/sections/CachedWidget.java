package com.nex.task.quests.tutorial.sections;



import java.util.Optional;
import java.util.function.Predicate;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.Interfaces;

import com.nex.task.action.QuestAction;

public class CachedWidget {

    private int parentID = -1, childID = -1, subChildID = -1;
    private String[] widgetTexts;
    private Predicate<InterfaceComponent> filter;

    public CachedWidget(final int parentID, final int childID){
        if(parentID < 0 || childID < 0) {
            throw new IllegalArgumentException("Widget IDs must have a value > 0");
        }
        this.parentID = parentID;
        this.childID = childID;
    }

    public CachedWidget(final int parentID, final int childID, final int subChildID){
        if(parentID < 0 || childID < 0 || subChildID < 0) {
            throw new IllegalArgumentException("Widget IDs must have a value > 0");
        }
        this.parentID = parentID;
        this.childID = childID;
        this.subChildID = subChildID;
    }

    public CachedWidget(final String... widgetTexts){
        if(widgetTexts.length == 0) {
            throw new IllegalArgumentException("At least 1 String must be provided");
        }
        this.widgetTexts = widgetTexts;
    }

    public CachedWidget(Predicate<InterfaceComponent> filter) {
        if (filter == null) {
            throw new IllegalArgumentException("filter cannot be null");
        }
        this.filter = filter;
    }

    public InterfaceComponent get(){
        if(subChildID != -1) {
            return QuestAction.get(parentID, childID, subChildID);
        } else if(parentID != -1) {
            return getSecondLevelWidget();
        } else if (widgetTexts != null) {
            return getWidgetWithText();
        } else {
            return getWidgetUsingFilter();
        }
    }

    private InterfaceComponent getSecondLevelWidget(){
        InterfaceComponent rs2Widget = QuestAction.get(parentID, childID);
        if(rs2Widget != null && rs2Widget.toAddress().getSubComponent() > 0){
            subChildID = rs2Widget.toAddress().getSubComponent();
        }
        return rs2Widget;
    }

    private InterfaceComponent getWidgetWithText(){
        InterfaceComponent rs2Widget = QuestAction.get(widgetTexts);
        if(rs2Widget != null){
            parentID = rs2Widget.toAddress().getRoot();
            childID = rs2Widget.toAddress().getComponent();
            if(rs2Widget.toAddress().getSubComponent() > 0) {
                subChildID = rs2Widget.toAddress().getSubComponent();
            }
        }
        return rs2Widget;
    }

    private InterfaceComponent getWidgetUsingFilter() {
        InterfaceComponent rs2Widget = Interfaces.getFirst(filter);
        if (rs2Widget != null) {
        	 parentID = rs2Widget.toAddress().getRoot();
             childID = rs2Widget.toAddress().getComponent();
             if(rs2Widget.toAddress().getSubComponent() > 0) {
                 subChildID = rs2Widget.toAddress().getSubComponent();
             }
        }
        return rs2Widget;
    }

    @Override
    public String toString() {
        return parentID + ", " + childID + ", " + subChildID;
    }
} 