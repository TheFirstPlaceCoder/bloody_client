package com.client.impl.function.movement.speedmodes.vulcan;

import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.interfaces.IVec3d;
import com.client.utils.game.movement.MovementUtils;
import mixin.accessor.PlayerMoveC2SPacketAccessor;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.shape.VoxelShapes;

public class Vulcan_286 extends SpeedMode {
    @Override
    public void tick(TickEvent.Pre e) {
        if (MovementUtils.isMoving() && collidesBottomVertical() && !mc.options.keyJump.isPressed()) {
            StatusEffectInstance speedEffect = mc.player.hasStatusEffect(StatusEffects.SPEED) ? mc.player.getStatusEffect(StatusEffects.SPEED) : null;
            boolean isAffectedBySpeed = speedEffect != null && speedEffect.getAmplifier() > 0;
            boolean isMovingSideways = mc.player.input.movementSideways != 0f;

            double strafe;
            if (isAffectedBySpeed) {
                strafe = 0.59;
            } else if (isMovingSideways) {
                strafe = 0.41;
            } else {
                strafe = 0.42;
            }

            MovementUtils.strafe((float) strafe);
            ((IVec3d) mc.player.getVelocity()).setY(0.005);
        }
    }

    @Override
    public void onPacket(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket packet && collidesBottomVertical() && !mc.options.keyJump.isPressed()) {
            ((PlayerMoveC2SPacketAccessor) packet).setY(((PlayerMoveC2SPacketAccessor) packet).getY() + 0.005);
        }
    }

    private boolean collidesBottomVertical() {
        return mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0, -0.005, 0.0))
                .anyMatch(shape -> shape != VoxelShapes.empty());
    }
}
