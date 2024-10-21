package com.client.event.events;

import com.client.event.IEvent;

public class ESPRenderEvent extends IEvent {
    public float tickDelta;

    public ESPRenderEvent(float tickDelta) {
        this.tickDelta = tickDelta;
    }
}