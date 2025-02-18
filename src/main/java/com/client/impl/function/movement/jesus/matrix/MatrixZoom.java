package com.client.impl.function.movement.jesus.matrix;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.jesus.JesusMode;
import com.client.interfaces.IVec3d;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MatrixZoom extends JesusMode {
    private final float range = 0.005f;

    @Override
    public void tick(TickEvent.Pre event) {
        float yaw = mc.player.yaw;
        Vec3d forward = Vec3d.fromPolar(0, yaw);
        Vec3d right = Vec3d.fromPolar(0, yaw + 90);

        double velX = 0;
        double velZ = 0;
        double s = 0.5;
        double speedValue = settings.speed.get();

        if (mc.options.keyForward.isPressed()) {
            velX += forward.x * s * speedValue;
            velZ += forward.z * s * speedValue;
        }
        if (mc.options.keyBack.isPressed()) {
            velX -= forward.x * s * speedValue;
            velZ -= forward.z * s * speedValue;
        }

        if (mc.options.keyRight.isPressed()) {
            velX += right.x * s * speedValue;
            velZ += right.z * s * speedValue;
        }
        if (mc.options.keyLeft.isPressed()) {
            velX -= right.x * s * speedValue;
            velZ -= right.z * s * speedValue;
        }
        if (mc.world.getBlockState(new BlockPos(mc.player.getBlockPos().getX(), (int) (mc.player.getBlockPos().getY() + range), mc.player.getBlockPos().getZ())).getBlock() == Blocks.WATER && !mc.player.horizontalCollision) {
            ((IVec3d) mc.player.getVelocity()).set(velX, 0, velZ);
        }
    }
}
