package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

public class PlayerTravelEvent extends IEvent {
    private static final PlayerTravelEvent INSTANCE = new PlayerTravelEvent();
    public boolean pre;
    public Vec3d movement;

    public static PlayerTravelEvent get(Vec3d movement, boolean pre) {
        INSTANCE.movement = movement;
        INSTANCE.pre = pre;
        return INSTANCE;
    }
}