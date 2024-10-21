package com.client.clickgui.button.buttons.multiboolean;

import com.client.clickgui.GuiScreen;
import com.client.clickgui.Impl;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class MultiBooleanButtonValue implements Impl {
    public FloatRect rect;
    public MultiBooleanValue value;

    public MultiBooleanButtonValue(FloatRect rect, MultiBooleanValue value) {
        this.rect = rect;
        this.value = value;
    }

    public void drawValue(double mx, double my, float alpha) {
        GL.drawRoundedRect(rect, 2, ColorUtils.injectAlpha(GuiScreen.BACK_OTHER, (int) (alpha * 255)));
        IFont.drawCenteredXY(IFont.MONTSERRAT_MEDIUM, value.getName(), rect.getCenteredX(), rect.getCenteredY(), ColorUtils.injectAlpha(value.getValue() ? Color.WHITE : Color.GRAY, (int) (alpha * 255)), 7);
    }

    @Override
    public void draw(double mx, double my, float alpha) {
    }

    @Override
    public void click(double mx, double my, int button) {
        if (rect.intersect(mx, my) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            value.setValue(!value.getValue());
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
