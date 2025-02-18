package com.client.impl.function.movement;

import com.client.event.events.PacketEvent;
import com.client.event.events.PlayerMoveEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.interfaces.IClientPlayerEntity;
import com.client.interfaces.IVec3d;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.movement.MovementUtils;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Iterator;
import java.util.List;

public class Strafe extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Обычный", "Matrix", "Grim")).defaultValue("Grim").build();

    public Strafe() {
        super("Strafe", Category.MOVEMENT);
    }

    public static double oldSpeed, contextFriction;
    public static boolean needSwap, needSprintState, disabled;
    public static int noSlowTicks;

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent var1) {
        if (!mode.get().equals("Обычный")) {
            Iterator var2 = mc.world.getPlayers().iterator();

            while(true) {
                Vec3d var3;
                PlayerEntity var4;
                float var5;
                do {
                    do {
                        do {
                            do {
                                if (!var2.hasNext()) {
                                    return;
                                }

                                var4 = (PlayerEntity)var2.next();
                            } while(mc.player == var4);

                            if (mode.get().equals("Grim") && mc.player.distanceTo(var4) <= 2.0F && (mc.options.keyForward.isPressed() || mc.options.keyRight.isPressed() || mc.options.keyLeft.isPressed() || mc.options.keyBack.isPressed())) {
                                var5 = 1.185F;
                                var3 = mc.player.getVelocity();
                                mc.player.setVelocity(var3.x * var5, var3.y, var3.z * var5);
                            }
                        } while(!mode.get().equals("Matrix"));
                    } while(mc.player.distanceTo(var4) > 2.0F);
                } while(!mc.options.keyForward.isPressed() && !mc.options.keyRight.isPressed() && !mc.options.keyLeft.isPressed() && !mc.options.keyBack.isPressed());

                var5 = 1.15F;
                var3 = mc.player.getVelocity();
                mc.player.setVelocity(var3.x * var5, var3.y, var3.z * var5);
            }
        }
    }

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
        if (!mc.player.isOnGround()) {
            needSprintState = !((IClientPlayerEntity) mc.player).lastSprinting();
            needSwap = true;
        } else needSprintState = false;
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
    }

    public boolean canStrafe() {
        if (mc.player.isSneaking()) {
            return false;
        }
        if (mc.player.isInLava()) {
            return false;
        }
        if (FunctionManager.isEnabled("Speed")) {
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
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (canStrafe()) {
            if (MovementUtils.isMoving()) {
                double[] motions = forward(calculateSpeed(event));

                ((IVec3d) event.movement).setX(motions[0]);
                ((IVec3d) event.movement).setZ(motions[1]);
            } else {
                oldSpeed = 0;
                ((IVec3d) event.movement).setX(0);
                ((IVec3d) event.movement).setZ(0);
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
    public void tick(TickEvent.Pre e) {
        oldSpeed = Math.hypot(mc.player.getX() - mc.player.prevX, mc.player.getZ() - mc.player.prevZ) * contextFriction;
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (e.packet instanceof PlayerPositionLookS2CPacket) {
            oldSpeed = 0;
        }
    }

    @Override
    public String getHudPrefix() {
        return mode.get();
    }
}
