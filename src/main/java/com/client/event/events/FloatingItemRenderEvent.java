package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.item.ItemStack;

public class FloatingItemRenderEvent extends IEvent {
    public ItemStack stack;

    public FloatingItemRenderEvent(ItemStack stack) {
        this.stack = stack;
    }
}
