package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;

public class NoHunger extends Function {
    public NoHunger() {
        super("No Hunger", Category.MOVEMENT);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player.getHungerManager().getFoodLevel() <= 7.0F) {
            mc.player.getHungerManager().setFoodLevel(7);
            mc.player.getHungerManager().setSaturationLevel(20);
        }
    }
}
