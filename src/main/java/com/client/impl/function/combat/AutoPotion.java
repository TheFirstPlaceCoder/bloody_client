package com.client.impl.function.combat;

import com.client.event.events.PacketEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.misc.TaskTransfer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class AutoPotion extends Function {
    public final IntegerSetting delay = Integer().name("Задержка").enName("Place Delay").defaultValue(2).min(0).max(6).build();
    private final BooleanSetting matrixBypass = Boolean().name("Обход Matrix").enName("Matrix Bypass").defaultValue(false).build();
    private final BooleanSetting excludeHotbar = Boolean().name("Не оставлять в хотбаре").enName("Exclude Hotbar").defaultValue(true).build();
    private final BooleanSetting strength = Boolean().name("Сила").enName("Power").defaultValue(true).build();
    private final BooleanSetting speed = Boolean().name("Скорость").enName("Speed").defaultValue(true).build();
    private final BooleanSetting fireResistance = Boolean().name("Огнестойкость").enName("Fire Resistance").defaultValue(true).build();
    private final BooleanSetting healing = Boolean().name("Хилка").enName("Healing Potion").defaultValue(true).build();
    private final IntegerSetting health = Integer().name("Здоровье").enName("Health").defaultValue(4).min(1).max(36).visible(healing::get).build();

    public AutoPotion() {
        super("Auto Potion", Category.COMBAT);
    }

    private boolean[] use;
    private long lastTime, healTime;
    private final TaskTransfer taskTransfer = new TaskTransfer();
    private boolean shouldSwap = false, afterSwap = false;
    public int prev;

    @Override
    public void onEnable() {
        healTime = 0;
        lastTime = 0;
        shouldSwap = false;
        afterSwap = false;

        use = new boolean[SlotUtils.MAIN_END];
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        if (cantUse()) return;
        boolean flag = false;
        for (int i = 0; i < SlotUtils.MAIN_END; i++) {
            if (canUse(i, true)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            event.pitch = 90F;
        }
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent.Post event) {
        if (cantUse()) return;
        for (int i = 0; i < SlotUtils.MAIN_END; i++) {
            if (canUse(i)) {
                use(i);
            }
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (use == null) {
            use = new boolean[SlotUtils.MAIN_END];
        }

        if (System.currentTimeMillis() >= lastTime && lastTime != -1) {
            for (int i = 0; i < SlotUtils.MAIN_END; i++) {
                use[i] = false;
            }

            lastTime = -1;
        }

        taskTransfer.handle();

        if (shouldSwap) {
            mc.player.inventory.selectedSlot = prev;
            shouldSwap = false;
        }
    }

    private boolean cantUse() {
        return !mc.player.isOnGround() && mc.player.fallDistance > 1.25F;
    }

    private boolean canUse(int i) {
        return canUse(i, false);
    }

    private boolean canUse(int i, boolean check) {
        ItemStack itemStack = mc.player.inventory.getStack(i);

        if (itemStack.isEmpty() || !(itemStack.getItem() instanceof SplashPotionItem)) return false;

        boolean bl = false;
        String name = itemStack.getTranslationKey().toLowerCase();

        if (strength.get() && name.contains("strength") && !mc.player.hasStatusEffect(StatusEffects.STRENGTH)) bl = true;
        if (speed.get() && name.contains("swiftness") && !mc.player.hasStatusEffect(StatusEffects.SPEED)) bl = true;
        if (fireResistance.get() && name.contains("fire_resistance") && !mc.player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) bl = true;
        if (healing.get() && name.contains("healing") && (int) EntityUtils.getTotalHealth() <= health.get() && System.currentTimeMillis() > healTime) {
            bl = true;
            if (!check) {
                healTime = System.currentTimeMillis() + 250L;
            }
        }

        return bl && !use[i];
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (afterSwap && event.packet instanceof UpdateSelectedSlotS2CPacket) {
            shouldSwap = true;

            taskTransfer.bind(() -> {
                mc.player.inventory.selectedSlot = prev;
            }, delay.get() * 50L);

            afterSwap = false;
        }
    }

    private void use(int slot) {
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

                mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);

                taskTransfer.bind(() -> {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);

                    mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));

                    taskTransfer.bind(() -> {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, mc.player.inventory.selectedSlot + 36, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, mc.player.inventory.selectedSlot + 36, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);

                        mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
                    }, delay.get() * 50L);
                }, delay.get() * 50L);
            } else {
                prev = mc.player.inventory.selectedSlot;
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                mc.interactionManager.pickFromInventory(slot);
                mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);
                taskTransfer.bind(() -> {
                    mc.player.inventory.selectedSlot = prev;
                    afterSwap = true;
                }, delay.get() * 50L);
            }
        } else {
            if (matrixBypass.get()) {
                int slotToSwap = slot >= 36 ? slot - 36 : slot;

                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);

                mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);

                taskTransfer.bind(() -> {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotToSwap, mc.player.inventory.selectedSlot, SlotActionType.SWAP, mc.player);

                    mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
                }, delay.get() * 50L);
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
                mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);

                if (air) {
                    if (excludeHotbar.get()) mc.interactionManager.pickFromInventory(slot);
                    taskTransfer.bind(() -> {
                        mc.player.inventory.selectedSlot = prev;
                        afterSwap = true;
                    }, delay.get() * 50L);
                } else {
                    taskTransfer.bind(() -> {
                        mc.interactionManager.pickFromInventory(slot);
                        afterSwap = true;
                    }, delay.get() * 50L);
                }
            }
        }

        use[slot] = true;
        lastTime = System.currentTimeMillis() + 3000L;
    }
}
