package com.client.impl.hud;

import com.client.interfaces.IChatScreen;
import com.client.system.command.Command;
import com.client.system.hud.HudFunction;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.SmoothStepAnimation;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import net.minecraft.client.gui.screen.ChatScreen;

import java.awt.*;

public class CoordsHud extends HudFunction {
    public CoordsHud() {
        super(new FloatRect(0, 0, 0, 0), "Coords-Hud");
        draggable = false;
    }

    private final SmoothStepAnimation animation = new SmoothStepAnimation(300, 1);

    @Override
    public void draw(float alpha) {
        if (mc.currentScreen instanceof ChatScreen chatScreen && ((IChatScreen) chatScreen).getChatField().trim().startsWith(Command.getPrefix())) return;

        String position = "Pos: ";
        String bp = isEnabled() ? getBp() : "0.0, 0.0, 0.0";
        String netherBp = isEnabled() ? getNetherPos() : "(0.0, 0.0, 0.0)";

        animation.setDirection(mc.currentScreen instanceof ChatScreen ? Direction.FORWARDS : Direction.BACKWARDS);

        float offsetY = (float) ((mc.getWindow().getHeight() / 2) - 1 - (15f * animation.getOutput()));

        draw((float) (offsetY - IFont.getHeight(IFont.MONTSERRAT_MEDIUM, position, 7)), position, bp, netherBp, alpha);
    }

    public String getNetherPos() {
        if (mc.world.getDimension().isBedWorking()) return "(" + String.format("%.1f", mc.player.getX() / 8).replace(",", ".") + ", " + String.format("%.1f", mc.player.getY()).replace(",", ".") + ", " + String.format("%.1f", mc.player.getZ() / 8).replace(",", ".") + ")";
        else return "(" + String.format("%.1f", mc.player.getX() * 8).replace(",", ".") + ", " + String.format("%.1f", mc.player.getY()).replace(",", ".") + ", " + String.format("%.1f", mc.player.getZ() * 8).replace(",", ".") + ")";
    }

    public String getBp() {
        return String.format("%.1f", mc.player.getX()).replace(",", ".") + ", " + String.format("%.1f", mc.player.getY()).replace(",", ".") + ", " + String.format("%.1f", mc.player.getZ()).replace(",", ".");
    }

    private void draw(float y, String text, String bp, String netherBp, float alpha) {
        FloatRect rect = new FloatRect((float) 2, y, 0, 0);

        FontRenderer.color(true);
        IFont.draw(IFont.MONTSERRAT_BOLD, text, rect.getX(), rect.getY(), inject(Color.WHITE, alpha), 7);
        FontRenderer.color(false);

        IFont.draw(IFont.MONTSERRAT_MEDIUM, bp, rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, text, 7), rect.getY(), inject(Color.WHITE, alpha), 7);
        IFont.draw(IFont.MONTSERRAT_MEDIUM, netherBp, rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, text, 7) + IFont.getWidth(IFont.MONTSERRAT_MEDIUM, bp + " ", 7), rect.getY(), inject(Color.RED, alpha), 7);
    }
}