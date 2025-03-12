package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.item.Item;

public class AddCooldownEvent extends IEvent {
    private static final AddCooldownEvent INSTANCE = new AddCooldownEvent();

    public Item item;
    public int startTick = -1, endTick = -1,duration = -1;

    public static AddCooldownEvent get(Item item, int startTick, int endTick, int duration) {
        INSTANCE.item = item;
        INSTANCE.startTick = startTick;
        INSTANCE.endTick = endTick;
        INSTANCE.duration = duration;
        return INSTANCE;
    }
}
