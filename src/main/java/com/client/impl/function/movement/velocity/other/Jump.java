package com.client.impl.function.movement.velocity.other;

import com.client.event.events.PacketEvent;
import com.client.impl.function.movement.velocity.VelocityMode;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class Jump extends VelocityMode {
    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (e.packet instanceof EntityVelocityUpdateS2CPacket pac) {
            if (pac.getId() != mc.player.getEntityId()) return;

            if (settings.beforeJump.get()) e.cancel();
            mc.player.jump();
            if (!settings.beforeJump.get()) e.cancel();
        }
    }
}
