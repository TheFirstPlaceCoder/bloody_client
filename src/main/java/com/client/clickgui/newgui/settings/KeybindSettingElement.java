package com.client.clickgui.newgui.settings;

import com.client.clickgui.newgui.GuiScreen;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.KeybindSetting;
import com.client.utils.Utils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.misc.InputUtils;
import com.client.utils.render.wisetree.font.main.IFont;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class KeybindSettingElement extends AbstractSettingElement {
    private FloatRect interactRect = new FloatRect();
    private boolean intersect;
    public boolean binding = false;
    public String name;

    public KeybindSettingElement(AbstractSettings<?> setting, FloatRect rect) {
        super(setting, rect);
        this.name = Utils.isRussianLanguage || setting.getEnName() == null || setting.getEnName().isEmpty() ? setting.getName() : setting.getEnName();
    }

    public void setNameHeight(double height) {
        this.name = parse(Utils.isRussianLanguage || setting.getEnName() == null || setting.getEnName().isEmpty() ? setting.getName() : setting.getEnName(), rect.getW() - height, IFont.MONTSERRAT_BOLD, 6);
        rect.setH(Math.max(rect.getH(), IFont.getHeight(IFont.MONTSERRAT_BOLD, name, 6)));
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        setNameHeight(interactRect.getW() + 10);

        intersect = rect.intersect(mx, my);
        int key = ((KeybindSetting)setting).get();
        String bindName = "[" + (binding ? "..." : key >= 90000 ? InputUtils.getButtonName(key - 90001) : key == -1 ? (Utils.isRussianLanguage ? "Нету" : "None") : InputUtils.getKeyName(key)) + "]";

        Color invactive = GuiScreen.clickGui.settingsTextColor.get();

        interactRect = new FloatRect(rect.getX2() - 4 - IFont.getWidth(IFont.MONTSERRAT_BOLD, bindName, 6), rect.getCenteredY() - IFont.getHeight(IFont.MONTSERRAT_BOLD, bindName, 6) / 2 - 1, IFont.getWidth(IFont.MONTSERRAT_BOLD, bindName, 6), IFont.getHeight(IFont.MONTSERRAT_BOLD, bindName, 6) + 2);
        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, bindName, interactRect.getCenteredX(), interactRect.getCenteredY(), inject(invactive, alpha), 6);
        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, name, rect.getX() + 8, rect.getCenteredY(), inject(invactive, alpha), 6);
    }

    @Override
    public void click(double mx, double my, int button) {
        if (binding) {
            ((KeybindSetting) setting).set(90001 + button);
            binding = false;
        } else {
            if (interactRect.intersect(mx, my)) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && !binding) {
                    binding = true;
                }
                if (button == GLFW.GLFW_MOUSE_BUTTON_3) {
                    ((KeybindSetting) setting).set(((KeybindSetting) setting).getDefaultValue());
                }
            }
        }
    }

    @Override
    public void release(double mx, double my, int button) {

    }

    @Override
    public void key(int key) {
        if (binding) {
            if (key == GLFW.GLFW_KEY_DELETE) {
                ((KeybindSetting) setting).set(-1);
            } else {
                ((KeybindSetting) setting).set(key);
            }
            binding = false;
        }
        if (intersect && key == GLFW.GLFW_KEY_DELETE) {
            ((KeybindSetting) setting).set(-1);
        }
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
