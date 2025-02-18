package com.client.impl.function.movement.speedmodes.verus;

import com.client.event.events.SendMovementPacketsEvent;
import com.client.impl.function.movement.Timer;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.interfaces.IVec3d;
import com.client.utils.game.movement.MovementUtils;

public class VerusFast extends SpeedMode {
    public boolean bool, lastStopped;
    public int ticks;

    @Override
    public void onEnable() {
        bool = lastStopped = false;
        ticks = 0;
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        if (!MovementUtils.isMoving()) return;

        if (!(mc.player.forwardSpeed > 0)) {
            lastStopped = true;
            return;
        }

        if (mc.player.isOnGround()) {
            if (MovementUtils.getVelocitySpeed() > 0.3) lastStopped = false;

            event.onGround = (true);

            MovementUtils.strafe(0.41f);
            ((IVec3d) mc.player.getVelocity()).setY(0.42);
            Timer.setOverride(2.1F);

            ticks = 0;
        } else {
            if (ticks >= 10) {
                bool = true;
                MovementUtils.strafe(0.35F);
                return;
            }

            if (bool) {
                if (lastStopped) {
                    MovementUtils.strafe(0.2f);
                }
                else if (ticks <= 1) {
                    MovementUtils.strafe(0.35F);
                }
                else {
                    MovementUtils.strafe(0.69F - (ticks - 2F) * 0.019F);
                }
            }

            ((IVec3d) mc.player.getVelocity()).setY(0);
            Timer.setOverride(0.9F);

            event.onGround = (true);
            mc.player.setOnGround(true);
        }

        ticks++;
    }
}