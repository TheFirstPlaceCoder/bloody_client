package com.client.system.cheststealer.item.items;

import com.client.system.cheststealer.item.ChestStealerItem;
import com.client.system.setting.api.ConfigManual;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ChestStealerDefaultItem extends ChestStealerItem {
    private Item item;

    public ChestStealerDefaultItem() {
    }

    public ChestStealerDefaultItem(Item item) {
        this.item = item;
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public String getName() {
        return item.getDefaultStack().getName().getString();
    }

    @Override
    public String toCfg() {
        return item.getTranslationKey() + ConfigManual.DEFAULT.split + priority;
    }

    @Override
    public void load(String in, ConfigManual manual) {
        this.item = Registry.ITEM.stream().filter(f -> f.getTranslationKey().equals(in.split(manual.split)[0])).toList().get(0);
        this.priority = Integer.parseInt(in.split(manual.split)[1]);
    }
}
