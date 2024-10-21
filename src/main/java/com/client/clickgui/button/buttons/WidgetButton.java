package com.client.clickgui.button.buttons;

import com.client.clickgui.GuiScreen;
import com.client.clickgui.button.SettingButton;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.Widget;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class WidgetButton extends SettingButton {
    public WidgetButton(AbstractSettings<?> setting, FloatRect rect) {
        super(setting, rect);
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        FloatRect drawRect = new FloatRect(rect.getX() + 4, rect.getY().floatValue(), rect.getW() - 8, rect.getH().floatValue());
        GL.drawRoundedRect(drawRect, 2, inject(GuiScreen.BACK_OTHER, alpha));
        IFont.drawCenteredXY(IFont.MONTSERRAT_MEDIUM, setting.getName(), drawRect.getCenteredX(), drawRect.getCenteredY(), inject(Color.WHITE, alpha), 7);
    }

    @Override
    public void click(double mx, double my, int button) {
        if (rect.intersect(mx, my) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            ((Widget) setting).get().run();
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
