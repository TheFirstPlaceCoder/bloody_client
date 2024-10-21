package com.client.impl.function.movement.blink;

import com.client.event.events.PacketEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import mixin.accessor.PlayerMoveC2SPacketAccessor;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class Blink extends Function {
    public Blink() {
        super("Blink", Category.MOVEMENT);
    }

    private final List<Runnable> packets = new ArrayList<>();

    private float yaw, pitch;

    @Override
    public void onEnable() {
        yaw = mc.player.yaw;
        pitch = mc.player.pitch;
        packets.clear();
    }

    @Override
    public void onDisable() {
        mc.openScreen(new EmptyScreen());
        for (Runnable runnable : packets) {
            runnable.run();
        }
        mc.openScreen(null);

        packets.clear();
    }

    @Override
    public void onPacket(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket movePacket) {
            event.cancel();
            packets.add(() -> {
                ((PlayerMoveC2SPacketAccessor) movePacket).setYaw(yaw);
                ((PlayerMoveC2SPacketAccessor) movePacket).setPitch(pitch);
                mc.player.networkHandler.sendPacket(movePacket);
            });
        }
    }
}
