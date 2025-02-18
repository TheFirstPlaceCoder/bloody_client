package com.client.clickgui.newgui.settings;

import com.client.clickgui.newgui.GuiScreen;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.settings.StringSetting;
import com.client.utils.Utils;
import com.client.utils.math.Timer;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.client.BloodyClient.mc;

public class StringSettingElement extends AbstractSettingElement {
    private String override;
    private boolean write;
    private FloatRect textRect;
    private final List<String> z_stack = new ArrayList<>();
    private int z_index;
    private final List<String> y_stack = new ArrayList<>();
    private int y_index;
    private final Timer dot = new Timer();
    public String name;

    public StringSettingElement(AbstractSettings<?> setting, FloatRect rect) {
        super(setting, rect);
        this.textRect = new FloatRect(rect.getX() + 8, rect.getY() + IFont.getHeight(IFont.MONTSERRAT_MEDIUM, setting.getName(), 6) + 3, rect.getW() - 16, 0);
        this.override = clampString(((StringSetting) setting).get());
        this.textRect.setH(IFont.getHeight(IFont.MONTSERRAT_MEDIUM, override, 6) + 2);
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
    public float getAdd() {
        return rect.getH() + textRect.getH() + 2;
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        dot.tick();
        dot.resetIfPassed(20);
        ((StringSetting) setting).set(getOverride());
        override = clampString(((StringSetting)setting).get());
        this.textRect = new FloatRect(rect.getX() + 8, rect.getY() + IFont.getHeight(IFont.MONTSERRAT_MEDIUM, Utils.isRussianLanguage || setting.getEnName() == null || setting.getEnName().isEmpty() ? setting.getName() : setting.getEnName(), 6) + 1, rect.getW() - 16, IFont.getHeight(IFont.MONTSERRAT_MEDIUM, override, 6) + 2);
        Color invactive = GuiScreen.clickGui.settingsTextColor.get();
        IFont.draw(IFont.MONTSERRAT_MEDIUM, Utils.isRussianLanguage || setting.getEnName() == null || setting.getEnName().isEmpty() ? setting.getName() : setting.getEnName(), rect.getX() + 8, rect.getY(), inject(invactive, alpha), 6);

        GL.drawRoundedRect(textRect, 2, inject(GuiScreen.clickGui.categoryColor.get().darker().darker(), alpha));
        IFont.draw(IFont.MONTSERRAT_MEDIUM, override + (write && dot.passed(10) ? "|" : ""), textRect.getX() + 2, textRect.getY() + 1, inject(write ? Color.WHITE : new Color(162, 162, 162), alpha), 6);
    }

    @Override
    public void click(double mx, double my, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            write = textRect.intersect(mx, my);
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
                override = Utils.getStringIgnoreLastChar(override);
            }

            if (key == GLFW.GLFW_KEY_ENTER) {
                write = false;
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
    }
}
