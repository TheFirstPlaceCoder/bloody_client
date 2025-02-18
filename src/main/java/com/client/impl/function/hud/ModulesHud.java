package com.client.impl.function.hud;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;

import java.awt.*;
import java.util.List;

public class ModulesHud extends Function {
    public final IntegerSetting steps = Integer().name("Оффсеты").enName("Color Steps").defaultValue(5).min(0).max(16).build();
    public final BooleanSetting drawBackground = Boolean().name("Рисовать бекграунд").enName("Draw Background").defaultValue(true).build();
    public final BooleanSetting line = Boolean().name("Рисовать линию").enName("Draw Line").defaultValue(true).build();
    public ColorSetting lineColor = Color().name("Цвет линии").enName("Line Color").defaultValue(Color.BLACK).visible(line::get).build();

    public ModulesHud() {
        super("Function List", Category.HUD);
    }
}