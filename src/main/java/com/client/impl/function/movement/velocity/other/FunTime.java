package com.client.impl.function.movement.velocity.other;

import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.velocity.VelocityMode;
import com.client.interfaces.IEntityVelocityUpdateS2CPacket;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.utils.math.MsTimer;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class FunTime extends VelocityMode {
    private MsTimer msTimer = new MsTimer();
    private boolean setBack = false;
    private boolean afterSetback = false;
    private int attackCount = 0;

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (setBack && msTimer.passedS(15)) {
            setBack = false;
            afterSetback = true;
        }

        if (e.packet instanceof EntityVelocityUpdateS2CPacket pac) {
            if (pac.getId() != mc.player.getEntityId() || setBack) return;

            if (mc.player.isOnGround()) {
                ((IEntityVelocityUpdateS2CPacket) pac).setX(((int) ((double) pac.getVelocityX() * (afterSetback ? 0.72 : 0.33))));
                ((IEntityVelocityUpdateS2CPacket) pac).setZ(((int) ((double) pac.getVelocityZ() * (afterSetback ? 0.72 : 0.33))));

                if (afterSetback && attackCount++ > 4) {
                    attackCount = 0;
                    afterSetback = false;
                }
            }
        } else if (e.packet instanceof PlayerPositionLookS2CPacket) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "Вас сетбекнуло! Велосити на время не работает!", 3000L), NotificationManager.NotifType.Warning);
            setBack = true;
            msTimer.reset();
        }
    }

    // ground 0.33
}
