package com.client.system.autobuy;

import com.client.impl.function.client.AutoBuy;
import com.client.system.function.FunctionManager;
import com.client.utils.game.entity.ServerUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.client.BloodyClient.mc;

public class CustomAutoBuyItem extends AutoBuyItem {
    public String name;
    public List<String> enchantments = new ArrayList<>();
    public List<String> strings = new ArrayList<>();
    public boolean strictCheck = false, isFTItem;

    public CustomAutoBuyItem(Item item, int price, boolean isFTItem) {
        this.item = item;
        this.price = price;
        this.isFTItem = isFTItem;
    }

    public boolean tryBuy(ItemStack stack, int price) {
        if (!stack.getItem().equals(item)) return false;
        if (price / stack.getCount() > this.price) return false;
        if (price > ServerUtils.getBalance() || this.price > ServerUtils.getBalance()) return false;
        if ((isFTItem && FunctionManager.get(AutoBuy.class).server.get().equals("HolyWorld")) || (!isFTItem && FunctionManager.get(AutoBuy.class).server.get().equals("FunTime"))) return false;

        if (!enchantments.isEmpty()) {
            if (strictCheck) {
                List<String> eList = getEnchantments(stack);
                if (eList.size() != enchantments.size()) return false;
                for (String check : eList) {
                    boolean flag = true;
                    for (String in : enchantments) {
                        if ((in.contains("farmer") && in.contains(check)) || check.equalsIgnoreCase(in)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        return false;
                    }
                }
            } else {
                for (String check : enchantments) {
                    boolean flag = true;
                    for (String in : getEnchantments(stack)) {
                        if ((check.contains("farmer") && in.contains(check)) || check.equalsIgnoreCase(in)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        return false;
                    }
                }
            }
        }

        if (!strings.isEmpty()) {
            for (String check : strings) {
                boolean flag = true;
                for (Text in : stack.getTooltip(mc.player, TooltipContext.Default.NORMAL)) {
                    if (in.equals(stack.getName())) continue;
                    if (checkString(check, in.getString())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkString(String stringFromPlayer, String stringToCheck) {
        return Pattern.compile("\\b" + removeAndNextChar(stringFromPlayer) + "\\b", Pattern.CASE_INSENSITIVE).matcher(removeAndNextChar(stringToCheck)).find();
    }

    public static String removeAndNextChar(String input) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            // Если символ не §, добавляем его в результат
            if (currentChar != '§') {
                output.append(currentChar);
            } else {
                // Если символ $, пропускаем следующий символ
                i++;
            }
        }
        return output.toString();
    }

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
}