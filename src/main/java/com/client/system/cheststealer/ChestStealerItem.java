package com.client.system.cheststealer;

import net.minecraft.item.Item;

public class ChestStealerItem {
    public Item item;
    public int priority;

    public ChestStealerItem(Item item, int priority) {
        this.item = item;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return item.getTranslationKey() + ":" + priority;
    }
}