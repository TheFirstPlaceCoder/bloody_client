package com.client.interfaces;

import net.minecraft.util.math.Vec3i;

public interface IVec3d {
    void set(double x, double y, double z);
    void setX(double x);
    void setY(double y);
    void setZ(double z);
    void setXZ(double x, double z);

    default void set(Vec3i vec) {
        set(vec.getX(), vec.getY(), vec.getZ());
    }
}
