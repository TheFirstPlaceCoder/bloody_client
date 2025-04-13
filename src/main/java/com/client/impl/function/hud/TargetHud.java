package com.client.impl.function.hud;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ListSetting;

import java.util.List;

public class TargetHud extends Function {
    public final ListSetting barMode = List().name("Режим бара").enName("Bar Mode").list(List.of("Здоровье", "Клиентский")).defaultValue("Здоровье").build();
    public final BooleanSetting blur = Boolean().name("Блюр бара").enName("Bar Blur").defaultValue(true).build();
    public final BooleanSetting particles = Boolean().name("Рисовать партиклы").enName("Draw Particles").defaultValue(true).build();

    public TargetHud() {
        super("Target", Category.HUD);
    }
}