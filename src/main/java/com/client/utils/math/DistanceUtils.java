package com.client.utils.math;

import com.client.utils.math.vector.Vec3;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static com.client.system.function.Function.mc;

public class DistanceUtils {
    public static double distanceTo(Vec3d start, Vec3d end) {
        return squaredDistanceTo(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ());
    }

    public static double distanceTo(Vec3 pos) {
        return distanceTo(mc.player, pos);
    }

    public static double distanceTo(Vec3d pos) {
        return distanceTo(mc.player, pos);
    }

    public static double distanceTo(BlockPos pos) {
        return distanceTo(mc.player, pos);
    }

    public static double distanceTo(Entity entity, Vec3d pos) {
        return squaredDistanceTo(entity, pos.getX(), pos.getY(), pos.getZ());
    }

    public static double distanceTo(Entity entity, Vec3 pos) {
        return squaredDistanceTo(entity, pos.x, pos.y, pos.z);
    }

    public static double distanceTo(Entity entity, BlockPos pos) {
        return squaredDistanceTo(entity, pos.getX(), pos.getY(), pos.getZ());
    }

    public static double squaredDistanceTo(double x, double y, double z, double x2, double y2, double z2) {
        return squaredDistance(x, y, z, x2, y2, z2);
    }

    public static double squaredDistanceTo(Entity entity, double x, double y, double z) {
        return squaredDistance(entity.getX(), entity.getY(), entity.getZ(), x, y, z);
    }

    public static double squaredDistanceTo(double x, double y, double z) {
        return squaredDistance(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ(), x, y, z);
    }

    public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dX = x2 - x1;
        double dY = y2 - y1;
        double dZ = z2 - z1;
        return dX * dX + dY * dY + dZ * dZ;
    }
}
