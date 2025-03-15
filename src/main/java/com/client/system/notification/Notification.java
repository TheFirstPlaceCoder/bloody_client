package com.client.system.notification;

import com.client.system.hud.HudFunction;
import com.client.utils.Utils;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class Notification {
    public final NotificationType type;
    public final String message;
    private final FloatRect rect = new FloatRect();

    public float nextY;
    public long alive;

    private float alpha;

    public Notification(NotificationType type, String message) {
        this(type, message, 2000L);
    }

    public Notification(NotificationType type, String message, long alive) {
        this.type = type;
        this.message = message;

        float w = IFont.getWidth(IFont.COMFORTAAB, message, 8) + 8;

        this.rect.setX(NotificationManager.getX() - w / 2).setW(w).setH(IFont.getHeight(IFont.COMFORTAAB, message, 8) + 6);
        this.rect.setY(NotificationManager.getY() - 16f);
        this.nextY = NotificationManager.getY();

        this.alpha = 0;
        this.alive = System.currentTimeMillis() + alive;
    }

    public void draw() {
        if (alive > System.currentTimeMillis()) alpha += 25f / 255f;
        else alpha -= 25f / 255f;

        alpha = MathHelper.clamp(alpha, 0f, 1f);

        rect.setY(Math.round(AnimationUtils.fast(rect.getY(), nextY) * 10.0f) / 10.0f);

        Utils.rescaling(() -> {
            GL.drawRoundedGlowRect(rect, 5,4, ColorUtils.injectAlpha(Colors.getColor(0), (int) (255 * alpha)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (255 * alpha)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (255 * alpha)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (255 * alpha)));
            GL.drawRoundedRect(rect, 5, new Color(15, 15, 15, (int) (100 * alpha)));

            IFont.drawCenteredXY(IFont.COMFORTAAB, message, rect.getCenteredX(), rect.getCenteredY(), ColorUtils.injectAlpha(Color.WHITE, (int) (255 * alpha)), 8);
        });
    }

    public void next() {
        this.nextY += rect.getH() + 4;
    }

    public boolean remove() {
        return System.currentTimeMillis() > alive && alpha <= 0;
    }
}