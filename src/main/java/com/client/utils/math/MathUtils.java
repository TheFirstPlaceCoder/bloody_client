package com.client.utils.math;

import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

import static com.client.BloodyClient.mc;

public class MathUtils {
    public static final Random RANDOM = new Random();

    public static int random(int a, int b) {
        int i = RANDOM.nextInt(b);
        return a < 0 ? new Random().nextBoolean() ? -i : i : Math.max(a, i);
    }

    public static float random(float a, float b) {
        float i = RANDOM.nextFloat();
        return a < 0 ? new Random().nextBoolean() ? -i * a : i * b : Math.max(a, i * b);
    }

    public static double random(double a, double b) {
        double i = RANDOM.nextDouble();
        return a < 0 ? new Random().nextBoolean() ? -i * a : i * b : Math.max(a, i * b);
    }

    public static double offset(double range) {
        int random = RANDOM.nextInt(2);
        return random == 1 ? new Random().nextDouble() * range : -(new Random().nextDouble() * range);
    }

    public static float offset(float range) {
        int random = RANDOM.nextInt(2);
        return random == 1 ? new Random().nextFloat() * range : -(new Random().nextFloat() * range);
    }

    public static double reverse(double v) {
        return v > 0 ? -v : Math.abs(v);
    }

    public static float reverse(float v) {
        return v > 0 ? -v : Math.abs(v);
    }

    public static float getRotations(BlockPos pos) {
        Vec3d en = Vec3d.of(pos).add(0.5, 0.5, 0.5);
        Vec3d sf = Renderer3D.getSmoothPos(mc.player);
        return getRotations(en.getX(), en.getZ(), sf.getX(), sf.getZ());
    }

    public static float getRotations(Entity ent) {
        Vec3d en = Renderer3D.getSmoothPos(ent);
        Vec3d sf = Renderer3D.getSmoothPos(mc.player);
        return getRotations(en.getX(), en.getZ(), sf.getX(), sf.getZ());
    }

    public static float getRotations(double x, double y, double x1, double y1) {
        double diffX = x - x1;
        double diffZ = y - y1;
        return (float)(-(Math.atan2(diffX, diffZ) * 57.29577951308232));
    }

    public static int lerp(int start, int end) {
        return (int) MathHelper.lerp(mc.getTickDelta(), start, end);
    }

    public static float lerp(float start, float end) {
        return MathHelper.lerp(mc.getTickDelta(), start, end);
    }

    public static double lerp(double start, double end) {
        return MathHelper.lerp(mc.getTickDelta(), start, end);
    }

    public static int lerp(double delta, int start, int end) {
        return (int) MathHelper.lerp(delta, start, end);
    }

    public static float lerp(double delta, float start, float end) {
        return (float) MathHelper.lerp(delta, start, end);
    }

    public static double lerp(double delta, double start, double end) {
        return MathHelper.lerp(delta, start, end);
    }
}