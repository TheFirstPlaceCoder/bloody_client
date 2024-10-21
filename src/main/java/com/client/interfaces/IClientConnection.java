package com.client.interfaces;

import net.minecraft.network.Packet;

public interface IClientConnection {
    void sendPacket(Packet<?> packet);
}
