package com.client.clickgui.button.buttons;

import com.client.clickgui.GuiScreen;
import com.client.clickgui.StringWriteStack;
import com.client.clickgui.button.SettingButton;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.StringSetting;
import com.client.utils.math.Timer;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.client.system.function.Function.mc;

public class StringButton extends SettingButton {
    private String override;
    private boolean write;
    private FloatRect textRect;
    private final List<String> z_stack = new ArrayList<>();
    private int z_index;
    private final List<String> y_stack = new ArrayList<>();
    private int y_index;
    private final Timer dot = new Timer();

    public StringButton(AbstractSettings<?> setting, FloatRect rect) {
        super(setting, rect);
        this.textRect = new FloatRect(rect.getX() + 4, rect.getY() + IFont.getHeight(IFont.MONTSERRAT_MEDIUM, setting.getName(), 7) + 3, rect.getW() - 8, 0);
        this.override = clampString(((StringSetting) setting).get());
        this.textRect.setH(IFont.getHeight(IFont.MONTSERRAT_MEDIUM, override, 7) + 2);
    }

    @Override
    public float getAdd() {
        return getH() + 6;
    }

    @Override
    public float getH() {
        return rect.getH() + textRect.getH();
    }

    private String clampString(String in) {
        float x = rect.getX() + 6;
        StringBuilder f = new StringBuilder();

        for (char c : in.toCharArray()) {
            x += IFont.getWidth(IFont.MONTSERRAT_MEDIUM, "" + c, 7);
            if (x >= rect.getX2() - 6) {
                f.append("\n");
                x = rect.getX() + 6;
            }
            f.append(c);
        }

        return f.toString();
    }

    private String getOverride() {
        StringBuilder f = new StringBuilder();
        for (char c : override.toCharArray()) {
            if (c == '\n') continue;
            f.append(c);
        }
        return f.toString();
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        dot.tick();
        dot.resetIfPassed(20);
        ((StringSetting) setting).set(getOverride());
        override = clampString(((StringSetting)setting).get());
        this.textRect = new FloatRect(rect.getX() + 4, rect.getY() + IFont.getHeight(IFont.MONTSERRAT_MEDIUM, setting.getName(), 7) + 3, rect.getW() - 8, IFont.getHeight(IFont.MONTSERRAT_MEDIUM, override, 7) + 2);
        IFont.draw(IFont.MONTSERRAT_MEDIUM, setting.getName(), rect.getX() + 4, rect.getY() + 2, inject(Color.WHITE, alpha), 7);

        GL.drawRoundedRect(textRect, 2, inject(GuiScreen.BACK_OTHER, alpha));
        IFont.draw(IFont.MONTSERRAT_MEDIUM, override + (write && dot.passed(10) ? "." : ""), textRect.getX() + 2, textRect.getY() + 1, inject(Color.LIGHT_GRAY, alpha), 7);
    }

    @Override
    public void click(double mx, double my, int button) {
        if (textRect.intersect(mx, my) && button == GLFW.GLFW_MOUSE_BUTTON_1 && StringWriteStack.test(this)) {
            write = true;
            StringWriteStack.setCurrent(this);
        }
        if (rect.intersect(mx, my) && button == GLFW.GLFW_MOUSE_BUTTON_3) {
            ((StringSetting) setting).set(((StringSetting) setting).getDefaultValue());
        }
    }

    @Override
    public void release(double mx, double my, int button) {

    }

    @Override
    public void key(int key) {
        if (write) {
            if (key == GLFW.GLFW_KEY_BACKSPACE && !override.isEmpty()) {
                y_index = 0;
                z_index = 0;
                z_stack.add(override);
                override = GuiScreen.getStringIgnoreLastChar(override);
            }

            if (key == GLFW.GLFW_KEY_ENTER) {
                write = false;
                StringWriteStack.setCurrent(null);
            }
            if (key == GLFW.GLFW_KEY_C && Screen.hasControlDown() && !((StringSetting) setting).get().isEmpty()) {
                mc.keyboard.setClipboard(((StringSetting) setting).get());
            }
            if (key == GLFW.GLFW_KEY_V && Screen.hasControlDown() && !mc.keyboard.getClipboard().isEmpty()) {
                y_index = 0;
                z_index = 0;
                z_stack.add(override);
                override += mc.keyboard.getClipboard();
            }
            if (key == GLFW.GLFW_KEY_Z && Screen.hasControlDown() && !z_stack.isEmpty()) {
                y_index = 0;
                y_stack.add(override);
                z_index = z_stack.size() - 1;
                override = z_stack.get(z_index);
                z_stack.remove(z_index);
            }
            if (key == GLFW.GLFW_KEY_Y && Screen.hasControlDown() && !y_stack.isEmpty()) {
                z_index = 0;
                z_stack.add(override);
                y_index = y_stack.size() - 1;
                override = y_stack.get(y_index);
                y_stack.remove(y_index);
            }
        }
    }

    @Override
    public void symbol(char chr) {
        if (write) {
            y_index = 0;
            z_index = 0;
            z_stack.add(override);
            override += chr;
        }
    }

    @Override
    public void scroll(double mx, double my, double amount) {

    }

    @Override
    public void close() {
        StringWriteStack.setCurrent(null);
    }
}
