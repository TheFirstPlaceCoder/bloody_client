package com.client.impl.function.movement.speedmodes.verus;

import com.client.event.events.SendMovementPacketsEvent;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.utils.game.movement.MovementUtils;

public class VerusHop extends SpeedMode {
    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        if (!MovementUtils.isMoving()) return;

        if (mc.player.isOnGround()) {
            mc.player.jump();
            MovementUtils.strafe((float) (0.55F + MovementUtils.getSpeedEffect(0.09)));
        } else {
            MovementUtils.strafe((float) (0.33F + MovementUtils.getSpeedEffect(0.084)));
        }

        if (!(mc.player.forwardSpeed > 0)) {
            MovementUtils.strafe((float) (0.3 + MovementUtils.getSpeedEffect(0.065)));
        }
    }
}
