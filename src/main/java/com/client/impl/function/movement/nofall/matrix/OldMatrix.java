package com.client.impl.function.movement.nofall.matrix;

import com.client.event.events.SendMovementPacketsEvent;
import com.client.impl.function.movement.nofall.NoFallMode;
import com.client.utils.game.movement.MovementUtils;

public class OldMatrix extends NoFallMode {
    @Override
    public void sendMovementPackets(SendMovementPacketsEvent e) {
        if (mc.player.fallDistance > 2) {
            MovementUtils.strafe(0.19f);
        }

        if (mc.player.fallDistance > 3 && MovementUtils.getVelocitySpeed() < 0.2) {
            e.onGround = true;
            mc.player.fallDistance = 0;
        }
    }
}
