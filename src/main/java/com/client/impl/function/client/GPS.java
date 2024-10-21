package com.client.impl.function.client;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.gps.GpsManager;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.DoubleSetting;

import java.awt.*;

public class GPS extends Function {
    public ColorSetting color = Color().name("Цвет").defaultValue(Color.GREEN).build();
    public final DoubleSetting scaleGps = Double().name("Размер точки").defaultValue(5.0).min(0).max(20).build();
    public final DoubleSetting scaleText = Double().name("Размер текста").defaultValue(1.0).min(0).max(1).build();

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
}
