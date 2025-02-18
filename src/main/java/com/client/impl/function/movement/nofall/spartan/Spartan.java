package com.client.impl.function.movement.nofall.spartan;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.nofall.NoFallMode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Spartan extends NoFallMode {
    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player.fallDistance > 2f) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookOnly(mc.player.yaw, mc.player.pitch, true));
        }
    }
}
