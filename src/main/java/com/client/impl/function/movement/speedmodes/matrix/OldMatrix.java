package com.client.impl.function.movement.speedmodes.matrix;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Timer;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.utils.game.movement.MovementUtils;

public class OldMatrix extends SpeedMode {
    @Override
    public void tick(TickEvent.Pre e) {
        Timer.setOverride(Timer.OFF);
        if (mc.player.isTouchingWater() || mc.player.isInLava() ||
                mc.player.isClimbing() || mc.player.isRiding()) return;
        if (MovementUtils.isMoving()) {
            if (mc.player.isOnGround()) {
                mc.player.jump();
                mc.player.flyingSpeed = 0.02098f;
                Timer.setOverride(1.055f);
            }
        } else {
            Timer.setOverride(1);
        }
    }
}