package com.client.interfaces;

public interface IClientPlayerEntity {
    boolean lastSprinting();
    void setLastSprinting(boolean bool);
    float getLastYaw();
    float getLastPitch();
}