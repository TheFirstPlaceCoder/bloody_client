package com.client.impl.function.visual.storageesp;

import java.awt.*;

public enum Type {
    CHEST(Color.YELLOW),
    ENDER_CHEST(Color.MAGENTA),
    BARREL(new Color(224, 99, 32, 255)),
    SHULKER(Color.WHITE),
    END_PORTAL(Color.CYAN),
    DROPPER(Color.GRAY),
    DISPENSER(Color.DARK_GRAY),
    HOPPER(Color.LIGHT_GRAY),
    TRAPPED_CHEST(Color.ORANGE),
    UNKNOWN(Color.WHITE);

    final Color color;

    Type(Color color) {
        this.color = color;
    }
}