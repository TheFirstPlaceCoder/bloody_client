package com.client.impl.function.misc;

import com.client.event.events.KeybindSettingEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.KeybindSetting;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.misc.InputUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

/**
 * __aaa__
 * 25.05.2024
 * */
public class ElytraSwap extends Function {
    public ElytraSwap() {
        super("Elytra Swap", Category.MISC);
    }

    private final KeybindSetting swap = Keybind().name("Свап").defaultValue(-1).build();
    private final BooleanSetting autoStart = Boolean().name("Авто полет").defaultValue(false).build();

    @Override
    public void onKeybindSetting(KeybindSettingEvent event) {
        if (!swap.key(event.key, !event.mouse) || event.action != InputUtils.Action.PRESS) return;
        swap();
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

            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
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
}