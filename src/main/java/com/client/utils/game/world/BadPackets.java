package com.client.utils.game.world;

import api.interfaces.EventHandler;
import api.main.EventPriority;
import com.client.event.events.PacketEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.*;

public class BadPackets {
    private static boolean slot, attack, swing, block, inventory;

    public static boolean bad() {
        return bad(true, true, true, true, true);
    }

    public static boolean bad(final boolean slot, final boolean attack, final boolean swing, final boolean block, final boolean inventory) {
        return (BadPackets.slot && slot) ||
                (BadPackets.attack && attack) ||
                (BadPackets.swing && swing) ||
                (BadPackets.block && block) ||
                (BadPackets.inventory && inventory);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPacket(PacketEvent.Send event) {

        final Packet<?> packet = event.packet;

        if (packet instanceof UpdateSelectedSlotC2SPacket) {
            slot = true;
        } else if (packet instanceof HandSwingC2SPacket) {
            swing = true;
        } else if (packet instanceof PlayerInteractEntityC2SPacket) {
            attack = true;
        } else if (packet instanceof PlayerInteractBlockC2SPacket || packet instanceof PlayerActionC2SPacket) {
            block = true;
        } else if (packet instanceof ClickSlotC2SPacket ||
                packet instanceof CloseHandledScreenC2SPacket) {
            inventory = true;
        } else if (packet instanceof PlayerMoveC2SPacket) {
            slot = false;
            swing = false;
            attack = false;
            block = false;
            inventory = false;
        }
    };
}