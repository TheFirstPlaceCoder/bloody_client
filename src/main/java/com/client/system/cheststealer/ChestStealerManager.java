package com.client.system.cheststealer;

import com.client.utils.auth.Loader;
import com.client.utils.game.inventory.InvUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChestStealerManager {
    private static final ConcurrentLinkedDeque<ChestStealerItem> chestStealerItem = new ConcurrentLinkedDeque<>();

    public static void add(ChestStealerItem item) {
        if (chestStealerItem.stream().noneMatch(e -> e.item == item.item))
            chestStealerItem.add(item);
    }

    public static void remove(ChestStealerItem item) {
        chestStealerItem.remove(item);
    }

    public static void clear() {
        chestStealerItem.clear();
    }

    public static ConcurrentLinkedDeque<ChestStealerItem> getChestStealerItem() {
        return chestStealerItem;
    }

    public static List<Integer> getInv(Inventory in, String mode) {
        int index = !Loader.isPremium() || mode.equals("Нет") ? 0 : mode.equals("Только") ? 1 : 2;

        List<Integer> list = new ArrayList<>();

        switch (index) {
            case 0: {
                for (int i = 0; i < in.size(); i++) {
                    if (in.getStack(i).isEmpty()) continue;
                    list.add(i);
                }

                break;
            }

            case 1: {
                for (int i = 0; i < in.size(); i++) {
                    ItemStack stack = in.getStack(i);
                    if (stack.isEmpty()) continue;

                    for (ChestStealerItem stealerItem : chestStealerItem) {
                        if (stack.getItem().equals(stealerItem.item)) {
                            list.add(i);
                            break;
                        }
                    }
                }

                list.sort(Comparator.comparing(i -> -InvUtils.getId(in.getStack(i))));

                break;
            }

            case 2: {
                List<ChestStealerItem> items = new ArrayList<>(chestStealerItem.stream().toList());
                items.sort(Comparator.comparing(c -> -c.priority));

                for (ChestStealerItem stealerItem : items) {
                    list.addAll(getSlots(in, stealerItem.item));
                }

                for (int i = 0; i < in.size(); i++) {
                    if (in.getStack(i).isEmpty()) continue;

                    boolean skip = false;

                    for (ChestStealerItem item : items) {
                        if (item.item.equals(in.getStack(i))) {
                            skip = true;
                            break;
                        }
                    }

                    if (skip) continue;

                    list.add(i);
                }

                break;
            }
        }

        return list;
    }

    private static List<Integer> getSlots(Inventory in, Item item) {
        List<Integer> slots = new ArrayList<>();

        for (int i = 0; i < in.size(); i++) {
            ItemStack stack = in.getStack(i);
            if (stack.getItem().equals(item)) {
                slots.add(i);
            }
        }

        slots.sort(Comparator.comparing(i -> -InvUtils.getId(in.getStack(i))));

        return slots;
    }

    public static void save(BufferedWriter writer) {
        try {
            writer.write("automyst{\n");
            for (ChestStealerItem stealerItem : chestStealerItem) {
                writer.write(stealerItem.toString() + "\n");
            }
            writer.write("}\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load(List<String> strings) {
        clear();
        boolean target = false;
        for (String string : strings) {
            if (string.startsWith("}") && target)
                break;
            if (target) {
                String itemId = string.split(":")[0];
                String priority = string.split(":")[1];
                try {
                    add(new ChestStealerItem(Registry.ITEM.stream().filter(a -> a.getTranslationKey().equals(itemId)).toList().get(0), Integer.parseInt(priority)));
                } catch (Exception ignore) {
                }
            } else {
                if (string.startsWith("automyst")) {
                    target = true;
                }
            }
        }
    }
}
