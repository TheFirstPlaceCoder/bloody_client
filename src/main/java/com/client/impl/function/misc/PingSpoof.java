package com.client.impl.function.misc;

import com.client.event.events.PacketEvent;
import com.client.event.events.Render3DEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.math.MsTimer;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;

public class PingSpoof extends Function {
    public final IntegerSetting ping = Integer().name("Пинг").enName("Ping").defaultValue(100).min(1).max(300).build();

    public PingSpoof() {
        super("Ping Spoof", Category.MISC);
    }

    MsTimer timer = new MsTimer();

    KeepAliveC2SPacket cPacketKeepAlive = null;

    @Override
    public void onPacket(PacketEvent.Send event) {
        if(event.packet instanceof KeepAliveC2SPacket && cPacketKeepAlive != event.packet) {
            cPacketKeepAlive = (KeepAliveC2SPacket) event.packet;
            event.cancel();
            timer.reset();
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if(timer.passedMs(ping.get()) && cPacketKeepAlive != null) {
            mc.player.networkHandler.sendPacket(cPacketKeepAlive);
            cPacketKeepAlive = null;
        }
    }
}