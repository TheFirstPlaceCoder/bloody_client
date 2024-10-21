package com.client.event.events;


import com.client.event.IEvent;

public class Render2DEvent extends IEvent {
    public float tickDelta;

    public Render2DEvent(float tickDelta) {
        this.tickDelta = tickDelta;
    }
}