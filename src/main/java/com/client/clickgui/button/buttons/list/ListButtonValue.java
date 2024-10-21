package com.client.clickgui.button.buttons.list;

import com.client.clickgui.GuiScreen;
import com.client.clickgui.Impl;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ListButtonValue implements Impl {
    public FloatRect rect;
    public Runnable task;
    public String text;

    public ListButtonValue(FloatRect rect, Runnable task, String text) {
        this.rect = rect;
        this.task = task;
        this.text = text;
    }

    public void draw(double mx, double my, float alpha, String current) {
        GL.drawRoundedRect(rect, 2, ColorUtils.injectAlpha(GuiScreen.BACK_OTHER, (int) (alpha * 255)));
        IFont.drawCenteredXY(IFont.MONTSERRAT_MEDIUM, text, rect.getCenteredX(), rect.getCenteredY(), ColorUtils.injectAlpha(text.equals(current) ? Color.WHITE : Color.GRAY, (int) (alpha * 255)), 7);
    }

    @Override
    public void draw(double mx, double my, float alpha) {

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
