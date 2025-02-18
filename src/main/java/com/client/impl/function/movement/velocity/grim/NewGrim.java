package com.client.impl.function.movement.velocity.grim;

import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.velocity.VelocityMode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.BlockPos;

public class NewGrim extends VelocityMode {
    private boolean canCancel = false;
    public int cooldown = 0;

    @Override
    public void onEnable() {
        canCancel = false;
        cooldown = 0;
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if ((e.packet instanceof EntityVelocityUpdateS2CPacket p && p.getId() == mc.player.getEntityId() || e.packet instanceof ExplosionS2CPacket)) {
            e.cancel();

            canCancel = true;
            cooldown = settings.delay.get();
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (canCancel) {
            if (cooldown <= 0) {

                for (int i = 0; i < settings.repeats.get(); i++) {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Both(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.yaw, mc.player.pitch, mc.player.isOnGround()));
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.player.getPos().add(0, 1, 0)), mc.player.getHorizontalFacing().getOpposite()));
                }

                canCancel = false;
            } else cooldown--;
        }
    }
}