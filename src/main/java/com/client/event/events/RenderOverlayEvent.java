package com.client.event.events;

import com.client.event.IEvent;

public class RenderOverlayEvent extends IEvent {
    public Type type;

    public RenderOverlayEvent(Type type) {
        this.type = type;
    }

    public enum Type {
        PORTAL,
        PUMPKIN,
        VIGNETTE,
        EFFECTS,
        FIRE,
        CROSSHAIR,
        HELDITEMNAME,
        BLOCK,
        WATER
    }
}
