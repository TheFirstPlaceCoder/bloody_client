package com.client.event.events;

import com.client.event.IEvent;

public class KeyboardInputEvent extends IEvent {
    public float forward, sideways;
    public boolean jumping, sneaking;
    public double sneakSlowDownMultiplier;
    public boolean pressingForward;
    public boolean pressingBack;
    public boolean pressingLeft;
    public boolean pressingRight;

    public KeyboardInputEvent(float forward, float sideways, boolean jumping, boolean sneaking, double sneakSlowDownMultiplier, boolean pressingForward, boolean pressingBack, boolean pressingLeft, boolean pressingRight) {
        this.forward = forward;
        this.sideways = sideways;
        this.jumping = jumping;
        this.sneaking = sneaking;
        this.sneakSlowDownMultiplier = sneakSlowDownMultiplier;
        this.pressingForward = pressingForward;
        this.pressingBack = pressingBack;
        this.pressingLeft = pressingLeft;
        this.pressingRight = pressingRight;
    }
}
