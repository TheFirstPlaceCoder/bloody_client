package com.client.clickgui.newgui.settings;

import com.client.clickgui.newgui.GuiScreen;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.DoubleSetting;
import com.client.utils.Utils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class DoubleSettingElement extends AbstractSettingElement {
    private FloatRect sliderRect = new FloatRect(), textRect = new FloatRect();
    private double sliderWidth;
    private double target_slider_width;
    private boolean dragged;
    public String name;

    public DoubleSettingElement(AbstractSettings<?> settings, FloatRect rect) {
        super(settings, rect);
        this.name = Utils.isRussianLanguage || setting.getEnName() == null || setting.getEnName().isEmpty() ? setting.getName() : setting.getEnName();
    }

    public void setNameHeight(double height) {
        this.name = parse(Utils.isRussianLanguage || setting.getEnName() == null || setting.getEnName().isEmpty() ? setting.getName() : setting.getEnName(), rect.getW() - height, IFont.MONTSERRAT_BOLD, 6);
        //rect.setH(Math.max(rect.getH(), IFont.getHeight(IFont.MONTSERRAT_BOLD, name, 6)));
    }

    @Override
    public float getAdd() {
        return rect.getH() + 2;
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        rect.setH(Math.max(IFont.getHeight(IFont.MONTSERRAT_BOLD, name, 6),
                IFont.getHeight(IFont.MONTSERRAT_BOLD, "" + ((DoubleSetting) setting).get(), 6)) + 2 + 3 + 3);

        sliderRect = new FloatRect(rect.getX() + 10,
                rect.getY() + Math.max(IFont.getHeight(IFont.MONTSERRAT_BOLD, name, 6),
                        IFont.getHeight(IFont.MONTSERRAT_BOLD, "" + ((DoubleSetting) setting).get(), 6)) + 2,
                rect.getW() - 20,
                3);

        textRect = new FloatRect(rect.getX2() - 4 - IFont.getWidth(IFont.MONTSERRAT_BOLD, "" + ((DoubleSetting) setting).get(), 8),
                rect.getY().floatValue(),
                IFont.getWidth(IFont.MONTSERRAT_BOLD, "" + ((DoubleSetting) setting).get(), 8),
                IFont.getHeight(IFont.MONTSERRAT_BOLD, "" + ((DoubleSetting) setting).get(), 6));

        setNameHeight(textRect.getW() + 10);

        updateSlider(mx);

        sliderWidth = AnimationUtils.fast(sliderWidth, target_slider_width);

        FloatRect sliderRect2 = new FloatRect(sliderRect.getX(), sliderRect.getY(), sliderWidth, sliderRect.getH());

        Color invactive = GuiScreen.clickGui.settingsTextColor.get();
        Color active = GuiScreen.clickGui.settingsElementsTextColor.get();

        GL.drawRoundedRect(sliderRect, 1, inject(invactive, alpha));
        GL.drawRoundedGradientRect(sliderRect2, 1, inject(Colors.getColor(0), alpha), inject(Colors.getColor(0), alpha), inject(Colors.getColor(270), alpha), inject(Colors.getColor(270), alpha));
        GL.drawCircle(sliderRect2.getX2() - 2, sliderRect2.getCenteredY(), 3, 0, inject(active, alpha));

        IFont.draw(IFont.MONTSERRAT_BOLD, name, rect.getX() + 8, rect.getY(), inject(invactive, alpha), 6);

        GL.drawRoundedGradientRect(textRect,
                1, inject(Colors.getColor(0), alpha), inject(Colors.getColor(0), alpha), inject(Colors.getColor(270), alpha), inject(Colors.getColor(270), alpha));

        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, "" + ((DoubleSetting) setting).get(), textRect.getCenteredX(), textRect.getCenteredY(), inject(active, alpha), 6);
    }

    @Override
    public void click(double mx, double my, int button) {
        if (rect.intersect(mx, my)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                dragged = true;
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_3) {
                ((DoubleSetting) setting).set(((DoubleSetting) setting).getDefaultValue());
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
        final double min = ((DoubleSetting) setting).getMin();
        final double max = ((DoubleSetting) setting).getMax();
        target_slider_width = sliderRect.getW() * (((DoubleSetting) setting).get() - min) / (max - min);
        if (dragged) {
            if (diff == 0.0) {
                ((DoubleSetting) setting).set(((DoubleSetting) setting).getMin());
            } else {
                ((DoubleSetting) setting).set(Double.parseDouble(String.format(("%." + ((DoubleSetting) setting).getC() + "f"), diff / sliderRect.getW() * (max - min) + min).replace(",", ".")));
            }
        }
    }
}