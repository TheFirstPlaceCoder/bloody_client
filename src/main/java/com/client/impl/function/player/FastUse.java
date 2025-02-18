package com.client.impl.function.player;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Timer;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.movement.MovementUtils;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.List;

public class FastUse extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Timer", "Use Time")).defaultValue("Timer").build();
    public final DoubleSetting timerValue = Double().name("Значение Timer").enName("Timer").defaultValue(1.0).min(0.1).max(5).visible(() -> mode.get().equals("Timer")).build();
    public final IntegerSetting delay = Integer().name("Задержка").enName("Delay").defaultValue(0).min(0).max(10).visible(() -> mode.get().equals("Timer")).build();

    public final IntegerSetting itemUseTime = Integer().name("Время использования").enName("Use Time").defaultValue(15).min(0).max(20).visible(() -> mode.get().equals("Use Time")).build();

    private final ListSetting packetMode = List().name("Режим пакета").enName("Packet Mode").list(List.of("Look", "Position", "Full")).defaultValue("Full").build();
    public final IntegerSetting speed = Integer().name("Скорость использования").enName("Speed").defaultValue(20).min(1).max(35).build();
    public final BooleanSetting onlyStand = Boolean().name("Пауза при движении").enName("Only Stand").defaultValue(true).build();
    public final BooleanSetting onlyGround = Boolean().name("Пауза в воздухе").enName("Ground Only").defaultValue(true).build();

    public FastUse() {
        super("Fast Use", Category.PLAYER);
    }

    int ticks = 0;

    @Override
    public void tick(TickEvent.Pre event) {
        if (mode.get().equals("Timer")) {
            if (ticks <= 0) {
                Timer.setOverride(Timer.OFF);
            }

            if (shouldWork()) {
                if (ticks <= 0) {
                    Timer.setOverride(timerValue.get().floatValue());

                    ticks = delay.get() + 1;
                }

                if (ticks <= 1) {
                    for (int i = 0; i < speed.get(); i++) {
                        mc.getNetworkHandler().sendPacket(switch (packetMode.get()) {
                            case "Look" -> new PlayerMoveC2SPacket.LookOnly(mc.player.yaw, mc.player.pitch, mc.player.isOnGround());
                            case "Position" -> new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround());
                            default -> new PlayerMoveC2SPacket.Both(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.yaw, mc.player.pitch, mc.player.isOnGround());
                        });
                    }
                    mc.player.stopUsingItem();
                } else ticks--;
            }
        } else {
            if (shouldWork() && mc.player.getItemUseTime() >= itemUseTime.get()) {
                for (int i = 0; i < speed.get(); i++) {
                    mc.getNetworkHandler().sendPacket(switch (packetMode.get()) {
                        case "Look" -> new PlayerMoveC2SPacket.LookOnly(mc.player.yaw, mc.player.pitch, mc.player.isOnGround());
                        case "Position" -> new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround());
                        default -> new PlayerMoveC2SPacket.Both(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.yaw, mc.player.pitch, mc.player.isOnGround());
                    });
                }
                mc.player.stopUsingItem();
            }
        }
    }

    public boolean shouldWork() {
        return mc.player.isUsingItem() && (!onlyGround.get() || mc.player.isOnGround()) && (!onlyStand.get() || !MovementUtils.isMoving());
    }
}
