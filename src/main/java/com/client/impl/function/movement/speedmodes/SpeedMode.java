package com.client.impl.function.movement.speedmodes;

import com.client.event.events.*;
import com.client.impl.function.movement.Speed;
import com.client.system.function.FunctionManager;
import net.minecraft.client.MinecraftClient;

public class SpeedMode {
    public final MinecraftClient mc;
    public final Speed settings;

    public SpeedMode() {
        this.settings = FunctionManager.get(Speed.class);
        this.mc = MinecraftClient.getInstance();
    }

    public void tick(TickEvent.Pre e) {}
    public void tick(TickEvent.Post e) {}
    public void onTravel(PlayerTravelEvent e) {}
    public void onMove(PlayerMoveEvent e) {}
    public void sendMovementPackets(SendMovementPacketsEvent e) {}
    public void onPacket(PacketEvent.Receive e) {}
    public void onPacket(PacketEvent.Send e) {}
    public void onEnable() {}
    public void onDisable() {}
}
