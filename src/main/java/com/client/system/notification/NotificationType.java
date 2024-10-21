package com.client.system.notification;


import net.minecraft.util.Identifier;

import java.awt.*;

public enum NotificationType {
    ENABLE(new Identifier("bloody-client", "/client/circle.png"), Color.GREEN),
    DISABLE(new Identifier("bloody-client", "/client/circle.png"), Color.RED),
    CLIENT(new Identifier("bloody-client", "/client/client.png"), Color.WHITE);

    public final Identifier id;
    public final Color color;

    NotificationType(Identifier id, Color color) {
        this.id = id;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public Identifier getTexture() {
        return id;
    }
}
