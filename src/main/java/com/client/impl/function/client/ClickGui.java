package com.client.impl.function.client;

import api.interfaces.EventHandler;
import com.client.clickgui.newgui.GuiScreen;
import com.client.event.events.KeyEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.*;
import com.client.utils.Utils;
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

    public final ListSetting languageMode = List().name("Язык").enName("Language").list(List.of("Русский", "English")).defaultValue("Русский").callback(e -> Utils.isRussianLanguage = e.equals("Русский")).build();
    public final BooleanSetting clientSound = Boolean().name("Звуки клиента").enName("Client Sounds").defaultValue(true).build();
    public final DoubleSetting volume = Double().name("Громкось").enName("Volume").defaultValue(1d).min(0).max(1).build().visible(clientSound::get);
    private final ListSetting themeMode = List().name("Режим цвета").enName("Color Mode").list(List.of("Rainbow", "Astolfo", "Свой")).defaultValue("Свой").callback(e -> this.updateColor()).build();
    private final ColorSetting first = Color().name("Цвет 1").enName("Color 1").defaultValue(Color.RED).visible(() -> themeMode.get().equals("Свой")).callback(e -> this.updateColor()).build();
    private final ColorSetting second = Color().name("Цвет 2").enName("Color 2").defaultValue(Color.GREEN).visible(() -> themeMode.get().equals("Свой")).callback(e -> this.updateColor()).build();
    public final IntegerSetting speed = Integer().name("Скорость").enName("Gradient Speed").defaultValue(30).min(10).max(50).build();
    public final IntegerSetting scrollSpeed = Integer().name("Скорость скроллинга").enName("GUI Scroll Speed").defaultValue(2).min(0).max(30).build();

    public final ListSetting sortMode = List().name("Режим сортировки").enName("Sort Mode").list(List.of("По размеру", "По порядку")).defaultValue("По размеру").build();
    public final ListSetting centerMode = List().name("Позиционирование категорий").enName("Category Mode").list(List.of("Лево", "Центр", "Право")).defaultValue("Центр").build();
    public final ListSetting modulesMode = List().name("Позиционирование модулей").enName("Module Mode").list(List.of("Лево", "Центр", "Право")).defaultValue("Центр").build();
    public final ListSetting outlineMode = List().name("Режим обводки").enName("Outline Mode").list(List.of("Обычная", "Свечение", "Оба")).defaultValue("Оба").build();
    public final IntegerSetting opacity = Integer().name("Непрозрачность обычной").enName("Opacity").defaultValue(70).min(0).max(100).build();
    public final BooleanSetting drawBackground = Boolean().name("Рисовать бекграунд").enName("GUi Background").defaultValue(true).build();
    public final ColorSetting backgroundColor = Color().name("Цвет бекграунда").enName("Background Color").defaultValue(Color.BLACK).visible(drawBackground::get).build();
    public final ColorSetting categoryColor = Color().name("Цвет категорий").enName("Categories Color").defaultValue(new Color(26, 25, 25, 255)).build();
    public final ColorSetting categoryTextColor = Color().name("Цвет текста названий").enName("Categories Text Color").defaultValue(new Color(185, 185, 185, 255)).build();
    public final ColorSetting modulesTextColor = Color().name("Цвет текста модулей").enName("Modules Text Color").defaultValue(new Color(118, 118, 118, 255)).build();
    public final ColorSetting selectedModulesColor = Color().name("Цвет выделения модулей").enName("Select Quad Color").defaultValue(new Color(45, 45, 45,200)).build();
    public final ColorSetting selectedModulesTextColor = Color().name("Цвет текста выделенных модулей").enName("Selected Modules Color").defaultValue(Color.WHITE).build();
    public final ColorSetting settingsTextColor = Color().name("Главный цвет настроек").enName("Settings Color").defaultValue(Color.WHITE.darker()).build();
    public final ColorSetting settingsElementsTextColor = Color().name("Цвет элементов настроек").enName("Settings Elements Color").defaultValue(Color.WHITE).build();

    public void updateColor() {
        if (themeMode.get().equals("Свой")) {
            Colors.isRainbow = false;
            Colors.isAstolfo = false;
            Colors.setFirst(first.get());
            Colors.setSecond(second.get());
        } else {
            Colors.isAstolfo = themeMode.get().equals("Astolfo");
            Colors.isRainbow = themeMode.get().equals("Rainbow");
        }

        Colors.speed = speed.get();
    }

    @Override
    public void onEnable() {
        updateColor();
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