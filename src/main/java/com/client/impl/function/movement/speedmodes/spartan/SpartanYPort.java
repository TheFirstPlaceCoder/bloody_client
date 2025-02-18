package com.client.impl.function.movement.speedmodes.spartan;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Timer;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.interfaces.IVec3d;

import java.util.Random;

public class SpartanYPort extends SpeedMode {
    private int airMoves = 0;

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.options.keyForward.isPressed()) {
            if (mc.player.isOnGround()) {
                mc.player.jump();
                airMoves = 0;
            } else {
                Timer.setOverride(1.08f);
                if (airMoves >= 3) mc.player.flyingSpeed = 0.0275f;
                if (airMoves >= 4 && airMoves % 2 == 0) {
                    ((IVec3d) mc.player.getVelocity()).setY(-0.32 - new Random().nextDouble(0.009));
                    mc.player.flyingSpeed = 0.0238f;
                }
                airMoves++;
            }
        }
    }
}
