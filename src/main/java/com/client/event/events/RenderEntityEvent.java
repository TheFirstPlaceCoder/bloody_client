package com.client.event.events;

import net.minecraft.entity.Entity;

public class RenderEntityEvent {
    public Runnable before, after;
    public Entity entity;
}
