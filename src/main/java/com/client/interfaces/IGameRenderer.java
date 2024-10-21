package com.client.interfaces;

import net.minecraft.entity.Entity;

public interface IGameRenderer {
    Entity getTarget(float yaw, float pitch);
}