package com.client.impl.function.misc.disabler.other;

import com.client.event.events.SendMovementPacketsEvent;
import com.client.impl.function.misc.disabler.DisablerMode;
import com.client.utils.Utils;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Teleport extends DisablerMode {
    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        if (mc.player.age % settings.delay.get().intValue() == 0) {
            double x = event.x;
            double y = event.y;
            double z = event.z;

            switch (settings.teleportDirection.get()) {
                case "Up": {
                    y += Utils.random(1024, 2048);
                    break;
                }

                case "Down": {
                    y -= Utils.random(1024, 2048);
                    break;
                }

                case "Horizontal": {
                    x += Utils.random(1024, 2048);
                    z -= Utils.random(1024, 2048);
                    break;
                }
            }

            if (settings.mathGround.get()) {
                y = Math.round(y / 0.015625) * 0.015625;
            }

            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionOnly(x, y, z, settings.groundState.get()));
        }
    }
}
