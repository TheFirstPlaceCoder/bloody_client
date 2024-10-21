package com.client.clickgui.button.buttons;

import com.client.clickgui.GuiScreen;
import com.client.clickgui.button.SettingButton;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.BooleanSetting;
import com.client.utils.color.ColorTransfusion;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class BooleanButton extends SettingButton {
    private FloatRect interactRect = new FloatRect();
    private final ColorTransfusion colorTransfusion;

    public String name;

    public BooleanButton(AbstractSettings<?> settings, FloatRect rect) {
        super(settings, rect);
        this.name = parse(settings.getName(), rect.getW() - 18, IFont.MONTSERRAT_MEDIUM, 7);
        colorTransfusion = new ColorTransfusion(((BooleanSetting) settings).get() ? Color.GREEN : GuiScreen.BACK_OTHER);
        rect.setH(Math.max(16, IFont.getHeight(IFont.MONTSERRAT_MEDIUM, name, 7)));
    }

    @Override
    public float getAdd() {
        return rect.getH() + 2;
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        interactRect = new FloatRect(rect.getX2() - 12, rect.getCenteredY() - 4, 8, 8);
        IFont.drawCenteredY(IFont.MONTSERRAT_MEDIUM, name, rect.getX() + 4, rect.getCenteredY(), inject(Color.WHITE, alpha), 7);

        if (((BooleanSetting) setting).get()) {
            colorTransfusion.animate(Color.GREEN, 15);
        } else {
            colorTransfusion.animate(GuiScreen.BACK_OTHER, 15);
        }

        GL.drawRoundedRect(interactRect, 2, colorTransfusion.getColor((int) (alpha * 255)));
    }

    @Override
    public void click(double mx, double my, int button) {
        if (interactRect.intersect(mx, my)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                ((BooleanSetting) setting).set(!((BooleanSetting) setting).get());
                ((BooleanSetting) setting).callback();
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_3) {
                ((BooleanSetting) setting).set(((BooleanSetting) setting).getDefaultValue());
            }
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
