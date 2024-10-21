package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.client.option.Perspective;

public class ChangePerspectiveEvent extends IEvent {
    private static final ChangePerspectiveEvent INSTANCE = new ChangePerspectiveEvent();

    public Perspective perspective;

    public static ChangePerspectiveEvent get(Perspective perspective) {
        INSTANCE.setCancelled(false);
        INSTANCE.perspective = perspective;
        return INSTANCE;
    }
}
