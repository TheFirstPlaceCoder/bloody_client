package com.client.impl.function.visual;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;

public class MotionBlur extends Function {
    public final IntegerSetting smoothness = Integer().name("Размытие").enName("Blur Strength").defaultValue(0).min(0).max(99).build();

    public MotionBlur() {
        super("Motion Blur", Category.VISUAL);
    }
}
