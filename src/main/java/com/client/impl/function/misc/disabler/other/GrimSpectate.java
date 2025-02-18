package com.client.impl.function.misc.disabler.other;

import com.client.event.events.PacketEvent;
import com.client.impl.function.misc.disabler.DisablerMode;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class GrimSpectate extends DisablerMode {
    private HashSet<Packet<?>> packetQueue = new LinkedHashSet<>();
    private boolean delay = false;

    @Override
    public void onDisable() {
        packetQueue.forEach(e -> mc.getNetworkHandler().sendPacket(e));

        packetQueue.clear();
        delay = false;
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (mc.player.age < 20) {
            packetQueue.clear();
            return;
        }

        if (e.packet instanceof PlayerPositionLookS2CPacket) {
            // Should we start delaying?
            if (mc.player.abilities.flying && !delay) {
                delay = true;
            } else if (delay) {
                packetQueue.add(e.packet);
            }
        }

        if (delay) {
            // it's not really good idea to delay our packets for way too long, let's just release it after a while
            // idk this is fine for minemalia ig.
//                if (timer.hasElapsed(seconds.toLong() * 1000L)) {
//                    packetQueue.forEach() { handlePacket(it.packet) }
//                    packetQueue.clear()
//
//                    delay = false
//                    timer.reset()
//                }

            // delay transaction of course so the server will think we still have the flying ablities.
            if (e.packet instanceof QueryPongS2CPacket) {
                packetQueue.add(e.packet);
                e.cancel();

                // Prevent you from getting timed out, it will not work if your version is below 1.17
                mc.getNetworkHandler().sendPacket(new QueryPingC2SPacket(0));
            }
        }
    }
}
