package com.client.impl.function.visual;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;

public class Fullbright extends Function {
    public final DoubleSetting gamma = Double().name("Яркость").enName("Gamma Value").defaultValue(3.0).min(0).max(3).build();

    public Fullbright() {
        super("Fullbright", Category.VISUAL);
    }

    public double prevGamma;

    @Override
    public void onEnable() {
        prevGamma = mc.options.gamma;
    }

    @Override
    public void onDisable() {
        mc.options.gamma = prevGamma;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        mc.options.gamma = gamma.get();
    }
}
