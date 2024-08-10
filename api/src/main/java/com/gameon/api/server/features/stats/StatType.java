package com.gameon.api.server.features.stats;

public enum StatType {
    MONEY(0),
    MINED_BLOCKS(1),
    DEATHS(2),
    PLAYED_TIME(3),
    KILLS(4),
    MOB_KILLS(5),
    WALKED_DISTANCE(6),
    ANIMAL_BREED(7),
    DEALT_DAMAGE(8),
    PLACED_BLOCKS(9),
    XP_GAINED(10),
    BROKEN_TOOLS(11),
    ITEMS_CRAFTED(12),
    THROWN_ENDER_PEARLS(13),
    EATEN_GOLDEN_APPLES(14),
    ;

    private final int index;

    StatType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
