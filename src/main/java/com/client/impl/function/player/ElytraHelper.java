package com.client.impl.function.player;

import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.KeybindSetting;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.misc.InputUtils;
import com.client.utils.misc.TaskTransfer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * __aaa__
 * 25.05.2024
 * */
public class ElytraHelper extends Function {
    public ElytraHelper() {
        super("Elytra Helper", Category.PLAYER);
    }

    private final KeybindSetting swap = Keybind().name("Свап").enName("Swap").defaultValue(-1).build();
    private final KeybindSetting firewowrk = Keybind().name("Фейерверк").enName("Firework").defaultValue(-1).build();
    private final BooleanSetting autoStart = Boolean().name("Авто полет").enName("Auto Start").defaultValue(false).build();
    private final BooleanSetting useFirework = Boolean().name("Использовать фейерверк сразу").enName("Use Start Firework").defaultValue(false).visible(autoStart::get).build();

    public final IntegerSetting delay = Integer().name("Задержка").enName("Swap Delay").defaultValue(2).min(0).max(6).visible(() -> firewowrk.get() != -1 || (autoStart.get() && useFirework.get())).build();
    private final BooleanSetting matrixBypass = Boolean().name("Обход Matrix").enName("Matrix Bypass").defaultValue(false).visible(() -> firewowrk.get() != -1 || (autoStart.get() && useFirework.get())).build();
    private final BooleanSetting excludeHotbar = Boolean().name("Не оставлять в хотбаре").enName("Exclude Hotbar").defaultValue(true).visible(() -> firewowrk.get() != -1 || (autoStart.get() && useFirework.get())).build();

    private final TaskTransfer taskTransfer = new TaskTransfer(), equipTaskTransfer = new TaskTransfer();
    private boolean shouldSwap = false, afterSwap = false;
    public int prev;

    @Override
    public void onEnable() {
        shouldSwap = false;
        afterSwap = false;
    }

    @Override
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (event.action != InputUtils.Action.PRESS) return;

        if (swap.key(event.key, !event.mouse)) swap();
        else if (firewowrk.key(event.key, !event.mouse)) useFirework();
    }

    @Override
    public void tick(TickEvent.Post event) {
        equipTaskTransfer.handle();
        taskTransfer.handle();

        if (shouldSwap) {
            mc.player.inventory.selectedSlot = prev;
            shouldSwap = false;
        }
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
    }

    private void swap() {
        ItemStack slot = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem().getDefaultStack();
        int equip;
        boolean chestplateFlag = false;

        if (slot.isEmpty() || slot.getItem().equals(Items.ELYTRA)) {
            equip = getSlot(true);
            chestplateFlag = true;

            if (equip == -1 && slot.isEmpty()) {
                equip = getSlot(false);
                chestplateFlag = false;
            }
        } else {
            equip = getSlot(false);
        }

        if (equip == -1) return;

        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 0, SlotActionType.QUICK_MOVE, mc.player);

        if (SlotUtils.isHotbar(equip)) {
            InvUtils.swap(equip);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            InvUtils.swapBack();
        } else {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, equip, 0, SlotActionType.QUICK_MOVE, mc.player);
        }

        NotificationManager.add(new Notification(NotificationType.CLIENT, "Изменил на " + (chestplateFlag ? "нагрудник" : "элитры") + ".", 2000L), NotificationManager.NotifType.Info);

        if (autoStart.get() && !chestplateFlag) {
            if (mc.player.isOnGround()) {
                mc.player.jump();
            }

            equipTaskTransfer.bind(() -> {
                mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                if (useFirework.get()) useFirework();
            }, 70);
        }
    }

    public void useFirework() {
        FindItemResult i = getItem();

        if (!i.found() || mc.player.getItemCooldownManager().isCoolingDown(mc.player.inventory.getStack(i.slot()).getItem())) {
            return;
        }

        if (i.slot() == 45) {
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.OFF_HAND);
            mc.player.swingHand(Hand.OFF_HAND);

            return;
        }

        use(i.slot());
    }

    private int getSlot(boolean chestplate) {
        long hash = 0;
        int index = 0;

        for (int i = 0; i < mc.player.inventory.size(); i++) {
            ItemStack stack = mc.player.inventory.getStack(i);

            if (!chestplate && stack.getItem().equals(Items.ELYTRA)) {
                return i;
            }

            if (stack.getTranslationKey().contains("chestplate")) {
                long tempHash = InvUtils.getId(stack);

                if (tempHash > hash) {
                    index = i;
                    hash = tempHash;
                }
            }

        }
        return index;
    }

    private FindItemResult getItem() {
        FindItemResult result;

        if (mc.player.getOffHandStack().getItem().equals(Items.FIREWORK_ROCKET))
            return new FindItemResult(45, mc.player.getOffHandStack().getCount());

        result = InvUtils.find(Items.FIREWORK_ROCKET);

        return result;
    }
}