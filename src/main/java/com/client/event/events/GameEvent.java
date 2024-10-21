package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

public class GameEvent extends IEvent {
    public GameJoinS2CPacket packet;

    public GameEvent(GameJoinS2CPacket packet) {
        this.packet = packet;
    }

    public static class Left extends GameEvent {

        public Left() {
            super(null);
        }
        public Left(GameJoinS2CPacket packet) {
            super(packet);
        }
    }

    public static class Join extends GameEvent {
        public Join(GameJoinS2CPacket packet) {
            super(packet);
        }
    }

}
