package com.client.impl.function.player;

import api.interfaces.EventHandler;
import api.main.EventPriority;
import com.client.event.events.ItemUseCrosshairTargetEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.combat.AutoGApple;
import com.client.interfaces.IMinecraftClient;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.game.inventory.InvUtils;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

public class AutoEat extends Function {
    public final IntegerSetting hungerThreshold = Integer().name("Уровень голода").defaultValue(16).min(1).max(19).build();

    public AutoEat() {
        super("Auto Eat", Category.PLAYER);
    }

    private boolean eating;
    private int slot, prevSlot;

    @Override
    public void onDisable() {
        if (eating) stopEating();
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onTick(TickEvent.Pre event) {
        if (FunctionManager.get(AutoGApple.class).isEating()) return;

        if (eating) {
            if (shouldEat()) {
                if (!mc.player.inventory.getStack(slot).isFood()) {
                    int slot = findSlot();

                    if (slot == -1) {
                        stopEating();
                        return;
                    } else {
                        changeSlot(slot);
                    }
                }

                eat();
            } else {
                stopEating();
            }
        }
        else {
            if (shouldEat()) {
                slot = findSlot();

                if (slot != -1) startEating();
            }
        }
    }

    @EventHandler
    private void onItemUseCrosshairTarget(ItemUseCrosshairTargetEvent event) {
        if (eating) event.target = null;
    }

    private void startEating() {
        prevSlot = mc.player.inventory.selectedSlot;
        eat();
    }

    private void eat() {
        changeSlot(slot);
        setPressed(true);
        if (!mc.player.isUsingItem()) ((IMinecraftClient) mc).rightClick();

        eating = true;
    }

    private void stopEating() {
        changeSlot(prevSlot);
        setPressed(false);

        eating = false;
    }

    private void setPressed(boolean pressed) {
        mc.options.keyUse.setPressed(pressed);
    }

    private void changeSlot(int slot) {
        InvUtils.swap(slot);
        this.slot = slot;
    }

    private boolean shouldEat() {
        return mc.player.getHungerManager().getFoodLevel() <= hungerThreshold.get();
    }

    private int findSlot() {
        int slot = -1;
        int bestHunger = -1;

        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStack(i).getItem();
            if (!item.isFood()) continue;

            int hunger = item.getFoodComponent().getHunger();
            if (hunger > bestHunger) {
                if (getBadItems().contains(item)) continue;

                slot = i;
                bestHunger = hunger;
            }
        }

        return slot;
    }

    public List<Item> getBadItems() {
        return List.of(Items.POISONOUS_POTATO, Items.SPIDER_EYE, Items.ROTTEN_FLESH, Items.PUFFERFISH, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.SUSPICIOUS_STEW, Items.CHORUS_FRUIT, Items.CHICKEN);
    }
}
