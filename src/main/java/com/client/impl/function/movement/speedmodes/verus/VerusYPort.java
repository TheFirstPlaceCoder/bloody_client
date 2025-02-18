package com.client.impl.function.movement.speedmodes.verus;

import com.client.event.events.PlayerMoveEvent;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.interfaces.IVec3d;
import com.client.utils.game.movement.MovementUtils;

public class VerusYPort extends SpeedMode {
    @Override
    public void onMove(PlayerMoveEvent event) {
        if (!MovementUtils.isMoving()) return;

        if (mc.player.isOnGround()) {
            ((IVec3d) event.movement).setY(0.42F);
            MovementUtils.strafe((float) (0.69F + MovementUtils.getSpeedEffect(0.1)));
            ((IVec3d) mc.player.getVelocity()).setY(0);
        } else {
            MovementUtils.strafe((float) (0.41F + MovementUtils.getSpeedEffect(0.055)));
        }

        mc.player.setSprinting(true);
    }
}
