package com.nex.task.fishing;

import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;

import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;

public enum Fish {

    SHRIMP      ("Raw shrimp"       , "Small fishing net", "Fishing spot", "Net"     , "", 1),
    SARDINE     ("Raw sardine"      , "Fishing rod"      , "Fishing spot", "Bait"    , "Bait", 5),
    KARAMBWANJI ("Raw karambwanji"  , "Small fishing net", "Fishing spot", "Net"     , "", 5),
    HERRING     ("Raw herring"      , "Fishing rod"      , "Fishing spot", "Bait"    , "Bait", 10),
    ANCHOVIES   ("Raw anchovies"    , "Small fishing net", "Fishing spot", "Net"     , "", 15),
    MACKEREL    ("Raw mackerel"     , "Big fishing net"  , "Fishing spot", "Net"     , "", 16),
    TROUT       ("Raw trout"        , "Fly fishing rod"  , "Fishing spot", "Lure"    , "Feather", 20),
    COD         ("Raw cod"          , "Big fishing net"  , "Fishing spot", "Bait"    , "Bait", 23),
    PIKE        ("Raw pike"         , "Fishing rod"      , "Fishing spot", "Bait"    , "Bait", 25),
    SLIMY_EEL   ("Raw slimy eel"    , "Fishing rod"      , "Fishing spot", "Bait"    , "Bait", 28),
    SALMON      ("Raw salmon"       , "Fly fishing rod"  , "Fishing spot", "Lure"    , "Feather", 30),
    TUNA        ("Raw tuna"         , "Harpoon"          , "Fishing spot", "Harpoon" , "", 35),
    RAINBOW_FISH("Raw rainbow fish" , "Fly fishing rod"  , "Fishing spot", "Lure"    , "", 38),
    CAVE_EEL    ("Raw cave eel"     , "Fishing rod"      , "Fishing spot", "Bait"    , "Bait", 38),
    LOBSTER     ("Raw lobster"      , "Lobster pot"      , "Fishing spot", "Cage"    , "", 40),
    BASS        ("Raw bass"         , "Big fishing net"  , "Fishing spot", "Net"     , "", 46),
    SWORDFISH   ("Raw swordfish"    , "Harpoon"          , "Fishing spot", "Harpoon" , "", 50),
    MONKFISH    ("Raw monkfish"     , "Small fishing net", "Fishing spot", "Net"     , "", 62),
    SHARK       ("Raw shark"        , "Harpoon"          , "Fishing spot", "Harpoon" , "", 76),
    KARAMBWAN   ("Raw karambwan"    , "Small fishing net", "Fishing spot", "Fish"    , "Karamwbanji", 65);

    @Override
    public String toString() {
        return rawName;
    }

    private final String rawName, equipment, spot, action, bait;
    private final int requiredLevel;

    Fish(final String rawName, final String equipment, final String spot, final String action, final String bait, final int requiredLevel) {
        this.rawName = rawName;
        this.equipment = equipment;
        this.spot = spot;
        this.requiredLevel = requiredLevel;
        this.action = action;
        this.bait = bait;
    }

    public String getRawName() { return rawName; }

    public String getEquipment() { return equipment; }

    public String getSpot() { return spot; }

    public String getAction() { return action; }

    public String getBait() { return bait; }

    public int getRequiredLevel() { return requiredLevel; }

    public boolean isAccessible() {
        return Skills.getCurrentLevel(Skill.FISHING) >= requiredLevel;
    }
}