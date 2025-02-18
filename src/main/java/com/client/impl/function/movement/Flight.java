package com.client.impl.function.movement;

import com.client.event.events.PacketEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.interfaces.IVec3d;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.movement.MovementUtils;
import mixin.accessor.PlayerMoveC2SPacketAccessor;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Random;

public class Flight extends Function {
    private final ListSetting mode = List().name("Тип полета").enName("Mode").list(List.of("Ванильный", "Матрикс", "Скольжение", "Лодка", "Прыжки", "Grim")).defaultValue("Ванильный").build();
    public final DoubleSetting horizontalSpeed = Double().name("Скорость").enName("Horizontal Speed").defaultValue(0.5).min(0).max(5).visible(() -> !mode.get().equals("Grim") && !mode.get().equals("Прыжки")).build();
    public final DoubleSetting verticalSpeed = Double().name("Скорость по Y").enName("Vertical Speed").defaultValue(0.5).min(0).max(5).visible(() -> !mode.get().equals("Grim") && !mode.get().equals("Прыжки")).build();
    private final BooleanSetting autoJump = Boolean().name("Авто Прыжки").enName("Auto Jump").defaultValue(true).visible(() -> mode.get().equals("Прыжки")).build();
    private final BooleanSetting sendOnGroundPacket = Boolean().name("Дополнительный пакет").enName("Ground Packet").defaultValue(true).visible(() -> mode.get().equals("Прыжки")).build();

    public Flight() {
        super("Flight", Category.MOVEMENT);
    }

    private boolean start = false;
    private int timer;

    private boolean sprintFlag, flyFlag, groundFlag, set;

    @Override
    public void onEnable() {
        start = false;
        timer = 0;

        groundFlag = false;
        flyFlag = false;
        sprintFlag = false;
    }

    @Override
    public void onDisable() {
        Timer.setOverride(Timer.OFF);
        if (!mc.player.isSpectator()) {
            mc.player.abilities.flying = false;
            mc.player.abilities.setFlySpeed(0.05f);
            if (mc.player.abilities.creativeMode) return;
            mc.player.abilities.allowFlying = false;
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null)
            return;

        switch (mode.get()) {
            case "Ванильный" -> {
                updatePlayerMotion();
            }

            case "Матрикс" -> {
                if (mc.player.isOnGround())
                    mc.player.jump();
                else {
                    MovementUtils.setMotion(Math.min(horizontalSpeed.get(), 1.97f));
                    double y = 0;
                    if (mc.options.keyJump.isPressed()) y += verticalSpeed.get();
                    if (mc.options.keySneak.isPressed()) y -= verticalSpeed.get();

                    ((IVec3d) mc.player.getVelocity()).setY(y);
                }
            }

            case "Скольжение" -> {
                mc.player.setVelocity(Vec3d.ZERO);
                MovementUtils.setMotion(horizontalSpeed.get());
                mc.player.setVelocity(mc.player.getVelocity().x, -0.003, mc.player.getVelocity().z);
            }

            case "Прыжки" -> {
                if (mc.options.keyJump.isPressed()) {
                    if (mc.player.isOnGround()) break;

                    mc.player.setOnGround(true);
                    if (!sendOnGroundPacket.get()) break;
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY(), mc.player.getZ(), true));
                    break;
                }
                if (!autoJump.get() || !(mc.player.getVelocity().getY() < -0.44) || mc.player.isOnGround()) break;
                mc.player.jump();
                if (!sendOnGroundPacket.get()) break;
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY(), mc.player.getZ(), true));
            }

            default -> { // GrimAC
            }
        }
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (mc.player == null || mc.world == null)
            return;

        if (mode.get().equals("Матрикс")) {
            if (e.packet instanceof PlayerPositionLookS2CPacket p) {
                if (mc.player == null)
                    toggle();
                mc.player.setPosition(p.getX(), p.getY(), p.getZ());
                mc.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(p.getTeleportId()));
                e.setCancelled(true);
                toggle();
            }
        }
    }

    @Override
    public void onPacket(PacketEvent.Send e) {
        if (mc.player == null || mc.world == null)
            return;

        if (mode.get().equals("Grim")) {
            if (e.packet instanceof PlayerMoveC2SPacket p) {
                ((PlayerMoveC2SPacketAccessor) p).setX(mc.player.getX() + 1000);
                ((PlayerMoveC2SPacketAccessor) p).setZ(mc.player.getZ() + 1000);
            }
        }
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
       if (mode.get().equals("Лодка")) {
            if (hasBoat()) {
                start = true;
                timer = 10;
                groundFlag = mc.player.isOnGround();
            }

            if (start) {
                timer--;

                if (timer <= 10) {
                    start = false;
                }

                event.pitch = -20;
                Vec3d motion = new Vec3d(0, 0, getSpeed(timer)).rotateX(-(float) Math.toRadians(-20)).rotateY(-(float) Math.toRadians(event.yaw));

                if (mc.options.keyLeft.isPressed()) motion = motion.rotateY((float) Math.toRadians(90));
                if (mc.options.keyRight.isPressed()) motion = motion.rotateY((float) -Math.toRadians(90));

                flyFlag = mc.player.abilities.flying;
                mc.player.abilities.flying = true;

                set = true;

                if (mc.player.isSprinting()) {
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                    sprintFlag = true;
                }

                mc.player.setVelocity(motion);
            }
        }
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent.Post event) {
        if (mode.get().equals("Лодка")) {
            if (set) {
                mc.player.abilities.flying = flyFlag;

                if (sprintFlag) {
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                    sprintFlag = false;
                }

                set = false;
            }
        }
    }

    private double getSpeed(int div) {
        boolean flag = mc.player.age % 2 == 0;
        return (flag ? 1.5F : 1.5F + new Random().nextDouble() * (groundFlag ? 0.13D : 0.084D)) * (div / 10F * 1D);
    }

    private boolean hasBoat() {
        return mc.world.getEntityCollisions(mc.player, mc.player.getBoundingBox().expand(0.4f), entity -> entity instanceof BoatEntity).findAny().isPresent();
    }

    private void updatePlayerMotion() {
        double motionY = getMotionY();

        MovementUtils.setMotion(horizontalSpeed.get());
        ((IVec3d) mc.player.getVelocity()).setY(motionY);
    }

    private double getMotionY() {
        return mc.options.keySneak.isPressed() ? -verticalSpeed.get()
                : mc.options.keyJump.isPressed() ? verticalSpeed.get() : 0;
    }

    @Override
    public String getHudPrefix() {
        return mode.get();
    }
}