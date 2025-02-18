package com.client.impl.function.misc.disabler.aac;

import com.client.event.events.PacketEvent;
import com.client.impl.function.misc.disabler.DisablerMode;
import mixin.accessor.PlayerMoveC2SPacketAccessor;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AAC_1910 extends DisablerMode {
    @Override
    public void onPacket(PacketEvent.Send e) {
        if (e.packet instanceof PlayerMoveC2SPacket p) {
            mc.getNetworkHandler().sendPacket(
                    new PlayerInputC2SPacket(
                            mc.player.input.movementSideways,
                            mc.player.input.movementForward,
                            mc.player.input.jumping,
                            mc.player.input.sneaking
                    )
            );
            ((PlayerMoveC2SPacketAccessor) p).setY(((PlayerMoveC2SPacketAccessor) p).getY() + (7.0E-9));
        }
    }
}
