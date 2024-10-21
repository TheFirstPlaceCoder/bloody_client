package com.client.impl.function.combat;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.game.entity.SelfUtils;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.misc.TaskTransfer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * __aaa__
 * 25.05.2024
 * */
public class AutoTotem extends Function {
    public AutoTotem() {
        super("Auto Totem", Category.COMBAT);
    }

    private final IntegerSetting health = Integer().name("Здоровье").defaultValue(4).min(0).max(20).build();
    private final BooleanSetting fall = Boolean().name("При падении").defaultValue(true).build();
    private final BooleanSetting saveTalisman = Boolean().name("Сохранять талисман").defaultValue(true).build();
    private final BooleanSetting swapBack = Boolean().name("Возвращать назад").defaultValue(true).build();

    private long oldSlot = -1;
    private float fallDistance;

    private final TaskTransfer taskTransfer = new TaskTransfer();
    private int prev;

    @Override
    public void onEnable() {
        oldSlot = -1;
        fallDistance = 0;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        taskTransfer.handle();

        if (mc.player.isOnGround()) {
            fallDistance = 0;
        } else {
            if (fallDistance == 0) {
                fallDistance = getFallDistance();
            }
        }

        if (checkHealth(true) && swapBack.get()) {
            if (oldSlot != -1) {
                use(getOldSlot());
                oldSlot = -1;
            }
        }

        int totem = getTotemSlot();

        if (totem == -1) return;

        if (checkHealth(false) && canSwap()) {
            if (oldSlot == -1) oldSlot = InvUtils.getId(mc.player.getOffHandStack());
            use(totem);
        }
    }

    private float getFallDistance() {
        float f = 0;

        for (int i = (int) mc.player.getY(); i > 0; i--) {
            BlockState blockState = mc.world.getBlockState(new BlockPos(mc.player.getX(), i, mc.player.getZ()));

            if (blockState.isOpaque()) {
                break;
            }

            f++;
        }

        return f;
    }

    private void use(int slot) {
        if (slot == mc.player.inventory.selectedSlot) {
            swap();
        } else if (SlotUtils.isHotbar(slot)) {
            prev = mc.player.inventory.selectedSlot;
            mc.interactionManager.pickFromInventory(slot);
            swap();
            taskTransfer.bind(() -> mc.player.inventory.selectedSlot = prev, 150L);
        } else {
            boolean air = false;
            for (int i = 0; i < SlotUtils.MAIN_START; i++) {
                if (mc.player.inventory.getStack(i).getItem() == Items.AIR) {
                    air = true;
                    break;
                }
            }
            if (air) {
                prev = mc.player.inventory.selectedSlot;
                mc.interactionManager.pickFromInventory(slot);
                swap();
                taskTransfer.bind(() -> mc.player.inventory.selectedSlot = prev, 100L);
            } else {
                mc.interactionManager.pickFromInventory(slot);
                swap();
                mc.interactionManager.pickFromInventory(slot);
            }
        }
    }

    private void swap() {
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private boolean canSwap() {
        if (saveTalisman.get()) {
            boolean hasGlint = mc.player.getOffHandStack().hasGlint() && mc.player.getOffHandStack().getItem().equals(Items.TOTEM_OF_UNDYING);
            if (mc.player.getMainHandStack().getItem().equals(Items.TOTEM_OF_UNDYING)) return false;

            if (hasGlint) {
                long hash = InvUtils.getId(mc.player.getOffHandStack());

                for (int i = 0; i < mc.player.inventory.size(); i++) {
                    if (i == 45) continue;

                    ItemStack stack = mc.player.inventory.getStack(i);

                    if (stack.getItem().equals(Items.TOTEM_OF_UNDYING) && InvUtils.getId(stack) < hash) {
                        return true;
                    }
                }

                return false;
            } else {
                return !mc.player.getOffHandStack().getItem().equals(Items.TOTEM_OF_UNDYING);
            }

        } else {
            return !SelfUtils.hasItem(Items.TOTEM_OF_UNDYING);
        }
    }

    private int getTotemSlot() {
        List<Integer> slots = new ArrayList<>();

        for (int i = 0; i < mc.player.inventory.size(); i++) {
            if (i == 45) continue;

            ItemStack stack = mc.player.inventory.getStack(i);

            if (stack.getItem().equals(Items.TOTEM_OF_UNDYING)) {

                if (stack.hasGlint()) {
                    slots.add(i);
                    continue;
                }

                return i;
            }
        }

        if (slots.isEmpty())
            return -1;

        slots.sort(Comparator.comparing(i -> InvUtils.getId(mc.player.inventory.getStack(i))));

        return slots.get(0);
    }

    private int getOldSlot() {
        if (oldSlot == 0) return -1;

        for (int i = 0; i < mc.player.inventory.size(); i++) {
            if (InvUtils.getId(mc.player.inventory.getStack(i)) == oldSlot) {
                return i;
            }
        }

        return -1;
    }

    private boolean checkHealth(boolean flag) {
        int player_health = (int)(SelfUtils.getHealth() - fallDamage());

        return flag ? player_health > health.get() : health.get() > player_health;
    }

    private float fallDamage() {
        if (!fall.get() || fallDistance < 3 || MovementUtils.isInWater() || mc.player.abilities.flying) return 0F;
        if (mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING) || mc.player.hasStatusEffect(StatusEffects.LEVITATION)) return 0F;

        return fallDistance;
    }
}