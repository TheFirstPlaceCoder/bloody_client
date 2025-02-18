package com.client.clickgui.newgui.settings;

import com.client.clickgui.newgui.GuiScreen;
import com.client.impl.function.client.ClickGui;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.Utils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.misc.InputUtils;
import com.client.utils.render.wisetree.font.main.IFont;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class BindSettingElement extends AbstractSettingElement {
    private final Function function;
    private FloatRect interactRect = new FloatRect();
    private boolean binding, intersect;

    public BindSettingElement(Function function, FloatRect rect) {
        super(SettingManager.EMPTY_SETTING, rect);
        this.function = function;
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        intersect = rect.intersect(mx, my);
        int key = function.getKeyCode();
        String bindName = "[" + (binding ? "..." : key >= 90000 ? InputUtils.getButtonName(key - 90001) : key == -1 ? (Utils.isRussianLanguage ? "Нету" : "None") : InputUtils.getKeyName(key)) + "]";

        Color invactive = GuiScreen.clickGui.settingsTextColor.get();

        interactRect = new FloatRect(rect.getX2() - 4 - IFont.getWidth(IFont.MONTSERRAT_BOLD, bindName, 6), rect.getCenteredY() - IFont.getHeight(IFont.MONTSERRAT_BOLD, bindName, 6) / 2 - 1, IFont.getWidth(IFont.MONTSERRAT_BOLD, bindName, 6), IFont.getHeight(IFont.MONTSERRAT_BOLD, bindName, 6) + 2);
        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, bindName, interactRect.getCenteredX(), interactRect.getCenteredY(), inject(invactive, alpha), 6);
        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, Utils.isRussianLanguage ? "Бинд" : "Bind", rect.getX() + 8, rect.getCenteredY(), inject(invactive, alpha), 6);
    }

    @Override
    public void click(double mx, double my, int button) {
        if (binding) {
            function.setKeyCode(90001 + button);
            binding = false;
        } else {
            if (interactRect.intersect(mx, my)) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && !binding) {
                    binding = true;
                }
                if (button == GLFW.GLFW_MOUSE_BUTTON_3) {
                    function.setKeyCode(function instanceof ClickGui ? GLFW.GLFW_KEY_RIGHT_SHIFT : -1);
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
                function.setKeyCode(-1);
            } else {
                function.setKeyCode(key);
            }
            binding = false;
        }
        if (intersect && key == GLFW.GLFW_KEY_DELETE) {
            function.setKeyCode(-1);
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