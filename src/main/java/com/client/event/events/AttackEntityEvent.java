package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.entity.Entity;

public class AttackEntityEvent extends IEvent {
    public Entity entity;

    public AttackEntityEvent(Entity entity) {
        this.entity = entity;
    }

    public static class Pre extends AttackEntityEvent {

        public Pre(Entity entity) {
            super(entity);
        }
    }

    public static class Post extends AttackEntityEvent {

        public Post(Entity entity) {
            super(entity);
        }
    }
}