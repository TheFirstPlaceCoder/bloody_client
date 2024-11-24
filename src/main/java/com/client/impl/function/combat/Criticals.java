package com.client.impl.function.combat;

import com.client.event.events.PacketEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class Criticals extends Function {
    public Criticals() {
        super("Criticals", Category.COMBAT);
    }

    @Override
    public void onPacket(PacketEvent.Send event) {
        if (event.packet instanceof PlayerInteractEntityC2SPacket && ((PlayerInteractEntityC2SPacket) event.packet).getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
            if (skipCrit()) return;

            Entity entity =  ((PlayerInteractEntityC2SPacket) event.packet).getEntity(mc.world);

            if (!(entity instanceof LivingEntity)) return;

            sendPacket(0.1, false);
            sendPacket(0, false);
            sendPacket(0.01, false);
            sendPacket(0, false);
        }
    }

    private void sendPacket(double height, boolean onGround) {
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.PositionOnly(x, y + height, z, onGround);
        mc.player.networkHandler.sendPacket(packet);
    }

    private boolean skipCrit() {
        return !mc.player.isOnGround() || mc.player.isSubmergedInWater() || mc.player.isInLava()  || mc.player.isClimbing();
    }
}