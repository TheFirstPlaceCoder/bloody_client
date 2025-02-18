package com.client.impl.hud;

import com.client.clickgui.newgui.GuiScreen;
import com.client.impl.function.client.Optimization;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import mixin.accessor.BossBarHudAccessor;
import mixin.accessor.MinecraftClientAccessor;
import net.minecraft.client.gui.hud.DebugHud;

import java.awt.*;

public class FpsHud extends HudFunction {
    public FpsHud() {
        super(new FloatRect(0, 0, 0, 0), "Fps-Hud");
        draggable = false;
    }

    private Optimization optimization;

    @Override
    public void draw(float alpha) {
        if (optimization == null) optimization = FunctionManager.get(Optimization.class);

        String ping = "Fps: ";
        String value = "" + (isEnabled() ? (int) (((MinecraftClientAccessor) mc).getFps() * (optimization.isEnabled() ? 1.5 : 1)) : 0);

        draw(2, ping, value, alpha);
    }

    private void draw(float y, String text, String speed, float alpha) {
        int size = ((BossBarHudAccessor) mc.inGameHud.getBossBarHud()).getBossBars().size();
        if (size <= 4) y += 20 * size;
        else y = mc.getWindow().getWidth() / 4 + 5;

        FloatRect rect = new FloatRect(mc.getWindow().getWidth() / 4 - ((IFont.getWidth(IFont.MONTSERRAT_BOLD, text, 7) + (isEnabled() ? IFont.getWidth(IFont.MONTSERRAT_MEDIUM, speed, 7) : 0)) / 2), y, 0, 0);

        FontRenderer.color(true);
        IFont.draw(IFont.MONTSERRAT_BOLD, text, rect.getX(), rect.getY(), inject(Color.WHITE, alpha), 7);
        FontRenderer.color(false);

        IFont.draw(IFont.MONTSERRAT_MEDIUM, speed, rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, text, 7), rect.getY(), inject(Color.WHITE, alpha), 7);
    }
}