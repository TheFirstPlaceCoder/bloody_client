package com.client.impl.function.visual;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.render.Renderer2D;

public class DamageTint extends Function {
    public final IntegerSetting health = Integer().name("Здоровье").defaultValue(13).min(1).max(20).build();
    public final DoubleSetting amplifier = Double().name("Насыщенность").defaultValue(1.0).min(0).max(10).build();

    public DamageTint() {
        super("Damage Tint", Category.VISUAL);
    }

    public void draw() {
        if (mc.player == null || mc.interactionManager == null || !isEnabled()) return;

        float threshold = health.get().floatValue();
        float power = amplifier.get().floatValue();
        if (mc.interactionManager.getCurrentGameMode().isSurvivalLike() && isEnabled()) Renderer2D.drawVignette(threshold, power);
    }
}
