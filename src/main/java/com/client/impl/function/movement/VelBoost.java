package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.utils.game.movement.MovementUtils;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class VelBoost extends Function {
    public final DoubleSetting speed = Double().name("Speed").defaultValue(3.0).min(1).max(1.2).build();
    public final DoubleSetting jump = Double().name("Jump").defaultValue(3.0).min(1).max(6).build();
    public final BooleanSetting groundPacket = Boolean().name("groundPacket").defaultValue(true).build();
    public final BooleanSetting bypassPacket = Boolean().name("bypassPacket").defaultValue(true).build();
    public final BooleanSetting x = Boolean().name("X").defaultValue(true).visible(bypassPacket::get).build();
    public final BooleanSetting y = Boolean().name("Y").defaultValue(true).visible(bypassPacket::get).build();
    public final BooleanSetting z = Boolean().name("Z").defaultValue(true).visible(bypassPacket::get).build();

    public VelBoost() {
        super("Vel Boost", Category.MOVEMENT);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        Vec3d vec3d = mc.player.getVelocity();

        mc.player.setVelocity(vec3d.x * speed.get(), vec3d.y * jump.get(), vec3d.z * speed.get());
        if (groundPacket.get()) mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
        if (bypassPacket.get()) mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX() + (x.get() ? 32767 : 0), mc.player.getY() + (y.get() ? 32767 : 0), mc.player.getZ() + (z.get() ? 32767 : 0), mc.player.isOnGround()));
    }
}
