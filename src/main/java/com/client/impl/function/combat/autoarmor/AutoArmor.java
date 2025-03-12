package com.client.impl.function.combat.autoarmor;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.inventory.SlotUtils;
import com.client.utils.game.movement.MovementUtils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Function {
    private final IntegerSetting delay = Integer().name("Задержка").enName("Delay").defaultValue(1).min(0).max(5).build();
    private final BooleanSetting onlyStanding = Boolean().name("Только когда стоишь").enName("Only Stand").defaultValue(true).build();
    private final BooleanSetting onlyInv = Boolean().name("Только в инвентаре").enName("Only While Inventory").defaultValue(true).build();
    private final BooleanSetting hotbar = Boolean().name("Брать из хотбара").enName("Include Hotbar").defaultValue(true).build();
    private final BooleanSetting invisible = Boolean().name("Пауза при инвизе").enName("Invisible Pause").defaultValue(true).build();
    private final BooleanSetting grimBypass = Boolean().name("Обход Grim").enName("Grim BYpass").defaultValue(true).build();

    public AutoArmor() {
        super("Auto Armor", Category.COMBAT);
    }

    public int ticks;

    @Override
    public void onEnable() {
        ticks = delay.get();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if ((MovementUtils.isMoving() && onlyStanding.get()) || (!(mc.currentScreen instanceof InventoryScreen) && onlyInv.get()) || (mc.player.hasStatusEffect(StatusEffects.INVISIBILITY) && invisible.get())) return;

        if (ticks <= 0) {
            for (int i = 3; i >= 0; i--) {
                if (mc.player.inventory.armor.get(i).isEmpty()) {
                    equipArmor(i);
                    break;
                }
            }

            ticks = delay.get();
        } else ticks--;
    }

    private void equipArmor(int slot) {
        ArmorType armorType = getArmorTypeFromSlot(slot);
        int bestSlot = -1;
        int bestRating = -1;

        for (int i = (hotbar.get() ? 0 : 9); i <= 44; i++) {
            Item item = mc.player.inventory.getStack(i).getItem();
            if (item instanceof ArmorItem && getArmorTypeFromItem(item) == armorType) {
                int damageReduction = ((ArmorItem) item).getProtection();
                if (damageReduction >= bestRating) {
                    bestSlot = i;
                    bestRating = damageReduction;
                }
            }
        }

        if (bestSlot != -1 && bestRating != -1) {
            if (grimBypass.get()) {
                int finalBestSlot = bestSlot;
                InvUtils.grimSwap(() -> InvUtils.move().fromId(getSlotIndex(finalBestSlot)).to(SlotUtils.ARMOR_START + slot));
            } else {
                mc.interactionManager.clickSlot(0, getSlotIndex(bestSlot), 0, SlotActionType.QUICK_MOVE, mc.player);
            }
        }
    }

    private ArmorType getArmorTypeFromItem(Item item) {
        if (Items.NETHERITE_HELMET.equals(item) || Items.DIAMOND_HELMET.equals(item) || Items.GOLDEN_HELMET.equals(item) || Items.IRON_HELMET.equals(item) || Items.CHAINMAIL_HELMET.equals(item) || Items.LEATHER_HELMET.equals(item)) {
            return ArmorType.HELMET;
        } else if (Items.NETHERITE_CHESTPLATE.equals(item) || Items.DIAMOND_CHESTPLATE.equals(item) || Items.GOLDEN_CHESTPLATE.equals(item) || Items.IRON_CHESTPLATE.equals(item) || Items.CHAINMAIL_CHESTPLATE.equals(item) || Items.LEATHER_CHESTPLATE.equals(item)) {
            return ArmorType.CHESTPLATE;
        } else if (Items.NETHERITE_LEGGINGS.equals(item) || Items.DIAMOND_LEGGINGS.equals(item) || Items.GOLDEN_LEGGINGS.equals(item) || Items.IRON_LEGGINGS.equals(item) || Items.CHAINMAIL_LEGGINGS.equals(item) || Items.LEATHER_LEGGINGS.equals(item)) {
            return ArmorType.PANTS;
        } else if (Items.NETHERITE_BOOTS.equals(item) || Items.DIAMOND_BOOTS.equals(item) || Items.GOLDEN_BOOTS.equals(item) || Items.IRON_BOOTS.equals(item) || Items.CHAINMAIL_BOOTS.equals(item) || Items.LEATHER_BOOTS.equals(item)) {
            return ArmorType.BOOTS;
        }
        return null;
    }

    private ArmorType getArmorTypeFromSlot(int slot) {
        return switch (slot) {
            case 3 -> ArmorType.HELMET;
            case 2 -> ArmorType.CHESTPLATE;
            case 1 -> ArmorType.PANTS;
            case 0 -> ArmorType.BOOTS;
            default -> null;
        };
    }

    public static int getSlotIndex(int index) {
        return index < 9 ? index + 36 : index;
    }
}