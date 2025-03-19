package com.client.impl.function.movement;

import api.interfaces.EventHandler;
import com.client.event.events.KeyboardInputEvent;
import com.client.event.events.NoSlowEvent;
import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.entity.SelfUtils;
import com.client.utils.game.entity.ServerUtils;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.math.MsTimer;
import mixin.accessor.PlayerMoveC2SPacketAccessor;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NoSlow extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Grim", "Matrix", "Ванильный", "ReallyWorld", "Matrix 2.0")).defaultValue("Grim").build();
    private final BooleanSetting setSlot = Boolean().name("Свапать слот").enName("Swap Slot").defaultValue(true).visible(() -> mode.get().equals("Ванильный")).build();

    public NoSlow() {
        super("No Slow", Category.MOVEMENT);
    }

    @Override
    public void onNoSlowEvent(NoSlowEvent event) {
        if (SelfUtils.hasElytra() && mc.player.isFallFlying() || mc.player.isRiding() || !mc.player.isUsingItem() || !MovementUtils.isMoving())
            return;

        switch (mode.get()) {
            case "ReallyWorld" -> {
                label35:
                {
                    if (mc.player.getOffHandStack().getUseAction() != UseAction.BLOCK) {
                        if (mc.player.getOffHandStack().getUseAction() != UseAction.EAT) {
                            break label35;
                        }
                    }

                    if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
                        return;
                    }
                }

                if ((float) mc.player.getHungerManager().getFoodLevel() < 6.0F) {
                    if (mc.player.isSprinting()) {
                        return;
                    }
                }

                if (!mc.player.isSneaking()) {
                    if (!mc.player.isSwimming()) {
                        event.cancel();
                        if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
                            mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.OFF_HAND));
                        } else {
                            mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
                        }
                    }
                }
            }

            case "Grim" -> {
                if (mc.player.getActiveHand() == Hand.OFF_HAND) {
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot % 8 + 1));
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot % 7 + 2));
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot));
                } else if (mc.player.getActiveHand() == Hand.MAIN_HAND && (mc.player.getItemUseTime() <= 3 || mc.player.age % 2 == 0)) {
                    mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.OFF_HAND));
                }

                event.cancel();
            }

            case "Matrix" -> {
                if (mc.player.isOnGround() && !mc.options.keyJump.isPressed()) {
                    mc.player.setVelocity(mc.player.getVelocity().x * 0.3, mc.player.getVelocity().y, mc.player.getVelocity().z * 0.3);
                } else if (mc.player.fallDistance > 0.2f) {
                    mc.player.setVelocity(mc.player.getVelocity().x * 0.95f, mc.player.getVelocity().y, mc.player.getVelocity().z * 0.95f);
                }

                event.cancel();
            }

            default -> {
                if (setSlot.get())
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot));

                event.cancel();
            }
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mode.get().equals("Matrix 2.0")) {
            if (mc.player.isUsingItem() && MovementUtils.isMoving() && mc.player.fallDistance > 0.7) {
                mc.player.getVelocity().multiply(0.97, 1, 0.97);
            }
        }
    }

    @EventHandler
    public void onSendPacket(PacketEvent.Send event) {
        if (mode.get().equals("Matrix 2.0")) {
            if (event.packet instanceof PlayerMoveC2SPacket cPacketPlayer) {
                if (mc.player.isUsingItem() && MovementUtils.isMoving() && !mc.options.keyJump.isPressed()) {
                    ((PlayerMoveC2SPacketAccessor) cPacketPlayer).setY(mc.player.age % 2 == 0 ? ((PlayerMoveC2SPacketAccessor) cPacketPlayer).getY() + 0.0006 : ((PlayerMoveC2SPacketAccessor) cPacketPlayer).getY() + 0.0002);
                    ((PlayerMoveC2SPacketAccessor) cPacketPlayer).setOnGround(false);
                    mc.player.setOnGround(false);
                }
            }
        }
    }
}