package com.client.clickgui.button.buttons;

import com.client.clickgui.GuiScreen;
import com.client.clickgui.button.SettingButton;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class IntegerButton extends SettingButton {
    private FloatRect sliderRect = new FloatRect();
    private double sliderWidth;
    private double target_slider_width;
    private boolean dragged;

    public String name;

    public IntegerButton(AbstractSettings<?> settings, FloatRect rect) {
        super(settings, rect);
        name = parse(settings.getName(), rect.getW() - 10, IFont.MONTSERRAT_MEDIUM, 7);
        rect.addH(IFont.getHeight(IFont.MONTSERRAT_MEDIUM, name, 7) - IFont.getHeight(IFont.MONTSERRAT_MEDIUM, settings.getName(), 7));
    }

    @Override
    public float getAdd() {
        return rect.getH() + 2;
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        sliderRect = new FloatRect(rect.getX() + 2, rect.getY2() - 8, rect.getW() - 4, 6);
        updateSlider(mx);

        sliderWidth = AnimationUtils.fast(sliderWidth, target_slider_width);

        FloatRect sliderRect2 = new FloatRect(sliderRect.getX(), sliderRect.getY(), sliderWidth, sliderRect.getH());

        GL.drawRoundedRect(sliderRect, 2, inject(GuiScreen.BACK_OTHER, alpha));
        GL.drawRoundedGradientRect(sliderRect2, 2, inject(Colors.getColor(0), alpha), inject(Colors.getColor(0), alpha), inject(Colors.getColor(270), alpha), inject(Colors.getColor(270), alpha));

        IFont.draw(IFont.MONTSERRAT_MEDIUM, name, rect.getX() + 4, rect.getY() + 2, inject(Color.WHITE, alpha), 7);

        IFont.draw(IFont.MONTSERRAT_MEDIUM, "" + ((IntegerSetting) setting).get(), rect.getX2() - 4 - IFont.getWidth(IFont.MONTSERRAT_MEDIUM, "" + ((IntegerSetting) setting).get(), 7), rect.getY() + 2, inject(Color.WHITE, alpha), 7);
    }

    @Override
    public void click(double mx, double my, int button) {
        if (rect.intersect(mx, my)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                dragged = true;
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_3) {
                ((IntegerSetting) setting).set(((IntegerSetting) setting).getDefaultValue());
            }
        }
    }

    @Override
    public void release(double mx, double my, int button) {
        dragged = false;
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

    public void updateSlider(double mouseX) {
        final double diff = Math.min(sliderRect.getW(), Math.max(0, mouseX - sliderRect.getX()));
        final double min = ((IntegerSetting) setting).getMin();
        final double max = ((IntegerSetting) setting).getMax();
        target_slider_width = sliderRect.getW() * (((IntegerSetting) setting).get() - min) / (max - min);
        if (dragged) {
            if (diff == 0.0) {
                ((IntegerSetting) setting).set(((IntegerSetting) setting).getMin());
            } else {
                ((IntegerSetting) setting).set((int) Double.parseDouble(String.format("%.1f", diff / sliderRect.getW() * (max - min) + min).replace(",", ".")));
            }
        }
    }
}