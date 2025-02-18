package com.client.impl.function.hud;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;

public class TargetHud extends Function {
    public final BooleanSetting particles = Boolean().name("Рисовать партиклы").enName("Draw Particles").defaultValue(true).build();

    public TargetHud() {
        super("Target", Category.HUD);
    }
}