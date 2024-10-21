package com.client.system.hud;

import com.client.clickgui.GuiScreen;
import com.client.clickgui.Impl;
import com.client.system.hud.setting.HudValue;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;

import static com.client.system.hud.HudFunction.mc;

public class HudSettingPanel implements Impl {
    public FloatRect rect = new FloatRect(0, 0, 100, 16);
    public List<HudValue> values;

    public void setValues(List<HudValue> values) {
        this.values = values;
        float h = 0;
        for (HudValue value : values) h += 16;
        rect.setH(h);

        if (rect.getY2() > mc.getWindow().getScaledHeight()) {
            rect.setY(mc.getWindow().getScaledHeight() - 5 - rect.getH());
        }
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        HudFunction.drawRect(rect, alpha);
        float y = rect.getY();
        for (HudValue value : values) {
            fillValue(value, y, alpha);
            y += 16;
        }
    }

    private void fillValue(HudValue value, float y, float a) {
        IFont.drawCenteredY(IFont.COMFORTAAB, value.getName(), rect.getX() + 5, y + 8, ColorUtils.injectAlpha(Color.WHITE, (int) (a * 255)), 7);
        GL.drawRoundedRect(new FloatRect(rect.getX2() - 12, y + 4, 8, 8), 2, ColorUtils.injectAlpha(value.get() ? Color.GREEN : GuiScreen.BACK_OTHER, (int) (a * 255)));
    }

    @Override
    public void click(double mx, double my, int button) {
        FloatRect temp = new FloatRect(rect.getX(), rect.getY(), rect.getW(), 16);
        for (HudValue value : values) {
            if (temp.intersect(mx, my) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
                value.set(!value.get());
                value.callback();
            }
            temp.addY(16f);
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
