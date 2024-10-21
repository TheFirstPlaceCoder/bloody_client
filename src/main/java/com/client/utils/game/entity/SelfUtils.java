package com.client.utils.game.entity;

import net.minecraft.item.Item;
import net.minecraft.item.PotionItem;

import static com.client.BloodyClient.mc;

public class SelfUtils {
    public static float getHealth() {
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    public static boolean hasItem(Item items) {
        return PlayerUtils.hasItem(mc.player, items);
    }

    public static double getEyaY() {
        return PlayerUtils.getEyeY(mc.player);
    }

    public static int getSelectedSlot() {
        return mc.player.inventory.selectedSlot;
    }

    public static void setCurrentSlot(int slot) {
        mc.player.inventory.selectedSlot = slot;
    }

    public static boolean eating() {
        return mc.player.isUsingItem() && (mc.player.getMainHandStack().isFood() || mc.player.getOffHandStack().isFood());
    }

    public static boolean drinking() {
        return mc.player.isUsingItem() && (mc.player.getMainHandStack().getItem() instanceof PotionItem || mc.player.getOffHandStack().getItem() instanceof PotionItem);
    }

    public static boolean hasElytra() {
        return PlayerUtils.hasElytra(mc.player);
    }
}
