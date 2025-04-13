package com.client.impl.function.visual;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.ListSetting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.util.List;

public class Fullbright extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Ночное зрение", "Гамма")).defaultValue("Гамма").build();
    public final DoubleSetting gamma = Double().name("Яркость").enName("Gamma Value").defaultValue(3.0).min(0).max(3).visible(() -> mode.get().equals("Гамма")).build();

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
        if (mode.get().equals("Ночное зрение")) mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 500, 0, false, false));
        else mc.options.gamma = gamma.get();
    }
}
