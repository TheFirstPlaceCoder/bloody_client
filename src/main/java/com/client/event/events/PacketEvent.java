package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.network.Packet;

public class PacketEvent {
    public static class Receive extends IEvent {
        public Packet<?> packet;

        public Receive(Packet<?> packet) {
            this.packet = packet;
        }
    }

    public static class Send extends IEvent {
        public Packet<?> packet;

        public Send(Packet<?> packet) {
            this.packet = packet;
        }
    }

    public static class Sent extends IEvent {
        public Packet<?> packet;

        public Sent(Packet<?> packet) {
            this.packet = packet;
        }
    }
}