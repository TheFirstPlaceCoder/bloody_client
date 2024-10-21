package com.client.impl.function.visual;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

/**
 * __aaa__
 * 26.05.2024
 * */
public class NightVision extends Function {
    public NightVision() {
        super("Night Vision", Category.VISUAL);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 500, 0, false, false));
    }

    @Override
    public void onDisable() {
        mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }
}
