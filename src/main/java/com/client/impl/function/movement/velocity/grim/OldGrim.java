package com.client.impl.function.movement.velocity.grim;

import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.velocity.VelocityMode;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;

public class OldGrim extends VelocityMode {
    private int grimTicks;

    @Override
    public void onEnable() {
        grimTicks = 0;
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (e.packet instanceof EntityVelocityUpdateS2CPacket pac) {
            if (pac.getId() != mc.player.getEntityId()) return;

            e.cancel();
            grimTicks = 6;
        } else if (e.packet instanceof QueryPongS2CPacket && grimTicks > 0) {
            e.cancel();

            grimTicks--;
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (grimTicks > 0)
            grimTicks--;
    }
}
