package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.entity.vehicle.BoatEntity;

public class BoatMoveEvent extends IEvent {
    private static final BoatMoveEvent INSTANCE = new BoatMoveEvent();

    public BoatEntity boat;

    public static BoatMoveEvent get(BoatEntity entity) {
        INSTANCE.boat = entity;
        return INSTANCE;
    }
}