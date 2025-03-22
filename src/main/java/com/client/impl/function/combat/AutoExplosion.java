package com.client.impl.function.combat;

import com.client.event.events.EntityEvent;
import com.client.event.events.PacketEvent;
import com.client.event.events.PlaceBlockEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.misc.TaskTransfer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoExplosion extends Function {
    private final IntegerSetting delay = Integer().name("Задержка").enName("Delay").defaultValue(1).min(1).max(5).build();
    public final IntegerSetting swapDelay = Integer().name("Задержка свапа").enName("Swap Delay").defaultValue(2).min(0).max(6).build();
    private final BooleanSetting matrixBypass = Boolean().name("Обход Matrix").enName("Matrix Bypass").defaultValue(false).build();
    private final BooleanSetting excludeHotbar = Boolean().name("Не оставлять в хотбаре").enName("Exclude Hotbar").defaultValue(true).build();

    public AutoExplosion() {
        super("Auto Explosion", Category.COMBAT);
    }

    private BlockPos last;
    private int id;
    private int time;
    private final TaskTransfer taskTransfer = new TaskTransfer();
    private int prev;
    private boolean shouldSwap = false, afterSwap = false;

    @Override
    public void onEnable() {
        shouldSwap = false;
        afterSwap = false;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        taskTransfer.handle();

        if (shouldSwap) {
            mc.player.inventory.selectedSlot = prev;
            shouldSwap = false;
        }

        if (time > 0) time--;
        else {
            if (id != -1 && mc.world.getEntityById(id) != null) {
                Entity entity = mc.world.getEntityById(id);
                if (!PlayerUtils.isInRange(entity, mc.interactionManager.getReachDistance())) return;
                mc.interactionManager.attackEntity(mc.player, entity);
                mc.player.swingHand(Hand.MAIN_HAND);
                id = -1;
            }
        }
    }

    @Override
    public void placeBlock(PlaceBlockEvent.Post event) {
        last = event.pos;
        place(event.pos, event.hit, event.direction);
    }

    @Override
    public void addEntity(EntityEvent.Add event) {
        if (event.entity instanceof EndCrystalEntity) {
            if (event.entity.getBlockPos().down().equals(last)) {
                id = event.entity.getEntityId();
                time = delay.get();
            }
        }
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (afterSwap && event.packet instanceof UpdateSelectedSlotS2CPacket) {
            shouldSwap = true;

            taskTransfer.bind(() -> {
                mc.player.inventory.selectedSlot = prev;
            }, swapDelay.get() * 50L);

            afterSwap = false;
        }
    }

    private void place(BlockPos pos, Vec3d hit, Direction dir) {
        if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN)) return;
        FindItemResult crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);
        if (!crystal.found()) return;
        use(crystal.slot(), pos, hit, dir);
    }

    private void use(int slot, BlockPos pos, Vec3d hit, Direction dir) {
        if (slot == 45) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.OFF_HAND);
            mc.player.swingHand(Hand.OFF_HAND);
        } else if (slot == mc.player.inventory.selectedSlot) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
        } else if (SlotUtils.isHotbar(slot)) {
            if (matrixBypass.get()) {
                int slotToSwap = slot + 36;

                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);

                mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(hit, dir, pos, false));
                mc.player.swingHand(Hand.MAIN_HAND);

                taskTransfer.bind(() -> {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);

                    mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
                }, swapDelay.get() * 50L);
            } else {
                prev = mc.player.inventory.selectedSlot;

                mc.player.inventory.selectedSlot = slot;
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot));

                mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(hit, dir, pos, false));
                mc.player.swingHand(Hand.MAIN_HAND);

                taskTransfer.bind(() -> {
                    mc.player.inventory.selectedSlot = prev;
                    afterSwap = true;
                }, swapDelay.get() * 50L);
            }
        } else {
            if (matrixBypass.get()) {
                int slotToSwap = slot >= 36 ? slot - 36 : slot;

                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);

                mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(hit, dir, pos, false));
                mc.player.swingHand(Hand.MAIN_HAND);

                taskTransfer.bind(() -> {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);

                    mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
                }, swapDelay.get() * 50L);
            } else {
                boolean air = false;
                for (int i = 0; i < SlotUtils.MAIN_START; i++) {
                    if (mc.player.inventory.getStack(i).getItem() == Items.AIR) {
                        air = true;
                        break;
                    }
                }

                prev = mc.player.inventory.selectedSlot;
                mc.interactionManager.pickFromInventory(slot);
                mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(hit, dir, pos, false));
                mc.player.swingHand(Hand.MAIN_HAND);

                if (air) {
                    if (excludeHotbar.get()) mc.interactionManager.pickFromInventory(slot);
                    taskTransfer.bind(() -> {
                        mc.player.inventory.selectedSlot = prev;
                        afterSwap = true;
                    }, swapDelay.get() * 50L);
                } else {
                    taskTransfer.bind(() -> {
                        mc.interactionManager.pickFromInventory(slot);
                        afterSwap = true;
                    }, swapDelay.get() * 50L);
                }
            }
        }
    }
}
