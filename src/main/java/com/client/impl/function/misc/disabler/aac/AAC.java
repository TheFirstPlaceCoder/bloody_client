package com.client.impl.function.misc.disabler.aac;

import com.client.event.events.TickEvent;
import com.client.impl.function.misc.disabler.DisablerMode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AAC extends DisablerMode {
    @Override
    public void tick(TickEvent.Pre event) {
        if (!mc.isIntegratedServerRunning()) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Both(mc.player.getX(), mc.player.getY() - 1.0E159, mc.player.getZ() + 10.0, 0.0f, 0.0f, true));
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Both(mc.player.getX(), mc.player.getY(), mc.player.getZ(), 0.0f, 0.0f, true));
        }
    }
}
