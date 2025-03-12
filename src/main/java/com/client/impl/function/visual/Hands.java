package com.client.impl.function.visual;

import com.client.BloodyClient;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.color.ColorUtils;

import java.awt.*;
import java.util.List;

public class Hands extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Нормальный", "Градиент")).defaultValue("Градиент").build();
    public final IntegerSetting lineWidth = Integer().name("Ширина обводки").enName("Outline Width").defaultValue(5).min(1).max(10).build();
    public final ColorSetting colorSetting = Color().name("Цвет").enName("Color").defaultValue(ColorUtils.injectAlpha(Color.CYAN, 60)).visible(() -> !mode.get().equals("Градиент")).build();
    public final DoubleSetting speed = Double().name("Скорость переливания").enName("Gradient Speed").defaultValue(5.0).min(0).max(10).visible(() -> mode.get().equals("Градиент")).build();
    public final IntegerSetting fillOpacity = Integer().name("Непрозрачность").enName("Opacity").defaultValue(60).min(0).max(100).visible(() -> mode.get().equals("Градиент")).build();

    public Hands() {
        super("Hands", Category.VISUAL);
    }

    @Override
    public void onDisable() {
        BloodyClient.shaderManager.reloadShaders();
    }

    public int getIndexOfMode() {
        return mode.get().equals("Нормальный") ? 0 : 1;
    }

    public float getOpacity() {
        if (mode.get().equals("Градиент")) return (fillOpacity.get().floatValue() * 2.55f) / 255f;
        else return (colorSetting.get().getAlpha() / 255f);
    }

    @Override
    public String getHudPrefix() {
        return mode.get();
    }
}
