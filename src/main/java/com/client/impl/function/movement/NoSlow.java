package com.client.impl.function.movement;

import com.client.event.events.NoSlowEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.entity.SelfUtils;
import com.client.utils.game.movement.MovementUtils;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class NoSlow extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Old Grim", "New Grim", "Matrix", "Ванильный", "ReallyWorld", "FunTime Snow")).defaultValue("Grim").build();
    private final BooleanSetting setSlot = Boolean().name("Свапать слот").enName("Swap Slot").defaultValue(true).visible(() -> mode.get().equals("Ванильный")).build();

    public NoSlow() {
        super("No Slow", Category.MOVEMENT);
    }

    public int ticks = 0;

    @Override
    public void onNoSlowEvent(NoSlowEvent event) {
        if (SelfUtils.hasElytra() && mc.player.isFallFlying() || mc.player.isRiding() || !mc.player.isUsingItem() || !MovementUtils.isMoving())
            return;

        switch (mode.get()) {
            case "ReallyWorld" -> {
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, mc.player.getBlockPos().up(), Direction.NORTH));
                event.cancel();
            }

            case "Old Grim" -> {
                if (mc.player.getActiveHand() == Hand.OFF_HAND) {
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot % 8 + 1));
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot % 7 + 2));
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot));
                } else if (mc.player.getActiveHand() == Hand.MAIN_HAND && (mc.player.getItemUseTime() <= 3 || mc.player.age % 2 == 0)) {
                    mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.OFF_HAND));
                }

                event.cancel();
            }

            case "New Grim" -> {
                if (ticks >= 2) {
                    event.cancel();
                    ticks = 0;
                }
            }

            case "Matrix" -> {
                if (mc.player.isOnGround() && !mc.options.keyJump.isPressed()) {
                    mc.player.setVelocity(mc.player.getVelocity().x * 0.3, mc.player.getVelocity().y, mc.player.getVelocity().z * 0.3);
                } else if (mc.player.fallDistance > 0.2f) {
                    mc.player.setVelocity(mc.player.getVelocity().x * 0.95f, mc.player.getVelocity().y, mc.player.getVelocity().z * 0.95f);
                }

                event.cancel();
            }

            case "FunTime Snow" -> {
                if (mc.player.isOnGround() && (mc.world.getBlockState(new BlockPos(mc.player.getPos())).getBlock() instanceof CarpetBlock || mc.world.getBlockState(new BlockPos(mc.player.getPos())).getBlock() instanceof SnowBlock))
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
        if (mode.get().equals("New Grim") && mc.player != null && !mc.player.isFallFlying()) {
            if (mc.player.isUsingItem()) {
                ticks++;
            } else {
                ticks = 0;
            }
        }
    }
}