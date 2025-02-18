package com.client.impl.function.movement.jesus;

import com.client.event.events.*;
import com.client.impl.function.movement.Jesus;
import com.client.system.function.FunctionManager;
import net.minecraft.client.MinecraftClient;

public class JesusMode {
    public final MinecraftClient mc;
    public final Jesus settings;

    public JesusMode() {
        this.settings = FunctionManager.get(Jesus.class);
        this.mc = MinecraftClient.getInstance();
    }

    public void tick(TickEvent.Pre e) {}
    public void tick(TickEvent.Post e) {}
    public void onTravel(PlayerTravelEvent e) {}
    public void onMove(PlayerMoveEvent e) {}
    public void sendMovementPackets(SendMovementPacketsEvent e) {}
    public void onPacket(PacketEvent.Receive e) {}
    public void onPacket(PacketEvent.Send e) {}
    public void onBlockState(BlockShapeEvent event) {}
    public void onEnable() {}
}
