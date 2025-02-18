package com.client.impl.function.movement.nofall;

import com.client.event.events.PacketEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.NoFall;
import com.client.system.function.FunctionManager;
import net.minecraft.client.MinecraftClient;

public class NoFallMode {
    public final MinecraftClient mc;
    public final NoFall settings;

    public NoFallMode() {
        this.settings = FunctionManager.get(NoFall.class);
        this.mc = MinecraftClient.getInstance();
    }

    public void tick(TickEvent.Pre e) {}
    public void onPacket(PacketEvent.Receive e) {}
    public void onPacket(PacketEvent.Send e) {}
    public void sendMovementPackets(SendMovementPacketsEvent e) {}
    public void onEnable() {}
}