package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ListSetting;

import java.util.List;

public class WaterSpeed extends Function {
    public WaterSpeed() {
        super("Water Speed", Category.MOVEMENT);
    }

    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("FunTime")).defaultValue("FunTime").build();

    @Override
    public void tick(TickEvent.Pre e) {
        if (mc.player.isSwimming()) {
            mc.player.getVelocity().multiply(1.01058F, 1, 1.01058F);
        }
    }
}