package com.client.impl.function.misc;

import com.client.event.events.PacketEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

public class ItemSwapFix extends Function {
    public ItemSwapFix() {
        super("Item Swap Fix", Category.MISC);
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof UpdateSelectedSlotC2SPacket) {
            int serverSlot = ((UpdateSelectedSlotC2SPacket) event.packet).getSelectedSlot();
            if (serverSlot != mc.player.inventory.selectedSlot) {
                event.setCancelled(true);
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotS2CPacket(mc.player.inventory.selectedSlot));
            }
        }
    }
}