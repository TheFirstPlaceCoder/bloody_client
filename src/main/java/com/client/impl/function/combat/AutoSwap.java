package com.client.impl.function.combat;

import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.KeybindSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.misc.InputUtils;
import com.client.utils.misc.TaskTransfer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
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

    private final KeybindSetting swap = Keybind().name("Свап").build();
    private final ListSetting item1 = List().list(List.of("Золотое яблоко", "Чарка", "Щит", "Фейерверк", "Сфера", "Тотем")).defaultValue("Щит").name("Предмет 1").build();
    private final ListSetting item2 = List().list(List.of("Золотое яблоко", "Чарка", "Щит", "Фейерверк", "Сфера", "Тотем")).defaultValue("Золотое яблоко").name("Предмет 2").build();

    private final TaskTransfer taskTransfer = new TaskTransfer();
    private int prev;

    @Override
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (!swap.key(event.key, !event.mouse) || event.action != InputUtils.Action.PRESS) return;
        run();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        taskTransfer.handle();
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

    private Item getItem(String name) {
        return switch (name) {
            case "Сфера" -> Items.DIRT;
            case "Золотое яблоко" -> Items.COBBLESTONE;
            case "Щит" -> Items.SHIELD;
            case "Чарка" -> Items.ENCHANTED_GOLDEN_APPLE;
            case "Тотем" -> Items.TOTEM_OF_UNDYING;
            default -> Items.FIREWORK_ROCKET;
        };
    }
}