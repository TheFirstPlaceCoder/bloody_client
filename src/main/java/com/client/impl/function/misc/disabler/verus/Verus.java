package com.client.impl.function.misc.disabler.verus;

import com.client.event.events.PacketEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.impl.function.misc.disabler.DisablerMode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class Verus extends DisablerMode {
    private boolean teleported;

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        if (mc.player.age % 100 == 0) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), -0.015625, mc.player.getZ(), false));
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));

            teleported = true;
        }
    }

    @Override
    public void onPacket(PacketEvent.Receive receive) {
        if (receive.packet instanceof PlayerPositionLookS2CPacket) {
            if (teleported) {
                receive.cancel();
            }

            teleported = false;
        }
    }
}
