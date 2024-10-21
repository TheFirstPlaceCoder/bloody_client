package com.client.event.events;

import com.client.event.IEvent;

public class SendMovementPacketsEvent extends IEvent {
    public double x, y, z;
    public float yaw, pitch;
    public boolean onGround;
    public boolean both = false;
    public Runnable post;

    public SendMovementPacketsEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public static class Pre extends SendMovementPacketsEvent {

        public Pre(double x, double y, double z, float yaw, float pitch, boolean onGround) {
            super(x, y, z, yaw, pitch, onGround);
        }
    }

    public static class Post extends SendMovementPacketsEvent {

        public Post(double x, double y, double z, float yaw, float pitch, boolean onGround) {
            super(x, y, z, yaw, pitch, onGround);
        }
    }
}