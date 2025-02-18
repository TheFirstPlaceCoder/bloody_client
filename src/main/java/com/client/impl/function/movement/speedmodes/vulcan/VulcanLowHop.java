package com.client.impl.function.movement.speedmodes.vulcan;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Timer;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.interfaces.IVec3d;
import com.client.utils.game.movement.MovementUtils;

public class VulcanLowHop extends SpeedMode {
    @Override
    public void tick(TickEvent.Pre e) {
        if (mc.player.isInLava() || mc.player.isSubmergedInWater() || mc.player.isHoldingOntoLadder()) return;

        if (MovementUtils.isMoving()) {
            if (!mc.player.isOnGround() && mc.player.fallDistance > 1.1) {
                Timer.setOverride(Timer.OFF);
                ((IVec3d) mc.player.getVelocity()).setY(-0.25);
                return;
            }

            if (mc.player.isOnGround()) {
                mc.player.jump();
                MovementUtils.strafe(0.4815f);
                Timer.setOverride(1.263f);
            } else if (mc.player.age % 4 == 0) {
                if (mc.player.age % 3 == 0) {
                    ((IVec3d) mc.player.getVelocity()).setY(-0.01 / mc.player.getVelocity().y);
                } else {
                    ((IVec3d) mc.player.getVelocity()).setY(-mc.player.getVelocity().y / mc.player.getY());
                }

                Timer.setOverride(0.8985f);
            }
        } else {
            Timer.setOverride(Timer.OFF);
        }
    }
}
