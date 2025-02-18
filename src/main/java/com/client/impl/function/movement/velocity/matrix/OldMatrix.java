package com.client.impl.function.movement.velocity.matrix;

import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.velocity.VelocityMode;
import com.client.interfaces.IEntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class OldMatrix extends VelocityMode {
    private boolean flag;

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player.hurtTime > 0 && !mc.player.isOnGround()) {
            double var3 = mc.player.yaw * 0.017453292F;
            double var5 = Math.sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x + mc.player.getVelocity().z * mc.player.getVelocity().z);
            mc.player.setVelocity(-Math.sin(var3) * var5, mc.player.getVelocity().y, Math.cos(var3) * var5);
            mc.player.setSprinting(mc.player.age % 2 != 0);
        }
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (e.packet instanceof EntityVelocityUpdateS2CPacket pac) {
            if (pac.getId() != mc.player.getEntityId()) return;

            if (!flag) {
                e.setCancelled(true);
                flag = true;
            } else {
                flag = false;
                ((IEntityVelocityUpdateS2CPacket) pac).setX(((int) ((double) pac.getVelocityX() * -0.1)));
                ((IEntityVelocityUpdateS2CPacket) pac).setZ(((int) ((double) pac.getVelocityZ() * -0.1)));
            }
        }
    }
}
