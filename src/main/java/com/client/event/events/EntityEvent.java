package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;

public class EntityEvent {
    public static class Add extends IEvent {
        public Entity entity;

        public Add(Entity entity) {
            this.entity = entity;
        }
    }

    public static class Remove extends IEvent {
        public Entity entity;

        public Remove(Entity entity) {
            this.entity = entity;
        }
    }
}