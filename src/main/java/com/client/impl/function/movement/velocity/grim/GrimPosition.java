package com.client.impl.function.movement.velocity.grim;

import com.client.event.events.PacketEvent;
import com.client.impl.function.movement.velocity.VelocityMode;
import mixin.accessor.PlayerMoveC2SPacketAccessor;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class GrimPosition extends VelocityMode {
    private boolean grimFlag;

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (e.packet instanceof EntityVelocityUpdateS2CPacket pac) {
            if (pac.getId() != mc.player.getEntityId()) return;

            e.cancel();
            mc.player.addVelocity(0.1, 0.1, 0.1);
        }
    }

    @Override
    public void onPacket(PacketEvent.Send e) {
        if (mc.player.hurtTime != 0) {
            this.grimFlag = true;
        }

        if (mc.player.isOnGround()) {
            this.grimFlag = false;
        }

        if (!grimFlag || !(e.packet instanceof PlayerMoveC2SPacket p)) return;

        ((PlayerMoveC2SPacketAccessor) p).setX(mc.player.getX() + 210);
        ((PlayerMoveC2SPacketAccessor) p).setZ(mc.player.getZ() + 210);
    }
}
