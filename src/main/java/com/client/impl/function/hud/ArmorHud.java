package com.client.impl.function.hud;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;

public class ArmorHud extends Function {
    public final BooleanSetting mainHand = Boolean().name("Основная рука").enName("Main Hand").defaultValue(true).build();
    public final BooleanSetting offHand = Boolean().name("Левая рука").enName("Off Hand").defaultValue(true).build();

    public ArmorHud() {
        super("Armor", Category.HUD);
    }
}
