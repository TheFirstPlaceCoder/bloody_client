package com.client.impl.function.combat;

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
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.misc.InputUtils;
import com.client.utils.misc.TaskTransfer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

/**
 * __aaa__
 * 26.05.2024
 * */
public class AutoSwap extends Function {
    public AutoSwap() {
        super("Auto Swap", Category.COMBAT);
    }

    private final KeybindSetting swap = Keybind().name("Свап").enName("Swap").build();
    public final IntegerSetting delay = Integer().name("Задержка").enName("Place Delay").defaultValue(2).min(0).max(6).build();
    private final BooleanSetting matrixBypass = Boolean().name("Обход Matrix").enName("Matrix Bypass").defaultValue(true).build();
    private final ListSetting item1 = List().list(List.of("Золотое яблоко", "Чарка", "Щит", "Фейерверк", "Сфера", "Тотем")).defaultValue("Щит").name("Предмет 1").enName("Item 1").build();
    private final ListSetting item2 = List().list(List.of("Золотое яблоко", "Чарка", "Щит", "Фейерверк", "Сфера", "Тотем")).defaultValue("Золотое яблоко").name("Предмет 2").enName("Item 2").build();
    private final BooleanSetting excludeHotbar = Boolean().name("Не оставлять в хотбаре").enName("Exclude Hotbar").defaultValue(true).build();

    private final TaskTransfer taskTransfer = new TaskTransfer();
    private int prev;
    private boolean shouldSwap = false, afterSwap = false;

    @Override
    public void onEnable() {
        shouldSwap = false;
        afterSwap = false;
    }

    @Override
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (!swap.key(event.key, !event.mouse) || event.action != InputUtils.Action.PRESS) return;
        run();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        taskTransfer.handle();

        if (shouldSwap) {
            mc.player.inventory.selectedSlot = prev;
            shouldSwap = false;
        }
    }

    private void run() {
        FindItemResult i1 = InvUtils.find(getItem(item1.get()));
        FindItemResult i2 = InvUtils.find(getItem(item2.get()));

        if (!i1.found() || !i2.found()) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "Предмет не найден.", 2000L), NotificationManager.NotifType.Error);
            return;
        }

        boolean offhandFlag = true;
        try {
            offhandFlag = mc.player.inventory.getStack(i1.slot()).getItem().equals(mc.player.getOffHandStack().getItem());
        } catch (Exception ignore) {
        }

        boolean itemFlag = false;

        if (offhandFlag) {
            use(i2.slot());
            itemFlag = true;
        } else {
            use(i1.slot());
        }

        NotificationManager.add(new Notification(NotificationType.CLIENT, "Свапнул на " + (itemFlag ? item2.get() : item1.get()) + ".", 2000L), NotificationManager.NotifType.Info);
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
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                mc.interactionManager.pickFromInventory(slot);
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

    private void swap() {
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private Item getItem(String name) {
        return switch (name) {
            case "Сфера" -> Items.COBBLESTONE;
            case "Золотое яблоко" -> Items.DIRT;
            case "Щит" -> Items.SHIELD;
            case "Чарка" -> Items.ENCHANTED_GOLDEN_APPLE;
            case "Тотем" -> Items.TOTEM_OF_UNDYING;
            default -> Items.FIREWORK_ROCKET;
        };
    }
}