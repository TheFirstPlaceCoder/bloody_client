package com.client.impl.function.movement.speedmodes.vulcan;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Timer;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.utils.game.movement.MovementUtils;

public class VulcanHop extends SpeedMode {
    @Override
    public void tick(TickEvent.Pre e) {
        if (mc.player.isInLava() || mc.player.isSubmergedInWater() || mc.player.isHoldingOntoLadder()) return;

        if (MovementUtils.isMoving()) {
            if (!mc.player.isOnGround() && mc.player.fallDistance > 2) {
                Timer.setOverride(Timer.OFF);
                return;
            }

            if (mc.player.isOnGround()) {
                mc.player.jump();
                if (mc.player.getVelocity().y > 0) {
                    Timer.setOverride(1.1453f);
                }
                MovementUtils.strafe(0.4815f);
            } else {
                if (mc.player.getVelocity().y < 0) {
                    Timer.setOverride(0.9185f);
                }
            }
        } else {
            Timer.setOverride(Timer.OFF);
        }
    }
}
