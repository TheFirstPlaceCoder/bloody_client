package com.client.impl.function.movement.velocity.other;

import com.client.event.events.PacketEvent;
import com.client.impl.function.movement.velocity.VelocityMode;
import com.client.interfaces.IExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class Vanilla extends VelocityMode {
    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (e.packet instanceof EntityVelocityUpdateS2CPacket pac) {
            if (pac.getId() != mc.player.getEntityId()) return;
            e.setCancelled(true);
        }

        if (e.packet instanceof ExplosionS2CPacket explosion) {
            ((IExplosionS2CPacket) explosion).setVelocityX(0);
            ((IExplosionS2CPacket) explosion).setVelocityY(0);
            ((IExplosionS2CPacket) explosion).setVelocityZ(0);
        }
    }
}
