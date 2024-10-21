package com.client.utils.math.vector;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class Vector3d {
    public double x, y, z;

    public Vector3d() {}

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public Vector3d set(Vector3d vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;

        return this;
    }

    public Vector3d set(Vec3d vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;

        return this;
    }

    public Vector3d set(Entity entity, double tickDelta) {
        x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
        y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
        z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());

        return this;
    }

    public Vector3d add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;

        return this;
    }

    public Vector3d add(Vector3d vec) {
        return add(vec.x, vec.y, vec.z);
    }

    public Vector3d subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;

        return this;
    }

    public Vector3d subtract(Vec3d vec) {
        return subtract(vec.x, vec.y, vec.z);
    }

    public Vector3d multiply(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;

        return this;
    }

    public Vector3d multiply(double v) {
        return multiply(v, v, v);
    }

    public Vector3d divide(double v) {
        x /= v;
        y /= v;
        z /= v;

        return this;
    }

    public void negate() {
        x = -x;
        y = -y;
        z = -z;
    }

    public double distanceTo(Vector3d vec) {
        double d = vec.x - x;
        double e = vec.y - y;
        double f = vec.z - z;

        return Math.sqrt(d * d + e * e + f * f);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3d normalize() {
        return divide(length());
    }

    public BlockPos toBlockPos() {
        return new BlockPos((int) x, (int) y, (int) z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3d vector3D = (Vector3d) o;
        return Double.compare(vector3D.x, x) == 0 && Double.compare(vector3D.y, y) == 0 && Double.compare(vector3D.z, z) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("[%.3f, %.3f, %.3f]", x, y, z);
    }
}
