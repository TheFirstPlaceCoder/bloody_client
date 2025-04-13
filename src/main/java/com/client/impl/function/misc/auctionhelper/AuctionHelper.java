package com.client.impl.function.misc.auctionhelper;

import com.client.event.events.RenderSlotEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.game.entity.ServerUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class AuctionHelper extends Function {
    public final ColorSetting minItem = Color().name("Самый дешевый").defaultValue(Color.GREEN).build();
    public final ColorSetting bestItem = Color().name("Самый выгодный").defaultValue(Color.RED).build();

    private final BooleanSetting checkDurability = Boolean().name("Без повреждений").defaultValue(true).build();
    private final BooleanSetting checkBalance = Boolean().name("Учитывать баланс").defaultValue(true).build();
    private final BooleanSetting checkFilters = Boolean().name("Фильтровать предметы").defaultValue(true).build();

    private final MultiBooleanSetting armorChecks = MultiBoolean().name("Фильтры брони").defaultValue(List.of(
            new MultiBooleanValue(true, "Защита"),
            new MultiBooleanValue(false, "Прочность"),
            new MultiBooleanValue(false, "Починка"),
            new MultiBooleanValue(false, "Без шипов")
    )).visible(checkFilters::get).build();

    private final IntegerSetting protectionLevel = Integer().name("Уровень защиты брони").defaultValue(5).min(1).max(5).visible(() -> checkFilters.get() && armorChecks.get("Защита")).build();
    private final IntegerSetting armorUnbreakingLevel = Integer().name("Уровень прочности брони").defaultValue(5).min(1).max(5).visible(() -> checkFilters.get() && armorChecks.get("Прочность")).build();

    private final MultiBooleanSetting swordChecks = MultiBoolean().name("Фильтры меча").defaultValue(List.of(
            new MultiBooleanValue(true, "Острота"),
            new MultiBooleanValue(false, "Прочность"),
            new MultiBooleanValue(false, "Отдача"),
            new MultiBooleanValue(false, "Заговор огня"),
            new MultiBooleanValue(false, "Разящий клинок"),
            new MultiBooleanValue(false, "Добыча"),
            new MultiBooleanValue(false, "Починка")
    )).visible(checkFilters::get).build();

    private final IntegerSetting sharpnessLevel = Integer().name("Уровень остроты меча").defaultValue(7).min(1).max(7).visible(() -> checkFilters.get() && swordChecks.get("Острота")).build();
    private final IntegerSetting swordUnbreakingLevel = Integer().name("Уровень прочности меча").defaultValue(5).min(1).max(5).visible(() -> checkFilters.get() && swordChecks.get("Прочность")).build();
    private final IntegerSetting swordKnockbackLevel = Integer().name("Уровень отдачи меча").defaultValue(2).min(1).max(2).visible(() -> checkFilters.get() && swordChecks.get("Отдача")).build();
    private final IntegerSetting swordFireAspectLevel = Integer().name("Уровень заговора огня меча").defaultValue(2).min(1).max(2).visible(() -> checkFilters.get() && swordChecks.get("Заговор огня")).build();
    private final IntegerSetting swordSweepengEdgeLevel = Integer().name("Уровень разящего клинка меча").defaultValue(3).min(1).max(3).visible(() -> checkFilters.get() && swordChecks.get("Разящий клинок")).build();
    private final IntegerSetting swordLootingLevel = Integer().name("Уровень добычи меча").defaultValue(3).min(1).max(3).visible(() -> checkFilters.get() && swordChecks.get("Добыча")).build();

    private final MultiBooleanSetting pickaxeChecks = MultiBoolean().name("Фильтры кирок").defaultValue(List.of(
            new MultiBooleanValue(true, "Эффективность"),
            new MultiBooleanValue(true, "Удача"),
            new MultiBooleanValue(false, "Прочность"),
            new MultiBooleanValue(false, "Починка"),
            new MultiBooleanValue(false, "Шёлковое касание")
    )).visible(checkFilters::get).build();

    private final IntegerSetting pickaxeEfficiencyLevel = Integer().name("Уровень эффективности кирки").defaultValue(5).min(1).max(10).visible(() -> checkFilters.get() && pickaxeChecks.get("Эффективность")).build();
    private final IntegerSetting pickaxeFortuneLevel = Integer().name("Уровень удачи кирки").defaultValue(3).min(1).max(3).visible(() -> checkFilters.get() && pickaxeChecks.get("Удача")).build();
    private final IntegerSetting pickaxeUnbreakingLevel = Integer().name("Уровень прочности кирки").defaultValue(5).min(1).max(10).visible(() -> checkFilters.get() && pickaxeChecks.get("Прочность")).build();

    private final MultiBooleanSetting potionsChecks = MultiBoolean().name("Фильтры зелий").defaultValue(List.of(
            new MultiBooleanValue(true, "Сила"),
            new MultiBooleanValue(false, "Скорость"),
            new MultiBooleanValue(false, "Прилив здоровья"),
            new MultiBooleanValue(false, "Исцеление"),
            new MultiBooleanValue(false, "Регенерация"),
            new MultiBooleanValue(false, "Отравление"),
            new MultiBooleanValue(false, "Замедление"),
            new MultiBooleanValue(false, "Слабость"),
            new MultiBooleanValue(false, "Иссушение")
    )).visible(checkFilters::get).build();

    private final IntegerSetting strengthLevel = Integer().name("Уровень Силы").defaultValue(2).min(1).max(4).visible(() -> checkFilters.get() && potionsChecks.get("Сила")).build();
    private final IntegerSetting speedLevel = Integer().name("Уровень Скорости").defaultValue(2).min(1).max(3).visible(() -> checkFilters.get() && potionsChecks.get("Скорость")).build();
    private final IntegerSetting healthBoostLevel = Integer().name("Уровень Прилива здоровья").defaultValue(2).min(1).max(3).visible(() -> checkFilters.get() && potionsChecks.get("Прилив здоровья")).build();
    private final IntegerSetting instantHealthBoostLevel = Integer().name("Уровень Исцеления").defaultValue(2).min(1).max(2).visible(() -> checkFilters.get() && potionsChecks.get("Исцеление")).build();
    private final IntegerSetting regenerationLevel = Integer().name("Уровень Регенерации").defaultValue(2).min(1).max(3).visible(() -> checkFilters.get() && potionsChecks.get("Регенерация")).build();
    private final IntegerSetting poisonLevel = Integer().name("Уровень Отравления").defaultValue(2).min(1).max(2).visible(() -> checkFilters.get() && potionsChecks.get("Отравление")).build();
    private final IntegerSetting slownessLevel = Integer().name("Уровень Замедления").defaultValue(2).min(1).max(4).visible(() -> checkFilters.get() && potionsChecks.get("Замедление")).build();
    private final IntegerSetting weaknessLevel = Integer().name("Уровень Слабости").defaultValue(2).min(1).max(3).visible(() -> checkFilters.get() && potionsChecks.get("Слабость")).build();
    private final IntegerSetting witherLevel = Integer().name("Уровень Иссушения").defaultValue(2).min(1).max(5).visible(() -> checkFilters.get() && potionsChecks.get("Иссушение")).build();

    public AuctionHelper() {
        super("Auction Helper", Category.MISC);
        setPremium(true);
    }

    private boolean isValid(ItemStack stack) {
        if (!checkDurability.get() && !checkBalance.get() && !checkFilters.get()) {
            return true;
        }

        boolean valid = true;

        if (checkDurability.get()) {
            valid &= !stack.isDamageable() || stack.getDamage() == 0;
        }
        if (checkBalance.get()) {
            Integer price = getPrice(stack);
            valid &= ServerUtils.getBalance() > 0 && price != null && price < ServerUtils.getBalance();
        }
        if (checkFilters.get()) {
            valid &= checkFilters(stack);
        }

        return valid;
    }

    private boolean checkFilters(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        List<Text> tooltips = stack.getTooltip(mc.player, TooltipContext.Default.NORMAL);

        boolean valid = true;

        if (stack.hasEnchantments()) {
            if (stack.getItem() instanceof ArmorItem armorItem && (armorItem.getMaterial().equals(ArmorMaterials.NETHERITE) || armorItem.getMaterial().equals(ArmorMaterials.DIAMOND)) && !armorChecks.getChecked().isEmpty()) {
                if (armorChecks.get("Защита")) {
                    valid &= validEnchant(enchantments, Enchantments.PROTECTION, protectionLevel.get());
                }
                if (armorChecks.get("Прочность")) {
                    valid &= validEnchant(enchantments, Enchantments.UNBREAKING, armorUnbreakingLevel.get());
                }
                if (armorChecks.get("Починка")) {
                    valid &= validEnchant(enchantments, Enchantments.MENDING, 1);
                }
                if (armorChecks.get("Без шипов")) {
                    valid &= !enchantments.containsKey(Enchantments.THORNS);
                }
                return valid;
            } else if (stack.getItem() instanceof SwordItem swordItem && (swordItem.getMaterial().equals(ToolMaterials.NETHERITE) || swordItem.getMaterial().equals(ToolMaterials.DIAMOND)) && !swordChecks.getChecked().isEmpty()) {
                if (swordChecks.get("Острота")) {
                    valid &= validEnchant(enchantments, Enchantments.SHARPNESS, sharpnessLevel.get());
                }
                if (swordChecks.get("Прочность")) {
                    valid &= validEnchant(enchantments, Enchantments.UNBREAKING, swordUnbreakingLevel.get());
                }
                if (swordChecks.get("Отдача")) {
                    valid &= validEnchant(enchantments, Enchantments.KNOCKBACK, swordKnockbackLevel.get());
                }
                if (swordChecks.get("Заговор огня")) {
                    valid &= validEnchant(enchantments, Enchantments.FIRE_ASPECT, swordFireAspectLevel.get());
                }
                if (swordChecks.get("Разящий клинок")) {
                    valid &= validEnchant(enchantments, Enchantments.SWEEPING, swordSweepengEdgeLevel.get());
                }
                if (swordChecks.get("Добыча")) {
                    valid &= validEnchant(enchantments, Enchantments.LOOTING, swordLootingLevel.get());
                }
                if (swordChecks.get("Починка")) {
                    valid &= validEnchant(enchantments, Enchantments.MENDING, 1);
                }
                return valid;
            } else if (stack.getItem() instanceof PickaxeItem pickaxeItem && (pickaxeItem.getMaterial().equals(ToolMaterials.NETHERITE) || pickaxeItem.getMaterial().equals(ToolMaterials.DIAMOND)) && !pickaxeChecks.getChecked().isEmpty()) {
                if (pickaxeChecks.get("Эффективность")) {
                    valid &= validEnchant(enchantments, Enchantments.EFFICIENCY, pickaxeEfficiencyLevel.get());
                }
                if (pickaxeChecks.get("Удача")) {
                    valid &= validEnchant(enchantments, Enchantments.FORTUNE, pickaxeFortuneLevel.get());
                }
                if (pickaxeChecks.get("Прочность")) {
                    valid &= validEnchant(enchantments, Enchantments.UNBREAKING, pickaxeUnbreakingLevel.get());
                }
                if (pickaxeChecks.get("Починка")) {
                    valid &= validEnchant(enchantments, Enchantments.MENDING, 1);
                }
                if (pickaxeChecks.get("Шёлковое касание")) {
                    valid &= validEnchant(enchantments, Enchantments.SILK_TOUCH, 1);
                }
                return valid;
            }
            return false;
        }

        if (stack.getItem() instanceof PotionItem && !potionsChecks.getChecked().isEmpty()) {
            if (potionsChecks.get("Сила")) {
                valid &= validPotion(stack, StatusEffects.STRENGTH, strengthLevel.get());
            }
            if (potionsChecks.get("Скорость")) {
                valid &= validPotion(stack, StatusEffects.SPEED, speedLevel.get());
            }
            if (potionsChecks.get("Прилив здоровья")) {
                valid &= validPotion(stack, StatusEffects.HEALTH_BOOST, healthBoostLevel.get());
            }
            if (potionsChecks.get("Исцеление")) {
                valid &= validPotion(stack, StatusEffects.INSTANT_HEALTH, instantHealthBoostLevel.get());
            }
            if (potionsChecks.get("Регенерация")) {
                valid &= validPotion(stack, StatusEffects.REGENERATION, regenerationLevel.get());
            }
            if (potionsChecks.get("Отравление")) {
                valid &= validPotion(stack, StatusEffects.POISON, poisonLevel.get());
            }
            if (potionsChecks.get("Замедление")) {
                valid &= validPotion(stack, StatusEffects.SLOWNESS, slownessLevel.get());
            }
            if (potionsChecks.get("Слабость")) {
                valid &= validPotion(stack, StatusEffects.WEAKNESS, weaknessLevel.get());
            }
            if (potionsChecks.get("Иссушение")) {
                valid &= validPotion(stack, StatusEffects.WITHER, witherLevel.get());
            }
            return valid;
        }
        return false;
    }

    private boolean validEnchant(Map<Enchantment, Integer> enchantments, Enchantment enchantment, int minLevel) {
        return enchantments.containsKey(enchantment) && enchantments.getOrDefault(enchantment, 0) >= minLevel;
    }

    private boolean validPotion(ItemStack itemStack, StatusEffect effect, int minLevel) {
        return hasPotion(itemStack, effect) && getPotionLevel(itemStack, effect) >= minLevel;
    }

    private boolean hasPotion(ItemStack itemStack, StatusEffect effect) {
        return PotionUtil.getPotionEffects(itemStack).stream().anyMatch(e -> e.getEffectType().equals(effect));
    }

    private int getPotionLevel(ItemStack itemStack, StatusEffect effect) {
        List<StatusEffectInstance> statusEffectInstances = PotionUtil.getPotionEffects(itemStack);

        for (StatusEffectInstance effectInstance : statusEffectInstances) {
            if (effectInstance.getEffectType().equals(effect)) {
                return effectInstance.getAmplifier() + 1;
            }
        }

        return 0;
    }

    @Override
    public void onRenderSlot(RenderSlotEvent event) {
        List<PriceSlot> priceSlots = new ArrayList<>();
        List<PriceSlot> pricesWithCountSlot = new ArrayList<>();

        ScreenHandler container = event.handler;
        Text title = event.title;

        if (container instanceof GenericContainerScreenHandler && (title.getString().toLowerCase().contains("аукцион") || title.getString().toLowerCase().contains("поиск"))) {
            for (int i = 0; i < container.slots.size() - 36; ++i) {
                Slot slot = container.slots.get(i);

                ItemStack stack = slot.getStack();
                if (stack.isEmpty() || !isValid(stack)) continue;

                Integer price = getPrice(stack);
                if (price != null && price > 0) {
                    priceSlots.add(new PriceSlot(i, price));
                    pricesWithCountSlot.add(new PriceSlot(i, price / stack.getCount()));
                }
            }
        }

        priceSlots.sort(Comparator.comparingInt(o -> o.price));
        pricesWithCountSlot.sort(Comparator.comparingInt(o -> o.price));

        if (!priceSlots.isEmpty() && (title.getString().toLowerCase().contains("аукцион") || title.getString().toLowerCase().contains("поиск"))) {
            Slot minSlot = container.slots.get(priceSlots.get(0).slotIndex);
            Slot minSlotWithCount = container.slots.get(pricesWithCountSlot.get(0).slotIndex);

            event.minCountSlot = minSlot;
            event.minSlot = minSlotWithCount;
            event.minCountColor = minItem.get();
            event.minColor = bestItem.get();
        }
    }

    @Nullable
    private static Integer getPrice(ItemStack stack) {
        try {
            return stack.getSubTag("display").getList("Lore", 8).stream()
                    .map(element -> {
                        String string = Text.Serializer.fromJson(element.asString()).getString();

                        if (Stream.of("$", "₽", "Цeнa", "Цена:", "Стоимость").anyMatch(string::contains)) {
                            List<Character> list = new ArrayList<>();
                            for (char c : string.toCharArray()) {
                                if (c == '.') break;
                                if (c >= '0' && c <= '9') list.add(c);
                            }
                            char[] chars = new char[list.size()];
                            for (int index = 0; index < list.size(); index++) chars[index] = list.get(index);
                            try {
                                return Integer.parseInt(new String(chars));
                            } catch (NumberFormatException ex) {}
                        }

                        return null;
                    })
                    .filter(Objects::nonNull)
                    .findFirst()
                    .get();
        } catch (Exception ex) {
            return null;
        }
    }

    private record PriceSlot(int slotIndex, int price) {
    }
}
