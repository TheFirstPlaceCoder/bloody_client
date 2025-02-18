package com.client.system.notification;


import com.client.system.textures.DownloadImage;
import net.minecraft.util.Identifier;

import java.awt.*;

public enum NotificationType {
    ENABLE(DownloadImage.getGlId(DownloadImage.CIRCLE), Color.GREEN),
    DISABLE(DownloadImage.getGlId(DownloadImage.CIRCLE), Color.RED),
    CLIENT(DownloadImage.getGlId(DownloadImage.CIRCLE), Color.WHITE);

    public final int id;
    public final Color color;

    NotificationType(int id, Color color) {
        this.id = id;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public int getTexture() {
        return id;
    }
}
