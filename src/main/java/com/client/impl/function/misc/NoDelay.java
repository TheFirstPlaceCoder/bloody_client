package com.client.impl.function.misc;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import mixin.accessor.LivingEntityAccessor;
import mixin.accessor.MinecraftClientAccessor;

/**
 * __aaa__
 * 21.05.2024
 * */
public class NoDelay extends Function {
    public NoDelay() {
        super("No Delay", Category.MISC);
    }

    private final BooleanSetting jump = Boolean().name("Прыжки").defaultValue(true).build();
    private final BooleanSetting item = Boolean().name("Предметы").defaultValue(false).build();

    @Override
    public void tick(TickEvent.Pre event) {
        if (jump.get()) {
            ((LivingEntityAccessor) mc.player).setLastJumpCooldown(0);
        }
        if (item.get()) {
            ((MinecraftClientAccessor) mc).setItemUseCooldown(0);
        }
    }
}
