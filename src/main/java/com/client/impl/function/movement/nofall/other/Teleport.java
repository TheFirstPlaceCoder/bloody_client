package com.client.impl.function.movement.nofall.other;

import com.client.event.events.PacketEvent;
import com.client.impl.function.movement.nofall.NoFallMode;
import mixin.accessor.PlayerMoveC2SPacketAccessor;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Teleport extends NoFallMode {
    @Override
    public void onPacket(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket p && mc.player.fallDistance > 2.5 && mc.player.fallDistance < 50) {
            ((PlayerMoveC2SPacketAccessor) p).setOnGround(true);
            mc.player.setVelocity(0, -99.887575, 0);
            mc.player.input.sneaking = true;
        }
    }
}
