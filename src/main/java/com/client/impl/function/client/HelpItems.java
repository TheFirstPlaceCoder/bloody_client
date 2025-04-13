package com.client.impl.function.client;

import api.interfaces.EventHandler;
import com.client.event.events.KeyEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.KeybindSetting;
import com.client.utils.game.chat.ChatUtils;
import com.client.utils.misc.InputUtils;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtElement;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.client.BloodyClient.mc;

public class HelpItems extends Function {
    private final KeybindSetting bind = Keybind().name("enablea").defaultValue(-1).build();

    public HelpItems() {
        super("Help Items", Category.CLIENT);
    }

    public static int x, y;

    private List<String> getEnchantments(ItemStack stack) {
        List<String> list = new ArrayList<>();
        for (NbtElement enchantment : stack.getEnchantments()) {
            String tag = enchantment.toString().replace("{", "").replace("}", "");
            StringBuilder lvl = new StringBuilder();
            for (char c : tag.split(",")[0].toCharArray()) {
                try {
                    lvl.append(Integer.parseInt(String.valueOf(c)));
                } catch (Exception ignored) {
                }
            }
            StringBuilder enchantName = new StringBuilder();
            boolean targ = false;
            for (char c : tag.split(",")[1].toCharArray()) {
                if (c == '\"') {
                    if (!targ) {
                        targ = true;
                        continue;
                    } else {
                        break;
                    }
                }
                if (targ) {
                    enchantName.append(c);
                }
            }
            list.add(enchantName.toString().split(":")[1] + ":" + lvl);
        }
        return list;
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        if (event.key == bind.get() && event.action == InputUtils.Action.PRESS && mc.currentScreen instanceof GenericContainerScreen chestScreen) {
            Slot slot = getSlotAt(mc.mouse.getX() / 2, mc.mouse.getY() / 2);
            if (slot != null) {
                ItemStack itemStack = slot.getStack();
                System.out.println("Stack Name: " + itemStack.getName().getString());
                System.out.println("Translation Key: " + itemStack.getTranslationKey());

                System.out.println("Enchantments:");
                int i = 0;
                for (String check : getEnchantments(itemStack)) {
                    System.out.println(i + ". Ench-Name: " + check);
                    i++;
                }

                if (itemStack.getItem() instanceof PotionItem) {
                    System.out.println("hasPotion: " + hasPotion(itemStack, StatusEffects.STRENGTH));
                    System.out.println("getPotionLevel: " + getPotionLevel(itemStack, StatusEffects.STRENGTH));
                }

                System.out.println("Strings:");
                int j = 0;
                for (Text in : itemStack.getTooltip(mc.player, TooltipContext.Default.NORMAL)) {
                    System.out.println(j + ". Str-Name: " + in.getString());
                    j++;
                }

                String author = getName(itemStack);
                System.out.println("PRODAVEC: " + (author != null ? author : ""));
            }
        }
    }

    private boolean hasPotion(ItemStack itemStack, StatusEffect effect) {
        return PotionUtil.getPotionEffects(itemStack).stream().anyMatch(e -> e.getEffectType().equals(effect));
    }

    private int getPotionLevel(ItemStack itemStack, StatusEffect effect) {
        List<StatusEffectInstance> statusEffectInstances = PotionUtil.getPotionEffects(itemStack);

        for (StatusEffectInstance effectInstance : statusEffectInstances) {
            if (effectInstance.getEffectType().equals(effect)) {
                return effectInstance.getAmplifier();
            }
        }

        return 0;
    }

    @Nullable
    private Slot getSlotAt(double x, double y) {
        if (mc.currentScreen instanceof GenericContainerScreen chestScreen) {
            for (int i = 0; i < chestScreen.getScreenHandler().slots.size(); ++i) {
                Slot slot = (Slot) chestScreen.getScreenHandler().slots.get(i);
                if (this.isPointOverSlot(slot, x, y) && slot.doDrawHoveringEffect()) {
                    return slot;
                }
            }
        }

        return null;
    }

    @Nullable
    private static String getName(ItemStack stack) {
        try {
            return stack.getSubTag("display").getList("Lore", 8).stream()
                    .map(element -> {
                        String string = Text.Serializer.fromJson(element.asString()).getString();

                        if (Stream.of("Продавец:").anyMatch(string::contains)) {
                            List<Character> list = new ArrayList<>();
                            for (char c : string.toCharArray()) {
                                if (c == '.' || c == '(') break;
                                if (isLatinLetter(c) || Character.isDigit(c)) list.add(c);
                            }
                            char[] chars = new char[list.size()];
                            for (int index = 0; index < list.size(); index++) chars[index] = list.get(index);
                            try {
                                return new String(chars);
                            } catch (NumberFormatException ignored) {}
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

    public static boolean isLatinLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_';
    }

    private boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
        return this.isPointWithinBounds(slot.x, slot.y, 16, 16, pointX, pointY);
    }

    protected boolean isPointWithinBounds(int x1, int y1, int width, int height, double pointX, double pointY) {
        int i = x;
        int j = y;
        pointX -= (double)i;
        pointY -= (double)j;
        return pointX >= (double)(x1 - 1) && pointX < (double)(x1 + width + 1) && pointY >= (double)(y1 - 1) && pointY < (double)(y1 + height + 1);
    }
}
