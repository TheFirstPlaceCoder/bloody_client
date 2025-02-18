package com.client.impl.function.movement.velocity.intave;

import com.client.event.events.AttackEntityEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.velocity.VelocityMode;
import com.client.interfaces.IVec3d;
import net.minecraft.util.hit.HitResult;

public class Intave extends VelocityMode {
    private boolean attacked;

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.crosshairTarget.getType().equals(HitResult.Type.ENTITY) && mc.player.hurtTime > 0 && !attacked) {
            ((IVec3d) mc.player.getVelocity()).setX(mc.player.getVelocity().getX() * settings.reducing.get());
            ((IVec3d) mc.player.getVelocity()).setZ(mc.player.getVelocity().getZ() * settings.reducing.get());

            mc.player.setSprinting(false);
        }

        attacked = false;
    }

    @Override
    public void onAttack(AttackEntityEvent.Pre event) {
        attacked = true;
    }
}
