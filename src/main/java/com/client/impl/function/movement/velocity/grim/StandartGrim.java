package com.client.impl.function.movement.velocity.grim;

import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.velocity.VelocityMode;
import com.client.interfaces.IExplosionS2CPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class StandartGrim extends VelocityMode {
    private boolean flag;
    private int cooldown;

    @Override
    public void onEnable() {
        cooldown = 0;
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        if (e.packet instanceof EntityVelocityUpdateS2CPacket pac) {
            if (pac.getId() == mc.player.getEntityId()) {
                e.setCancelled(true);
                flag = true;
            }
        }

        if (e.packet instanceof ExplosionS2CPacket explosion) {
            ((IExplosionS2CPacket) explosion).setVelocityX(0);
            ((IExplosionS2CPacket) explosion).setVelocityY(0);
            ((IExplosionS2CPacket) explosion).setVelocityZ(0);
            flag = true;
        }

        if (e.packet instanceof PlayerPositionLookS2CPacket) {
            cooldown = 5;
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (flag) {
            if (cooldown <= 0) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Both(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.yaw, mc.player.pitch, mc.player.isOnGround()));
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.player.getPos()), Direction.DOWN));
            }
            flag = false;
        }
    }
}
