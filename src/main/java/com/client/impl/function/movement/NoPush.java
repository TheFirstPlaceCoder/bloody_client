package com.client.impl.function.movement;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;

public class NoPush extends Function {
    public final BooleanSetting entity = Boolean().name("Сущности").defaultValue(true).build();
    public final BooleanSetting blocks = Boolean().name("Блоки").defaultValue(true).build();
    public final BooleanSetting liquids = Boolean().name("Жидкости").defaultValue(true).build();

    public NoPush() {
        super("No Push", Category.MOVEMENT);
    }
}