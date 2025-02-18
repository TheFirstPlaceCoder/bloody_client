package com.client.impl.function.movement.nofall.vulcan;

import com.client.event.events.PacketEvent;
import com.client.impl.function.movement.nofall.NoFallMode;
import com.client.interfaces.IVec3d;
import mixin.accessor.PlayerMoveC2SPacketAccessor;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Vulcan extends NoFallMode {
    @Override
    public void onPacket(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket p && mc.player.fallDistance > 7.0) {
            ((PlayerMoveC2SPacketAccessor) p).setOnGround(true);
            mc.player.fallDistance = 0f;
            ((IVec3d) mc.player.getVelocity()).setY(0);
        }
    }
}
