package com.client.impl.function.movement.velocity;

import com.client.event.events.*;
import com.client.impl.function.movement.Velocity;
import com.client.system.function.FunctionManager;
import net.minecraft.client.MinecraftClient;

public class VelocityMode {
    public final MinecraftClient mc;
    public final Velocity settings;

    public VelocityMode() {
        this.settings = FunctionManager.get(Velocity.class);
        this.mc = MinecraftClient.getInstance();
    }

    public void tick(TickEvent.Pre e) {}
    public void onPacket(PacketEvent.Receive e) {}
    public void onPacket(PacketEvent.Send e) {}
    public void onAttack(AttackEntityEvent.Pre event) {}
    public void onEnable() {}
}