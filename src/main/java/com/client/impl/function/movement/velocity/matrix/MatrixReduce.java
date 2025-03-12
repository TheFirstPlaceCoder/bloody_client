package com.client.impl.function.movement.velocity.matrix;

import com.client.event.events.PacketEvent;
import com.client.impl.function.movement.velocity.VelocityMode;
import com.client.interfaces.IEntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class MatrixReduce extends VelocityMode {
    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (e.packet instanceof EntityVelocityUpdateS2CPacket pac) {
            if (pac.getId() != mc.player.getEntityId()) return;

            ((IEntityVelocityUpdateS2CPacket) pac).setX(((int) ((double) pac.getVelocityX() * 0.33)));
            ((IEntityVelocityUpdateS2CPacket) pac).setZ(((int) ((double) pac.getVelocityZ() * 0.33)));

            if (mc.player.isOnGround()) {
                ((IEntityVelocityUpdateS2CPacket) pac).setX(((int) ((double) pac.getVelocityX() * 0.86)));
                ((IEntityVelocityUpdateS2CPacket) pac).setZ(((int) ((double) pac.getVelocityZ() * 0.86)));
            }
        }
    }
}