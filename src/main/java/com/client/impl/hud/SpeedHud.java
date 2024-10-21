package com.client.impl.hud;

import com.client.interfaces.IChatScreen;
import com.client.system.command.Command;
import com.client.system.hud.HudFunction;
import com.client.system.hud.HudManager;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.SmoothStepAnimation;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import net.minecraft.client.gui.screen.ChatScreen;

import java.awt.*;

public class SpeedHud extends HudFunction {
    public SpeedHud() {
        super(new FloatRect(0, 0, 0, 0), "Speed-Hud");
        draggable = false;
    }

    private final SmoothStepAnimation animation = new SmoothStepAnimation(300, 1);

    @Override
    public void draw(float alpha) {
        if (mc.currentScreen instanceof ChatScreen chatScreen && ((IChatScreen) chatScreen).getChatField().trim().startsWith(Command.getPrefix())) return;

        String bps = "Bps: ";
        String speed = isEnabled() ? PlayerUtils.getBps().replace(",", ".") : "0.0";

        float y, offsetY;

        animation.setDirection(mc.currentScreen instanceof ChatScreen ? Direction.FORWARDS : Direction.BACKWARDS);

        offsetY = (float) ((mc.getWindow().getHeight() / 2) - (15f * animation.getOutput()));

        y = HudManager.get(CoordsHud.class).isEnabled() || mc.currentScreen instanceof ChatScreen ? IFont.getHeight(IFont.MONTSERRAT_MEDIUM, "AAA123", 7) + 1 : 0;

        draw(offsetY - y - IFont.getHeight(IFont.MONTSERRAT_MEDIUM, bps, 7), bps, speed, alpha);
    }

    private void draw(float y, String text, String speed, float alpha) {
        FloatRect rect = new FloatRect((float) 2, y, 0, 0);

        FontRenderer.color(true);
        IFont.draw(IFont.MONTSERRAT_BOLD, text, rect.getX(), rect.getY(), inject(Color.WHITE, alpha), 7);
        FontRenderer.color(false);

        IFont.draw(IFont.MONTSERRAT_MEDIUM, speed, rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, text, 8), rect.getY(), inject(Color.WHITE, alpha), 7);
    }
}