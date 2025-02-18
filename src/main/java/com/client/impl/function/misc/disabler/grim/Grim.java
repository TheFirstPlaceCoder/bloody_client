package com.client.impl.function.misc.disabler.grim;

import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.misc.disabler.DisablerMode;
import com.client.utils.math.MsTimer;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Grim extends DisablerMode {
    List<Packet<?>> packets = new CopyOnWriteArrayList<>();
    MsTimer timer = new MsTimer();
    boolean sendPacket = false;

    @Override
    public void onDisable() {
        for (Packet<?> packet : packets) {
            mc.getNetworkHandler().sendPacket(packet);
        }
        packets.clear();
    }

    @Override
    public void onPacket(PacketEvent.Send e) {
        if (e.packet instanceof ClientCommandC2SPacket || sendPacket)
            return;
        packets.add(e.packet);
        e.cancel();
    }

    @Override
    public void tick(TickEvent.Pre e) {
        if (timer.passedMs(600L)) {
            for (Packet packet : packets) {
                sendPacket = true;
                mc.getNetworkHandler().sendPacket(packet);
                sendPacket = false;
            }
            packets.clear();
            timer.reset();
        }
    }
}
