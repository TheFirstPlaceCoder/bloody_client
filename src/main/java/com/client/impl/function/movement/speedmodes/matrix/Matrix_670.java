package com.client.impl.function.movement.speedmodes.matrix;

import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.interfaces.IVec3d;
import com.client.utils.game.movement.MovementUtils;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class Matrix_670 extends SpeedMode {
    int noVelocityY;

    @Override
    public void tick(TickEvent.Pre e) {
        if (!mc.player.isOnGround() && noVelocityY <= 0) {
            if (mc.player.getVelocity().y > 0) {
                mc.player.getVelocity().add(0, -0.0005, 0);
            }
            mc.player.getVelocity().add(0, -0.0094001145141919810, 0);
        }
        if (!mc.player.isOnGround() && noVelocityY < 8) {
            if (MovementUtils.getSpeed() < 0.2177 && noVelocityY < 8) {
                MovementUtils.strafe(0.2177f);
            }
        }
        if (Math.abs(mc.player.flyingSpeed) < 0.1) {
            mc.player.flyingSpeed = 0.026f;
        }
        else {
            mc.player.flyingSpeed = 0.0247f;
        }
        if (mc.player.isOnGround() && MovementUtils.isMoving()) {
            mc.options.keyJump.setPressed(false);
            mc.player.jump();
            IVec3d v = (IVec3d) mc.player.getVelocity();
            v.setY(0.41050001145141919810);
            if (Math.abs(mc.player.flyingSpeed) < 0.1) {
                MovementUtils.strafe(MovementUtils.getSpeed());
            }
        }
        if (!MovementUtils.isMoving()) {
            IVec3d v = (IVec3d) mc.player.getVelocity();
            v.setXZ(0, 0);
        }
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof EntityVelocityUpdateS2CPacket velocity) {
            if (mc.player != null && mc.world != null && mc.world.getEntityById(velocity.getId()) != null) {
                if (mc.player == mc.world.getEntityById(velocity.getId()))
                    noVelocityY = 10;
            }
        }
    }
}
