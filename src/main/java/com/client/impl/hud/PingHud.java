package com.client.impl.hud;

import com.client.system.hud.HudFunction;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.SmoothStepAnimation;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import mixin.accessor.MinecraftClientAccessor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;

import java.awt.*;

public class PingHud extends HudFunction {
    public PingHud() {
        super(new FloatRect(0, 0, 0, 0), "Ping-Hud");
        draggable = false;
    }

    private final SmoothStepAnimation animation = new SmoothStepAnimation(300, 1);

    @Override
    public void draw(float alpha) {
        String ping = "Ping: ";
        String value = "";

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        value += (playerListEntry != null && isEnabled() ? playerListEntry.getLatency() : 0);

        float offsetY;

        animation.setDirection(mc.currentScreen instanceof ChatScreen ? Direction.FORWARDS : Direction.BACKWARDS);

        offsetY = (float) ((mc.getWindow().getHeight() / 2) - 1 - (15f * animation.getOutput()));

        draw(offsetY - IFont.getHeight(IFont.MONTSERRAT_MEDIUM, ping, 7), ping, value, alpha);
    }

    private void draw(float y, String text, String speed, float alpha) {
        FloatRect rect = new FloatRect(mc.getWindow().getWidth() / 2 - IFont.getWidth(IFont.MONTSERRAT_BOLD, text, 7) - IFont.getWidth(IFont.MONTSERRAT_MEDIUM, speed, 7) - 5, y, 0, 0);

        FontRenderer.color(true);
        IFont.draw(IFont.MONTSERRAT_BOLD, text, rect.getX(), rect.getY(), inject(Color.WHITE, alpha), 7);
        FontRenderer.color(false);

        IFont.draw(IFont.MONTSERRAT_MEDIUM, speed, rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, text, 7), rect.getY(), inject(Color.WHITE, alpha), 7);
    }
}