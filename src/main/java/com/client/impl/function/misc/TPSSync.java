package com.client.impl.function.misc;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Timer;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.utils.math.TickRate;

public class TPSSync extends Function {
    public TPSSync() {
        super("TPS Sync", Category.MISC);
    }

    @Override
    public void tick(TickEvent.Post event) {
        Timer.setOverride((TickRate.getTickRate() >= 1 ? TickRate.getTickRate() : 1) / 20);
    }

    @Override
    public void onDisable() {
        Timer.setOverride(Timer.OFF);
    }
}
