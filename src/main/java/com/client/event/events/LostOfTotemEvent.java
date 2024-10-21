package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.entity.Entity;

public class LostOfTotemEvent extends IEvent {
    public Entity entity;

    public LostOfTotemEvent(Entity entity) {
        this.entity = entity;
    }
}
