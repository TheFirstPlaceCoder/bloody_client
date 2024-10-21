package com.client.clickgui.button.buttons.theme;

import com.client.clickgui.Impl;
import com.client.clickgui.button.SettingButton;
import com.client.system.setting.settings.theme.ThemeContainer;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ThemeButtonValue implements Impl {
    public final FloatRect rect;
    public final ThemeContainer container;
    private final Runnable task;
    public boolean current;

    public ThemeButtonValue(FloatRect rect, ThemeContainer container, Runnable task) {
        this.rect = rect;
        this.container = container;
        this.task = task;
    }

    private String parse(String in) {
        double w = IFont.getWidth(IFont.MONTSERRAT_MEDIUM, in, 7);
        double maxW = rect.getW() - 20f;
        if (w < maxW) {
            return in;
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            for (char c : in.toCharArray()) {
                stringBuilder.append(c);
                double tempW = IFont.getWidth(1, stringBuilder.toString(), 10);
                if (tempW >= maxW - 25.0) {
                    break;
                }
            }

            stringBuilder.append("...");
            return stringBuilder.toString();
        }
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        FontRenderer.color(current);
        IFont.drawCenteredY(IFont.MONTSERRAT_MEDIUM, parse(container.name()), rect.getX() + 2, rect.getCenteredY(), SettingButton.inject(Color.WHITE, alpha), 7);
        FontRenderer.color(false);
        GL.drawRoundedGradientRect(new FloatRect(rect.getX2() - 18, rect.getCenteredY() - 5, 16, 10), 2,
                SettingButton.inject(container.first(), alpha),
                SettingButton.inject(container.first(), alpha),
                SettingButton.inject(container.second(), alpha),
                SettingButton.inject(container.second(), alpha)
        );
    }

    @Override
    public void click(double mx, double my, int button) {
        if (rect.intersect(mx, my) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            task.run();
        }
    }

    @Override
    public void release(double mx, double my, int button) {

    }

    @Override
    public void key(int key) {

    }

    @Override
    public void symbol(char chr) {

    }

    @Override
    public void scroll(double mx, double my, double amount) {

    }

    @Override
    public void close() {

    }
}
