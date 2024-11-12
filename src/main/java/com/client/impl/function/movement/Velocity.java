package com.client.impl.function.movement;

import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.interfaces.IEntityVelocityUpdateS2CPacket;
import com.client.interfaces.IExplosionS2CPacket;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ListSetting;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class Velocity extends Function {
    private final ListSetting mode = List().name("Режим").list(List.of("Matrix", "Grim", "New Grim", "HolyWorld", "Легитный", "Прыжок", "Ванильный")).defaultValue("Ванильный").build();
    public final BooleanSetting pauseInFluids = Boolean().name("Пауза в жидкостях").defaultValue(true).build();
    public final BooleanSetting fire = Boolean().name("Пауза в огне").defaultValue(true).build();

    public Velocity() {
        super("Velocity", Category.MOVEMENT);
    }

    private boolean flag;
    private int grimTicks, ccCooldown;
    boolean damaged;

    @Override
    public void onEnable() {
        grimTicks = 0;
        damaged = false;
        ccCooldown = 0;
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if(mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava()) && pauseInFluids.get())
            return;

        if(mc.player != null && mc.player.isOnFire() && fire.get() && (mc.player.hurtTime > 0)){
            return;
        }

        if (ccCooldown > 0) {
            ccCooldown--;
            return;
        }

        if (e.packet instanceof EntityVelocityUpdateS2CPacket pac) {
            if (pac.getId() == mc.player.getEntityId()) {
                switch (mode.get()) {
                    case "Matrix" -> {
                        if (!flag) {
                            e.setCancelled(true);
                            flag = true;
                        } else {
                            flag = false;
                            ((IEntityVelocityUpdateS2CPacket) pac).setX(((int) ((double) pac.getVelocityX() * -0.1)));
                            ((IEntityVelocityUpdateS2CPacket) pac).setZ(((int) ((double) pac.getVelocityZ() * -0.1)));
                        }
                    }
                    case "Ванильный" -> {
                        e.setCancelled(true);
                    }
                    case "Grim" -> {
                        e.setCancelled(true);
                        grimTicks = 6;
                    }
                    case "New Grim" -> {
                        e.setCancelled(true);
                        flag = true;
                    }
                    case "Легитный" -> {
                        e.setCancelled(true);
                        flag = true;
                        mc.options.keySneak.setPressed(true);
                    }
                    case "Прыжок" -> {
                        //e.setCancelled(true);
                        mc.player.jump();
                        //mc.player.setVelocity(0, -1, 0);
                    }
                    case "HolyWorld" -> {
                        ((IEntityVelocityUpdateS2CPacket) pac).setX((int) ((double) pac.getVelocityX() * 0.666f));
                        ((IEntityVelocityUpdateS2CPacket) pac).setZ((int) ((double) pac.getVelocityZ() * 0.666f));
                    }
                }
            }
        }

        if (e.packet instanceof ExplosionS2CPacket explosion) {
            switch (mode.get()) {
                case "Ванильный" -> {
                    ((IExplosionS2CPacket) explosion).setVelocityX(0);
                    ((IExplosionS2CPacket) explosion).setVelocityY(0);
                    ((IExplosionS2CPacket) explosion).setVelocityZ(0);
                }
                case "Новый Grim" -> {
                    e.setCancelled(true);
                    flag = true;
                }
            }
        }

        if (mode.get().equals("Grim")) {
            if (e.packet instanceof QueryPingC2SPacket && grimTicks > 0) {
                e.setCancelled(true);
                grimTicks--;
            }
        }

        if (e.packet instanceof PlayerPositionLookS2CPacket) {
            if (mode.get().equals("Новый Grim")) ccCooldown = 5;
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater()) && pauseInFluids.get())
            return;

        if (mode.get().equals("Matrix")) {
            if (mc.player.hurtTime > 0 && !mc.player.isOnGround()) {
                double var3 = mc.player.yaw * 0.017453292F;
                double var5 = Math.sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x + mc.player.getVelocity().z * mc.player.getVelocity().z);
                mc.player.setVelocity(-Math.sin(var3) * var5, mc.player.getVelocity().y, Math.cos(var3) * var5);
                mc.player.setSprinting(mc.player.age % 2 != 0);
            }
        } else if (mode.get().equals("New Grim")) {
            if (flag) {
                if(ccCooldown <= 0) {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Both(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.yaw, mc.player.pitch, mc.player.isOnGround()));
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.player.getPos().x, mc.player.getPos().y, mc.player.getPos().z), Direction.DOWN));
                }
                flag = false;
            }
        } else if (mode.get().equals("Легитный")) {
            if (flag) {
                mc.options.keySneak.setPressed(false);
                flag = false;
            }
        }

        if (grimTicks > 0)
            grimTicks--;
    }
}