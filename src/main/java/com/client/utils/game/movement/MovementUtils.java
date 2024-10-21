package com.client.utils.game.movement;

import com.client.event.events.KeyboardInputEvent;
import com.client.event.events.PlayerMoveEvent;
import com.client.interfaces.IVec3d;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.input.Input;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

import static com.client.BloodyClient.mc;

public class MovementUtils {
    public static final double WALK_SPEED = 0.19;
    private static final Vec3d horizontalVelocity = new Vec3d(0, 0, 0);
    private static final double diagonal = 1 / Math.sqrt(2);

    public static double getBaseMoveSpeed() {
        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            return (WALK_SPEED) * ((Objects.requireNonNull(mc.player.getStatusEffect(StatusEffects.SPEED)).getAmplifier() + 1) * 0.2 + 1.0);
        }
        return WALK_SPEED;
    }

    public static double getJumpSpeed() {
        double jumpSpeed = 0.3999999463558197;
        if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            double amplifier = mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
            jumpSpeed += (amplifier + 1) * 0.1;
        }
        return jumpSpeed;
    }

    public static void modifyEventSpeed(PlayerMoveEvent event, double d) {
        double d2 = mc.player.input.movementForward;
        double d3 = mc.player.input.movementSideways;
        float f = mc.player.yaw;
        if (d2 == 0.0 && d3 == 0.0) {
            ((IVec3d) event.movement).setX(0.0);
            ((IVec3d) event.movement).setZ(0.0);
        } else {
            if (d2 != 0.0) {
                if (d3 > 0.0) {
                    f += (float) (d2 > 0.0 ? -45 : 45);
                } else if (d3 < 0.0) {
                    f += (float) (d2 > 0.0 ? 45 : -45);
                }

                d3 = 0.0;
                if (d2 > 0.0) {
                    d2 = 1.0;
                } else if (d2 < 0.0) {
                    d2 = -1.0;
                }
            }
            double sin = Math.sin(Math.toRadians(f + 90.0F));
            double cos = Math.cos(Math.toRadians(f + 90.0F));

            ((IVec3d) event.movement).setX(d2 * d * cos + d3 * d * sin);
            ((IVec3d) event.movement).setZ(d2 * d * sin - d3 * d * cos);
        }
    }

    public static double[] forward(final double d) {
        return forward(mc.player.yaw, d);
    }

    public static double[] forward(double yaw, final double d) {
        return forward(yaw, d, mc.player.input.movementForward, mc.player.input.movementSideways);
    }

    public static double[] forward(double yaw, final double d, float forward, float sideways) {
        float f = forward;
        float f2 = sideways;
        float f3 = (float) yaw;
        if (f != 0.0f) {
            if (f2 > 0.0f) {
                f3 += ((f > 0.0f) ? -45 : 45);
            } else if (f2 < 0.0f) {
                f3 += ((f > 0.0f) ? 45 : -45);
            }
            f2 = 0.0f;
            if (f > 0.0f) {
                f = 1.0f;
            } else if (f < 0.0f) {
                f = -1.0f;
            }
        }
        final double d2 = Math.sin(Math.toRadians(f3 + 90.0f));
        final double d3 = Math.cos(Math.toRadians(f3 + 90.0f));
        final double d4 = f * d * d3 + f2 * d * d2;
        final double d5 = f * d * d2 - f2 * d * d3;
        return new double[]{d4, d5};
    }

    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public static void fixMovement(KeyboardInputEvent event, float yaw) {
        final float forward = event.forward;
        final float strafe = event.sideways;

        final double angle = MathHelper.wrapDegrees(Math.toDegrees(direction(mc.player.isFallFlying() ? yaw : mc.player.yaw, forward, strafe)));

        if (forward == 0 && strafe == 0) return;

        float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

        for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
            for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                if (predictedStrafe == 0 && predictedForward == 0) continue;

                final double predictedAngle = MathHelper.wrapDegrees(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
                final double difference = Math.abs(angle - predictedAngle);

                if (difference < closestDifference) {
                    closestDifference = (float) difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
        }

        event.forward = (closestForward);
        event.sideways = (closestStrafe);
    }

    public static void setMotion(double speed) {
        double forward = mc.player.input.movementForward;
        double strafe = mc.player.input.movementSideways;
        float yaw = mc.player.yaw;
        if (forward == 0 && strafe == 0) {
            ((IVec3d) mc.player.getVelocity()).setXZ(0, 0);
        } else {
            if (forward != 0) {
                if (strafe > 0) {
                    yaw += (float) (forward > 0 ? -45 : 45);
                } else if (strafe < 0) {
                    yaw += (float) (forward > 0 ? 45 : -45);
                }
                strafe = 0;
                if (forward > 0) {
                    forward = 1;
                } else if (forward < 0) {
                    forward = -1;
                }
            }
            double sin = MathHelper.sin((float) Math.toRadians(yaw + 90));
            double cos = MathHelper.cos((float) Math.toRadians(yaw + 90));
            ((IVec3d) mc.player.getVelocity()).setX(forward * speed * cos + strafe * speed * sin);
            ((IVec3d) mc.player.getVelocity()).setZ(forward * speed * sin - strafe * speed * cos);
        }
    }

    public static void setSpeed(double speed) {
        float f = mc.player.input.movementForward;
        float f1 = mc.player.input.movementSideways;
        float f2 = mc.player.yaw;

        if (f == 0.0F && f1 == 0.0F) {
            ((IVec3d) mc.player.getVelocity()).setXZ(0, 0);
        } else if (f != 0.0F) {
            if (f1 >= 1.0F) {
                f2 += (f > 0.0F ? -35 : 35);
                f1 = 0.0F;
            } else if (f1 <= -1.0F) {
                f2 += (f > 0.0F ? 35 : -35);
                f1 = 0.0F;
            }

            if (f > 0.0F) {
                f = 1.0F;
            } else if (f < 0.0F) {
                f = -1.0F;
            }
        }

        double d0 = Math.cos(Math.toRadians(f2 + 90.0F));
        double d1 = Math.sin(Math.toRadians(f2 + 90.0F));
        ((IVec3d) mc.player.getVelocity()).setX(f * speed * d0 + f1 * speed * d1);
        ((IVec3d) mc.player.getVelocity()).setZ(f * speed * d1 - f1 * speed * d0);
    }

    public static void setSpeed(PlayerMoveEvent event, double speed) {
        Vec3d vel = getHorizontalVelocity(speed);
        double velX = vel.getX();
        double velZ = vel.getZ();

        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            double value = (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1) * 0.205;
            velX += velX * value;
            velZ += velZ * value;
        }

        ((IVec3d) event.movement).set(velX, event.movement.y, velZ);
    }

    public static Vec3d getHorizontalVelocity(double bps) {
        float yaw = mc.player.yaw;

        Vec3d forward = Vec3d.fromPolar(0, yaw);
        Vec3d right = Vec3d.fromPolar(0, yaw + 90);
        double velX = 0;
        double velZ = 0;

        boolean a = false;
        if (mc.player.input.pressingForward) {
            velX += forward.x / 20 * bps;
            velZ += forward.z / 20 * bps;
            a = true;
        }
        if (mc.player.input.pressingBack) {
            velX -= forward.x / 20 * bps;
            velZ -= forward.z / 20 * bps;
            a = true;
        }

        boolean b = false;
        if (mc.player.input.pressingRight) {
            velX += right.x / 20 * bps;
            velZ += right.z / 20 * bps;
            b = true;
        }
        if (mc.player.input.pressingLeft) {
            velX -= right.x / 20 * bps;
            velZ -= right.z / 20 * bps;
            b = true;
        }

        if (a && b) {
            velX *= diagonal;
            velZ *= diagonal;
        }

        ((IVec3d) horizontalVelocity).setXZ(velX, velZ);
        return horizontalVelocity;
    }

    public static void setStrafe(double motion) {
        if (!isMoving()) return;
        double radians = getDirection();
        ((IVec3d) mc.player.getVelocity()).setX(-Math.sin(radians) * motion);
        ((IVec3d) mc.player.getVelocity()).setZ(Math.cos(radians) * motion);
    }

    public static float getDirection() {
        return getDirection(mc.player.yaw);
    }

    public static float getDirection(float yaw) {
        float rotationYaw = yaw;

        float strafeFactor = 0f;

        if (mc.player.input.movementForward > 0)
            strafeFactor = 1;
        if (mc.player.input.movementForward < 0)
            strafeFactor = -1;

        if (strafeFactor == 0) {
            if (mc.player.input.movementSideways > 0)
                rotationYaw -= 90;

            if (mc.player.input.movementSideways < 0)
                rotationYaw += 90;
        } else {
            if (mc.player.input.movementSideways > 0)
                rotationYaw -= 45 * strafeFactor;

            if (mc.player.input.movementSideways < 0)
                rotationYaw += 45 * strafeFactor;
        }

        if (strafeFactor < 0)
            rotationYaw -= 180;

        return (float) Math.toRadians(rotationYaw);
    }

    public static float getMoveDirection(Entity entity) {
        double motionX = entity.getVelocity().x;
        double motionZ = entity.getVelocity().z;
        float direction = (float)(Math.atan2(motionX, motionZ) / Math.PI * 180.0D);
        return -direction;
    }

    public static float getMoveDirection() {
        return getMoveDirection(mc.player);
    }

    public static boolean airBlockAboveHead() {
        Box bb = new Box(mc.player.getX() - 0.3, mc.player.getY() + (double) mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ() + 0.3, mc.player.getX() + 0.3, mc.player.getY() + (!mc.player.isOnGround() ? 1.5 : 2.5), mc.player.getZ() - 0.3);
        return mc.world.getCollisions(mc.player, bb, entity -> entity instanceof Entity).iterator().hasNext();
    }

    public static boolean isInWater() {
        return mc.player.isTouchingWater() || mc.player.isSubmergedInWater();
    }

    public static boolean isInLiquid() {
        return mc.world.getBlockState(mc.player.getBlockPos()).getBlock() instanceof FluidBlock || mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava();
    }

    public static void strafe() {
        if (mc.options.keyBack.isPressed()) return;
        MovementUtils.strafe(MovementUtils.getSpeed());
    }

    public static float getSpeed() {
        return (float) Math.sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x + mc.player.getVelocity().z * mc.player.getVelocity().z);
    }

    public static float getAllDirection() {
        float rotationYaw = mc.player.yaw;

        float factor = 0f;

        if (mc.player.input.movementForward > 0)
            factor = 1;
        if (mc.player.input.movementForward < 0)
            factor = -1;

        if (factor == 0) {
            if (mc.player.input.movementSideways > 0)
                rotationYaw -= 90;

            if (mc.player.input.movementSideways < 0)
                rotationYaw += 90;
        } else {
            if (mc.player.input.movementSideways > 0)
                rotationYaw -= 45 * factor;

            if (mc.player.input.movementSideways < 0)
                rotationYaw += 45 * factor;
        }

        if (factor < 0)
            rotationYaw -= 180;

        return (float) Math.toRadians(rotationYaw);
    }

    public static float getMoveYaw() {
        float rotationYaw = mc.player.yaw;

        float factor = 0f;

        if (mc.player.input.movementForward > 0)
            factor = 1;
        if (mc.player.input.movementForward < 0)
            factor = -1;

        if (factor == 0) {
            if (mc.player.input.movementSideways > 0)
                rotationYaw -= 90;

            if (mc.player.input.movementSideways < 0)
                rotationYaw += 90;
        } else {
            if (mc.player.input.movementSideways > 0)
                rotationYaw -= 45 * factor;

            if (mc.player.input.movementSideways < 0)
                rotationYaw += 45 * factor;
        }

        if (factor < 0)
            rotationYaw -= 180;

        return (float) rotationYaw;
    }

    public static void strafe(float speed) {
    	if (mc.options.keyBack.isPressed()) return;
        if (!MovementUtils.isMoving()) return;
        double yaw = MovementUtils.getAllDirection();
        ((IVec3d) mc.player.getVelocity()).setXZ(-Math.sin(yaw) * (double) speed, Math.cos(yaw) * (double) speed);
    }

    public static boolean isMoving() {
        if (mc.player.input == null) return false;
        return mc.player.input.movementSideways != 0.0 || mc.player.input.movementForward != 0.0;
    }

    public static Vec3d getMovementOnKey(double hSpeed, double vSpeed) {
        float yaw = mc.player.yaw;
        Input input = mc.player.input;

        float forward = input.movementForward;
        float strafe = input.movementSideways;

        float vertical = 0f;
        if (mc.options.keyJump.isPressed()) vertical += 1.0f;
        if (mc.options.keySneak.isPressed()) vertical -= 1.0f;

        var tweakedSpeed = hSpeed;
        if (forward != 0f && strafe != 0f) tweakedSpeed *= 0.70710678118;
        double yawRad = Math.toRadians(yaw + 90.0);

        return new Vec3d(
                tweakedSpeed * (forward * Math.cos(yawRad) + strafe * Math.sin(yawRad)),
                vSpeed * vertical,
                tweakedSpeed * (forward * Math.sin(yawRad) - strafe * Math.cos(yawRad))
        );
    }
}
