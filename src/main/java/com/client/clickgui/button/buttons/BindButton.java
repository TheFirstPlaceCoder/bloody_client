package com.client.clickgui.button.buttons;

import com.client.clickgui.GuiScreen;
import com.client.clickgui.button.SettingButton;
import com.client.impl.function.client.ClickGui;
import com.client.system.function.Function;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.misc.InputUtils;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class BindButton extends SettingButton {
    private final Function function;
    private FloatRect interactRect = new FloatRect();
    private boolean binding, intersect;

    public BindButton(Function function, FloatRect rect) {
        super(SettingManager.EMPTY_SETTING, rect);
        this.function = function;
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        intersect = rect.intersect(mx, my);
        int key = function.getKeyCode();
        String bindName = binding ? "..." : key >= 90000 ? InputUtils.getButtonName(key - 90001) : InputUtils.getKeyName(key);
        interactRect = new FloatRect(rect.getX2() - 8 - IFont.getWidth(IFont.MONTSERRAT_MEDIUM, bindName, 7), rect.getCenteredY() - IFont.getHeight(IFont.MONTSERRAT_MEDIUM, bindName, 7) / 2 - 1, IFont.getWidth(IFont.MONTSERRAT_MEDIUM, bindName, 7) + 4, IFont.getHeight(IFont.MONTSERRAT_MEDIUM, bindName, 7) + 2);
        GL.drawRoundedRect(interactRect, 2, inject(GuiScreen.BACK_OTHER, alpha));
        IFont.drawCenteredXY(IFont.MONTSERRAT_MEDIUM, bindName, interactRect.getCenteredX(), interactRect.getCenteredY(), inject(Color.WHITE, alpha), 7);
        IFont.drawCenteredY(IFont.MONTSERRAT_MEDIUM, "Бинд", rect.getX() + 4, rect.getCenteredY(), inject(Color.WHITE, alpha), 7);
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
