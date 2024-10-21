package com.client.impl.function.combat;

import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.util.Hand;

public class AutoPotion extends Function {
    private final BooleanSetting strength = Boolean().name("Сила").defaultValue(true).build();
    private final BooleanSetting speed = Boolean().name("Скорость").defaultValue(true).build();
    private final BooleanSetting fireResistance = Boolean().name("Огнестойкость").defaultValue(true).build();
    private final BooleanSetting healing = Boolean().name("Хилка").defaultValue(true).build();
    private final IntegerSetting health = Integer().name("Здоровье").defaultValue(4).min(1).max(36).visible(healing::get).build();

    public AutoPotion() {
        super("Auto Potion", Category.COMBAT);
    }

    private boolean[] use;
    private long lastTime, healTime;

    @Override
    public void onEnable() {
        healTime = 0;
        lastTime = 0;

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
                swapAndUse(i);
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

    private void swapAndUse(int i) {
        if (SlotUtils.isHotbar(i)) {
            InvUtils.swap(i);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            InvUtils.swapBack();
        } else {
            InvUtils.quickSwap().fromId(i).to(mc.player.inventory.selectedSlot);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            InvUtils.quickSwap().fromId(i).to(mc.player.inventory.selectedSlot);
        }
        use[i] = true;
        lastTime = System.currentTimeMillis() + 3000L;
    }
}
