package com.client.impl.function.client;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.gps.GpsManager;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.utils.game.entity.PlayerUtils;

import java.awt.*;

public class GPS extends Function {
    public ColorSetting color = Color().name("Цвет").enName("Color").defaultValue(Color.GREEN).build();
    public final DoubleSetting scaleGps = Double().name("Размер точки").enName("Point Size").defaultValue(5.0).min(0).max(20).build();
    public final DoubleSetting scaleText = Double().name("Размер текста").enName("Point Text Size").defaultValue(1.0).min(0).max(1).build();

    public GPS() {
        super("GPS", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        GpsManager.show = true;
    }

    @Override
    public void onDisable() {
        GpsManager.show = false;
    }

    @Override
    public String getHudPrefix() {
        return GpsManager.get().isEmpty() ? "" : (String.format("%.1f", PlayerUtils.distanceTo(GpsManager.getCoords(GpsManager.get().get(0)))));
    }
}
