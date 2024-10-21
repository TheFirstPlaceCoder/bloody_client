package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.item.ItemStack;

public class FinishItemUseEvent extends IEvent {
    private static final FinishItemUseEvent INSTANCE = new FinishItemUseEvent();

    public ItemStack itemStack;

    public static FinishItemUseEvent get(ItemStack itemStack) {
        INSTANCE.itemStack = itemStack;
        return INSTANCE;
    }
}
