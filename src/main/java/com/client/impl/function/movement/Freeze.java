package com.client.impl.function.movement;

import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Freeze extends Function {
    public Freeze() {
        super("Freeze", Category.MOVEMENT);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        mc.player.setPose(EntityPose.STANDING);
    }

    @Override
    public void onPacket(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket) {
            if (mc.player.age % 10 == 0) return;
            event.setCancelled(true);
        }
    }
}
