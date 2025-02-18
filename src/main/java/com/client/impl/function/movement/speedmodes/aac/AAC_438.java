package com.client.impl.function.movement.speedmodes.aac;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Timer;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.utils.game.movement.MovementUtils;

public class AAC_438 extends SpeedMode {
    @Override
    public void tick(TickEvent.Pre e) {
        Timer.setOverride(Timer.OFF);
        if (!MovementUtils.isMoving() || mc.player.isTouchingWater() || mc.player.isInLava() ||
                mc.player.isClimbing() || mc.player.isRiding()) return;

        if (mc.player.isOnGround())
            mc.player.jump();
        else {
            if (mc.player.fallDistance <= 0.1)
                Timer.setOverride(1.5f);
            else if (mc.player.fallDistance < 1.3)
                Timer.setOverride(0.7f);
            else
                Timer.setOverride(Timer.OFF);
        }
    }
}
