package com.client.impl.function.movement.velocity.aac;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.velocity.VelocityMode;
import com.client.interfaces.IVec3d;

public class AAC extends VelocityMode {
    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player.hurtTime > 0 && !mc.player.isOnGround()) {
            ((IVec3d) mc.player.getVelocity()).setX(mc.player.getVelocity().getX() * settings.reducing.get());
            ((IVec3d) mc.player.getVelocity()).setZ(mc.player.getVelocity().getZ() * settings.reducing.get());
        }
    }
}
