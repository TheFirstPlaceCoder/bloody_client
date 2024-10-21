package com.client.system.cheststealer.item;

import com.client.system.setting.api.IConfig;
import net.minecraft.item.Item;

public abstract class ChestStealerItem implements IConfig {
    public int priority;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public abstract Item getItem();

    public abstract String getName();
}