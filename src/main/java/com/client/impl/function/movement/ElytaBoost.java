package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.entity.SelfUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ElytaBoost extends Function {
    private final ListSetting mode = List().name("Режим").list(List.of("Velocity", "Rotation Vector", "Cos Sin")).defaultValue("Velocity").build();

    public final DoubleSetting speed = Double().name("Скорость").defaultValue(3.0).min(1).max(5).build();

    public ElytaBoost() {
        super("Elyta Boost", Category.MOVEMENT);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (SelfUtils.hasElytra() && mc.player.isFallFlying() && mc.options.keyForward.isPressed()) {
            Vec3d vec3d = mc.player.getRotationVector();

            switch (mode.get()) {
                case "Velocity" -> {
                    mc.player.setVelocity(mc.player.getVelocity().x * speed.get(), mc.player.getVelocity().y, mc.player.getVelocity().z * speed.get());
                }

                case "Cos Sin" -> {
                    mc.player.addVelocity(-MathHelper.sin((float) Math.toRadians(mc.player.yaw)) * speed.get() / 10, 0, MathHelper.cos((float) Math.toRadians(mc.player.yaw)) * speed.get() / 10);
                }

                default -> {
                    mc.player.setVelocity(vec3d.x * speed.get(), mc.player.getVelocity().y, vec3d.z * speed.get());
                }
            }
        }
    }
}
