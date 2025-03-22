package com.client.impl.function.combat;

import com.client.event.events.GameEvent;
import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.entity.SelfUtils;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.misc.TaskTransfer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * __aaa__
 * 25.05.2024
 * */
public class AutoTotem extends Function {
    public AutoTotem() {
        super("Auto Totem", Category.COMBAT);
    }

    public final IntegerSetting delay = Integer().name("Задержка").enName("Place Delay").defaultValue(2).min(0).max(6).build();
    private final IntegerSetting health = Integer().name("Здоровье").enName("Health").defaultValue(4).min(0).max(20).build();
    private final BooleanSetting matrixBypass = Boolean().name("Обход Matrix").enName("Matrix Bypass").defaultValue(false).build();
    private final BooleanSetting fall = Boolean().name("При падении").enName("Place On Fall").defaultValue(true).build();
    private final BooleanSetting elytra = Boolean().name("При элитре").enName("Place On Elytra").defaultValue(true).build();
    private final BooleanSetting serverLag = Boolean().name("При лагах сервера").enName("Place On Lags").defaultValue(true).build();
    private final BooleanSetting saveTalisman = Boolean().name("Сохранять талисман").enName("Save Enchanted Totems").defaultValue(true).build();
    private final BooleanSetting swapBack = Boolean().name("Возвращать назад").enName("Swap Back").defaultValue(true).build();
    private final BooleanSetting smartSwapBack = Boolean().name("Возвращать если нет тотемов").enName("Swap Back If No Totems").defaultValue(true).visible(swapBack::get).build();
    private final BooleanSetting excludeHotbar = Boolean().name("Не оставлять в хотбаре").enName("Exclude Hotbar").defaultValue(true).build();

    private long oldSlot = -1;
    private long timeLastTimeUpdate = -1;
    private long timeGameJoined;

    private final TaskTransfer taskTransfer = new TaskTransfer();
    private int prev;
    private boolean shouldSwap = false, afterSwap = false;

    @Override
    public void onEnable() {
        oldSlot = -1;
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

        if (checkHealth(true) && swapBack.get() && oldSlot != -1) {
            int backSlot = getOldSlot();
            if (backSlot >= 0) use(backSlot);
            oldSlot = -1;
        }

        int totem = getTotemSlot();

        if (totem == -1) {
            if (swapBack.get() && smartSwapBack.get() && oldSlot != -1) {
                int backSlot = getOldSlot();
                if (backSlot >= 0) use(backSlot);
                oldSlot = -1;
            }

            return;
        }

        if (checkHealth(false) && canSwap()) {
            if (oldSlot == -1) oldSlot = InvUtils.getId(mc.player.getOffHandStack());
            use(totem);
        }
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (afterSwap && event.packet instanceof UpdateSelectedSlotS2CPacket) {
            shouldSwap = true;

            taskTransfer.bind(() -> {
                mc.player.inventory.selectedSlot = prev;
            }, delay.get() * 50);

            afterSwap = false;
        }
    }

    @Override
    public void onGameJoinEvent(GameEvent.Join event) {
        timeGameJoined = timeLastTimeUpdate = System.currentTimeMillis();
    }

    private float getTimeSinceLastTick() {
        long now = System.currentTimeMillis();
        if (now - timeGameJoined < 4000) return 0;
        return (now - timeLastTimeUpdate) / 1000f;
    }

    private void use(int slot) {
        if (slot == mc.player.inventory.selectedSlot) {
            swap();
        } else if (SlotUtils.isHotbar(slot)) {
            if (matrixBypass.get()) {
                int slotToSwap = slot + 36;

                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, 40, SlotActionType.SWAP, mc.player);

                taskTransfer.bind(() -> mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId)), delay.get() * 50);
            } else {
                prev = mc.player.inventory.selectedSlot;
                mc.player.inventory.selectedSlot = slot;
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot));
                swap();
                taskTransfer.bind(() -> mc.player.inventory.selectedSlot = prev, delay.get() * 50);
            }
        } else {
            if (matrixBypass.get()) {
                int slotToSwap = slot >= 36 ? slot - 36 : slot;

                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, 40, SlotActionType.SWAP, mc.player);

                taskTransfer.bind(() -> mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId)), delay.get() * 50);
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
                swap();

                if (air) {
                    if (excludeHotbar.get()) mc.interactionManager.pickFromInventory(slot);
                    taskTransfer.bind(() -> mc.player.inventory.selectedSlot = prev, delay.get() * 50);
                } else {
                    taskTransfer.bind(() -> {
                        mc.interactionManager.pickFromInventory(slot);
                        afterSwap = true;
                    }, delay.get() * 50);
                }
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
            if (InvUtils.getId(mc.player.inventory.getStack(i)) == oldSlot && !mc.player.inventory.getStack(i).getItem().equals(Items.AIR)) {
                return i;
            }
        }

        return -1;
    }

    private boolean checkHealth(boolean flag) {
        int player_health = (int)(SelfUtils.getHealth() - fallDamage());

        if (elytra.get() && mc.player.isFallFlying() && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) return !flag;
        if (serverLag.get() && getTimeSinceLastTick() > 1) return !flag;

        return flag ? player_health > health.get() : health.get() > player_health;
    }

    private float fallDamage() {
        if (!fall.get() || mc.player.fallDistance < 3 || MovementUtils.isInWater()) return 0F;
        if (mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING) || mc.player.hasStatusEffect(StatusEffects.LEVITATION)) return 0F;

        float damageTaken = 0;

        if (mc.player.fallDistance > 3) {
            float damage = mc.player.fallDistance * 0.5f;

            if (damage > damageTaken) {
                damageTaken = damage;
            }
        }

        return damageTaken;
    }
}