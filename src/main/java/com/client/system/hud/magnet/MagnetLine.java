package com.client.system.hud.magnet;

import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class MagnetLine {
    public final float x, x2, y, y2;
    private final FloatRect magnetRect;

    private int alpha;

    public MagnetLine(float x, float x2, float y, float y2) {
        this.x = x;
        this.x2 = x2;
        this.y = y;
        this.y2 = y2;

        if (x != x2) {
            magnetRect = new FloatRect(x, y - 20, x2 - x, 40);
        } else {
            magnetRect = new FloatRect(x - 20, y, 40, y2 - y);
        }
    }

    public void draw(int mx, int my) {
        if (magnetized(mx, my)) {
            if (alpha < 255) alpha += 25;
        } else {
            if (alpha > 0) alpha -= 25;
        }

        alpha = MathHelper.clamp(alpha, 0, 255);

        GL.drawLine(x, y, x2, y2, 0.5F, new Color(255, 255, 255, alpha));
    }

    public boolean direction() {
        return x != x2;
    }

    public boolean magnetized(int mx, int my) {
        return magnetRect.intersect(mx, my);
    }
}