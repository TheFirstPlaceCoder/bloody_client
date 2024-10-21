package com.client.impl.function.client;

import api.interfaces.EventHandler;
import com.client.clickgui.GuiScreen;
import com.client.event.events.KeyEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.*;
import com.client.system.setting.settings.theme.ThemeSetting;
import com.client.system.theme.ThemeManager;
import com.client.utils.auth.Loader;
import com.client.utils.color.Colors;
import com.client.utils.misc.InputUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;

public class ClickGui extends Function {
    public ClickGui() {
        super("Click Gui", Category.CLIENT);
        setKeyCode(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
    public final ListSetting enabledMode = List().name("Изменять при включении").list(List.of("Текст", "Обводка", "Оба")).defaultValue("Обводка").setPremium(true).build();
    public final BooleanSetting clientSound = Boolean().name("Звуки клиента").defaultValue(true).build();
    public final DoubleSetting volume = Double().name("Громкось").defaultValue(1d).min(0).max(1).build().visible(clientSound::get);
    private final ListSetting themeMode = List().name("Режим").list(List.of("Свой", "Клиент")).defaultValue("Клиент").setPremium(true).build();
    private final ThemeSetting themeSetting = Theme().name("Тема").defaultValue(ThemeManager.getThemeContainers().get(0)).setList(ThemeManager.getThemeContainers()).visible(() -> themeMode.get().equals("Клиент")).build();
    private final ColorSetting first = Color().name("Цвет 1").defaultValue(Color.RED).visible(() -> themeMode.get().equals("Свой")).build();
    private final ColorSetting second = Color().name("Цвет 2").defaultValue(Color.GREEN).visible(() -> themeMode.get().equals("Свой")).build();
    public final IntegerSetting speed = Integer().name("Скорость").defaultValue(30).min(10).max(50).build();

    public void updateColor() {
        if (themeMode.get().equals("Свой") && Loader.isPremium()) {
            Colors.setFirst(first.get());
            Colors.setSecond(second.get());
        } else {
            Colors.setFirst(themeSetting.get().first());
            Colors.setSecond(themeSetting.get().second());
        }
    }

    @Override
    public void onEnable() {
        GuiScreen.closeInvoke = false;
        mc.openScreen(GuiScreen.getInstance());
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (GuiScreen.closeInvoke || !(mc.currentScreen instanceof GuiScreen)) {
            toggle();
        }
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        if (event.key == GLFW.GLFW_KEY_ESCAPE && event.action.equals(InputUtils.Action.PRESS)) {
            GuiScreen.closeInvoke = true;
            event.cancel();
        }
    }

    @Override
    public void onDisable() {
        GuiScreen.closeInvoke = true;
    }
}