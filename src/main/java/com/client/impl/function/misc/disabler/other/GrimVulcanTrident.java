package com.client.impl.function.misc.disabler.other;

import com.client.event.events.TickEvent;
import com.client.impl.function.misc.disabler.DisablerMode;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.utils.game.inventory.InvUtils;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class GrimVulcanTrident extends DisablerMode {
    public int currentTick;

    @Override
    public void onEnable() {
        NotificationManager.add(new Notification(NotificationType.CLIENT, "Нужен трезубец с зачаровнием Тягун"));
        currentTick = settings.grimDelay.get();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (currentTick >= settings.grimDelay.get()){
            currentTick = 0;
            assert mc.player != null;
            int tridentSlot = InvUtils.findInHotbar(Items.TRIDENT).slot();
            int oldSlot = mc.player.inventory.selectedSlot;
            if (tridentSlot == -1) return;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(tridentSlot));
            mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(oldSlot));
        } else {
            currentTick++;
        }
    }
}
