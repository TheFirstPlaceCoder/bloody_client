package com.client.event.events;

import com.client.event.IEvent;

public class MouseScrollEvent extends IEvent {
    private static final MouseScrollEvent INSTANCE = new MouseScrollEvent();

    public double value;

    public static MouseScrollEvent get(double value) {
        INSTANCE.setCancelled(false);
        INSTANCE.value = value;

        return INSTANCE;
    }
}