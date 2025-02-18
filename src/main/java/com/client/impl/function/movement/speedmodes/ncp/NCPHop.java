package com.client.impl.function.movement.speedmodes.ncp;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Timer;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.utils.game.movement.MovementUtils;

public class NCPHop extends SpeedMode {
    @Override
    public void tick(TickEvent.Pre e) {
        if (mc.player.isTouchingWater() || mc.player.isInLava() ||
                mc.player.isClimbing() || mc.player.isRiding()) return;
        if (MovementUtils.isMoving() && mc.player.isOnGround()) {
            mc.player.jump();
            mc.player.flyingSpeed = 0.0223f;
        } else {
            Timer.setOverride(1);
        }
    }
}
