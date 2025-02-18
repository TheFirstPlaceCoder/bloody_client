package com.client.impl.function.movement;

import com.client.event.events.PacketEvent;
import com.client.event.events.PlayerMoveEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.interfaces.IClientPlayerEntity;
import com.client.interfaces.IEntityVelocityUpdateS2CPacket;
import com.client.interfaces.IVec3d;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.game.movement.MovementUtils;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class DamageBoost extends Function {
    public final IntegerSetting boostTicks = Integer().name("Тики буста").enName("Boost Ticks").defaultValue(5).min(0).max(10).build();
    public final DoubleSetting velReduction = Double().name("Скорость").enName("Boost Speed").defaultValue(6.0).min(0.1).max(10).build();
    public final DoubleSetting maxVelocity = Double().name("Максимальное ускорение").enName("Max Speed").defaultValue(0.8).min(0.1).max(5).build();

    public DamageBoost() {
        super("Damage Boost", Category.MOVEMENT);
    }

    public static double oldSpeed, contextFriction;
    public static boolean disabled, isDamaged;
    public static int noSlowTicks, ticks;

    public double calculateSpeed(PlayerMoveEvent move) {
        float speedAttributes = getAIMoveSpeed();
        final float frictionFactor = mc.world.getBlockState(new BlockPos.Mutable().set(mc.player.getX(), getBoundingBox().getMin(Direction.Axis.Y) - move.movement.getY(), mc.player.getZ())).getBlock().getSlipperiness() * 0.91F;
        float n6 = mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST) && mc.player.isUsingItem() ? 0.88f : (float) (oldSpeed > 0.32 && mc.player.isUsingItem() ? 0.88 : 0.91F);
        if (mc.player.isOnGround())
            n6 = frictionFactor;

        float n7 = (float) (0.1631f / Math.pow(n6, 3.0f));
        float n8;
        if (mc.player.isOnGround()) {
            n8 = speedAttributes * n7;
            if (move.movement.getY() > 0)
                n8 += 0.2f;
            disabled = false;
        } else n8 = 0.0255f;

        boolean noslow = false;
        double max2 = oldSpeed + n8;
        double max = 0.0;

        if (mc.player.isUsingItem() && move.movement.getY() <= 0) {
            double n10 = oldSpeed + n8 * 0.25;
            double motionY2 = move.movement.getY();
            if (motionY2 != 0.0 && Math.abs(motionY2) < 0.08) {
                n10 += 0.055;
            }
            if (max2 > (max = Math.max(0.043, n10))) {
                noslow = true;
                ++noSlowTicks;
            } else {
                noSlowTicks = Math.max(noSlowTicks - 1, 0);
            }
        } else {
            noSlowTicks = 0;
        }

        if (noSlowTicks > 3) max2 = max - 0.019;
        else max2 = Math.max(noslow ? 0 : 0.25, max2) - (mc.player.age % 2 == 0 ? 0.001 : 0.002);

        contextFriction = n6;
        return max2;
    }

    public float getAIMoveSpeed() {
        boolean prevSprinting = mc.player.isSprinting();
        mc.player.setSprinting(false);
        float speed = mc.player.getMovementSpeed() * 1.3f;
        mc.player.setSprinting(prevSprinting);
        return speed;
    }

    @Override
    public void onEnable() {
        oldSpeed = 0.0;
        ticks = 0;
        isDamaged = false;
    }

    public boolean canStrafe() {
        if (mc.player.isSneaking()) {
            return false;
        }
        if (mc.player.isInLava()) {
            return false;
        }
        if (FunctionManager.get(Speed.class).isEnabled()) {
            return false;
        }
        if (mc.player.isSubmergedInWater()) {
            return false;
        }
        return !mc.player.abilities.flying;
    }

    public Box getBoundingBox() {
        return new Box(mc.player.getX() - 0.1, mc.player.getY(), mc.player.getZ() - 0.1, mc.player.getX() + 0.1, mc.player.getY() + 1, mc.player.getZ() + 0.1);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (isDamaged) {
            if (ticks >= boostTicks.get()) {
                isDamaged = false;
                ticks = 0;
            }
            else ticks++;
        }
    }

    @Override
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (canStrafe() && isDamaged) {
            if (MovementUtils.isMoving()) {
                double[] motions = forward(calculateSpeed(event));

                ((IVec3d) event.movement).setX(motions[0]);
                ((IVec3d) event.movement).setZ(motions[1]);
            }
            event.cancel();
        } else {
            oldSpeed = 0;
        }
    }

    public static double[] forward(double d) {
        float f = mc.player.input.movementForward;
        float f2 = mc.player.input.movementSideways;
        float f3 =  mc.player.yaw;
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

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent.Post e) {
        oldSpeed = Math.hypot(mc.player.getX() - mc.player.prevX, mc.player.getZ() - mc.player.prevZ) * contextFriction;
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (e.packet instanceof PlayerPositionLookS2CPacket) {
            oldSpeed = 0;
            isDamaged = false;
        }

        if (e.packet instanceof EntityVelocityUpdateS2CPacket p && p.getId() == mc.player.getEntityId()) {
            if (mc.player.isOnGround()) return;

            isDamaged = true;

            int vX = p.getVelocityX();
            int vZ = p.getVelocityZ();

            if (vX < 0) vX *= -1;
            if (vZ < 0) vZ *= -1;

            oldSpeed = (vX + vZ) / (velReduction.get() * 1000f);
            oldSpeed = Math.min(oldSpeed, maxVelocity.get());

            ((IEntityVelocityUpdateS2CPacket) p).setX(0);
            ((IEntityVelocityUpdateS2CPacket) p).setY(0);
            ((IEntityVelocityUpdateS2CPacket) p).setZ(0);
        }
    }
}
