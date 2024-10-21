package com.client.utils.math.animation;

import com.client.interfaces.IMinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static com.client.system.function.Function.mc;

public class AnimationUtils {
    public static float fast(float end, float start) {
        return (1 - MathHelper.clamp((float) (deltaTime() * (float) 6), 0, 1)) * end
                + MathHelper.clamp((float) (deltaTime() * (float) 6), 0, 1) * start;
    }

    public static double fast(double end, double start) {
        return (1 - MathHelper.clamp((float) (deltaTime() * (float) 6), 0, 1)) * end
                + MathHelper.clamp((float) (deltaTime() * (float) 6), 0, 1) * start;
    }

    public static float fast(float end, float start, float multiple) {
        return (1 - MathHelper.clamp((float) (deltaTime() * multiple), 0, 1)) * end
                + MathHelper.clamp((float) (deltaTime() * multiple), 0, 1) * start;
    }

    public static double fast(double end, double start, double multiple) {
        return (1 - MathHelper.clamp((float) (deltaTime() * multiple), 0, 1)) * end
                + MathHelper.clamp((float) (deltaTime() * multiple), 0, 1) * start;
    }

    public static Vec3d fast(Vec3d end, Vec3d start, float multiple) {
        return new Vec3d(
                fast((float) end.getX(), (float) start.getX(), multiple),
                fast((float) end.getY(), (float) start.getY(), multiple),
                fast((float) end.getZ(), (float) start.getZ(), multiple));
    }

    public static double deltaTime() {
        return ((IMinecraftClient) mc).getFPS() > 0 ? (1.0000 / ((IMinecraftClient) mc).getFPS()) : 1;
    }
}
