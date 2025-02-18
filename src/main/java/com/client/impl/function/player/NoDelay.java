package com.client.impl.function.player;

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
        super("No Delay", Category.PLAYER);
    }

    private final BooleanSetting jump = Boolean().name("Прыжки").enName("Jumps").defaultValue(true).build();
    private final BooleanSetting item = Boolean().name("Предметы").enName("Items").defaultValue(false).build();

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
