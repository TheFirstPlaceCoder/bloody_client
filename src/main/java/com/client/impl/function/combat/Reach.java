package com.client.impl.function.combat;

import com.client.event.events.ReachEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;

public class Reach extends Function {
    public final DoubleSetting range = Double().name("Дистанция").defaultValue(6.5).min(0).max(7).build();

    public Reach() {
        super("Reach", Category.COMBAT);
    }

    @Override
    public void onReach(ReachEvent event) {
        event.distance = range.get().floatValue();
        event.cancel();
    }
}