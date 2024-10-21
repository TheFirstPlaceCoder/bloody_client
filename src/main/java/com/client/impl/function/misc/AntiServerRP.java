package com.client.impl.function.misc;

import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.utils.classes.ResourcePackAction;
import com.client.utils.math.MsTimer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;

public class AntiServerRP extends Function {
    public AntiServerRP() {
        super("Anti Server RP", Category.MISC);
    }

    ResourcePackAction currentAction = ResourcePackAction.WAIT;
    MsTimer timer = new MsTimer();

    @Override
    public void onPacket(PacketEvent.Receive packetEvent) {
        if (packetEvent.packet instanceof ResourcePackSendS2CPacket) {
            currentAction = ResourcePackAction.ACCEPT;
            packetEvent.cancel();
        }
    }

    @Override
    public void tick(TickEvent.Pre tickEvent) {
        ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (networkHandler != null) {
            if (currentAction == ResourcePackAction.ACCEPT) {
                networkHandler.sendPacket(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED));
                currentAction = ResourcePackAction.SEND;
                timer.reset();
            } else if (currentAction == ResourcePackAction.SEND && timer.passedMs(300L)) {
                networkHandler.sendPacket(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
                currentAction = ResourcePackAction.WAIT;
            }
        }
    }

    @Override
    public void onDisable() {
        currentAction = ResourcePackAction.WAIT;
    }
}
