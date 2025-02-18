package com.client.impl.hud;

import com.client.system.hud.HudFunction;
import com.client.system.hud.HudManager;
import com.client.utils.math.TickRate;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.SmoothStepAnimation;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import net.minecraft.client.gui.screen.ChatScreen;

import java.awt.*;

public class TpsHud extends HudFunction {
    public TpsHud() {
        super(new FloatRect(0, 0, 0, 0), "Tps-Hud");
        draggable = false;
    }

    private final SmoothStepAnimation animation = new SmoothStepAnimation(300, 1);
    private PingHud pingHud;

    @Override
    public void draw(float alpha) {
        if (pingHud == null) pingHud = HudManager.get(PingHud.class);

        String tps = "Tps: ";
        String value = isEnabled() ? String.format("%.1f", TickRate.getTickRate()).replace(",", ".") : "0.0";

        float y, offsetY;

        animation.setDirection(mc.currentScreen instanceof ChatScreen ? Direction.FORWARDS : Direction.BACKWARDS);

        offsetY = (float) ((mc.getWindow().getHeight() / 2) - (15f * animation.getOutput()));

        y = pingHud.isEnabled() || mc.currentScreen instanceof ChatScreen ? IFont.getHeight(IFont.MONTSERRAT_MEDIUM, "AAA123", 7) + 1 : 0;

        draw(offsetY - y - IFont.getHeight(IFont.MONTSERRAT_MEDIUM, tps, 7), tps, value, alpha);
    }

    private void draw(float y, String text, String speed, float alpha) {
        FloatRect rect = new FloatRect(mc.getWindow().getWidth() / 2 - IFont.getWidth(IFont.MONTSERRAT_BOLD, text, 7) - IFont.getWidth(IFont.MONTSERRAT_MEDIUM, speed, 7) - 5, y, 0, 0);

        FontRenderer.color(true);
        IFont.draw(IFont.MONTSERRAT_BOLD, text, rect.getX(), rect.getY(), inject(Color.WHITE, alpha), 7);
        FontRenderer.color(false);

        IFont.draw(IFont.MONTSERRAT_MEDIUM, speed, rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, text, 7), rect.getY(), inject(Color.WHITE, alpha), 7);
    }
}