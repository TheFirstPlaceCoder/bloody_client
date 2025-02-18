package com.client.impl.function.movement.speedmodes.ncp;

import com.client.event.events.PlayerMoveEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Timer;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.interfaces.IVec3d;
import com.client.utils.game.movement.MovementUtils;
import net.minecraft.entity.effect.StatusEffects;

public class NCP extends SpeedMode {
    public float currentPlayerSpeed = 0, prevForward = 0;
    public int stage, ticks;
    public double baseSpeed;

    @Override
    public void onEnable() {
        Timer.setOverride(Timer.OFF);
        currentPlayerSpeed = 0;
        stage = 1;
        ticks = 0;
        baseSpeed = 0.2873D;
    }

    @Override
    public void tick(TickEvent.Post e) {
        currentPlayerSpeed = (float) Math.hypot(mc.player.getX() - mc.player.prevX, mc.player.getZ() - mc.player.prevZ);
    }

    @Override
    public void onMove(PlayerMoveEvent event) {
        if (MovementUtils.isMoving()) {
            float currentSpeed = mc.player.input.movementForward <= 0 && prevForward > 0 ? currentPlayerSpeed * 0.66f : currentPlayerSpeed;
            if (stage == 1 && mc.player.isOnGround()) {
                mc.player.setVelocity(mc.player.getVelocity().x, MovementUtils.getJumpSpeed(), mc.player.getVelocity().z);
                ((IVec3d) event.movement).setY(MovementUtils.getJumpSpeed());
                baseSpeed *= 2.149;
                stage = 2;
            } else if (stage == 2) {
                baseSpeed = currentSpeed - (0.66 * (currentSpeed - getBaseMoveSpeed()));
                stage = 3;
            } else {
                if ((mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0, mc.player.getVelocity().getY(), 0.0)).iterator().hasNext() || mc.player.verticalCollision))
                    stage = 1;
                baseSpeed = currentSpeed - currentSpeed / 159.0D;
            }

            baseSpeed = Math.max(baseSpeed, getBaseMoveSpeed());

            double ncpSpeed = mc.player.input.movementForward < 1 ? 0.465 : 0.576;
            double ncpBypassSpeed = mc.player.input.movementForward < 1 ? 0.44 : 0.57;

            if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
                double amplifier = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
                ncpSpeed *= 1 + (0.2 * (amplifier + 1));
                ncpBypassSpeed *= 1 + (0.2 * (amplifier + 1));
            }

            if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
                double amplifier = mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
                ncpSpeed /= 1 + (0.2 * (amplifier + 1));
                ncpBypassSpeed /= 1 + (0.2 * (amplifier + 1));
            }

            baseSpeed = Math.min(baseSpeed, ticks > 25 ? ncpSpeed : ncpBypassSpeed);

            if (ticks++ > 50)
                ticks = 0;

            MovementUtils.modifyEventSpeed(event, baseSpeed);
            prevForward = mc.player.input.movementForward;
        } else {
            ((IVec3d) event.movement).setX(0);
            ((IVec3d) event.movement).setZ(0);
        }

        event.cancel();
    }

    public double getBaseMoveSpeed() {
        int n;
        double d = 0.2873;

        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            n = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
            d *= 1.0 + 0.2 * (n + 1);
        }
        if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            n = mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
            d /= 1.0 + 0.2 * (n + 1);
        }
        if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
            n = mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
            d /= 1.0 + (0.2 * (n + 1));
        }
        return d;
    }
}