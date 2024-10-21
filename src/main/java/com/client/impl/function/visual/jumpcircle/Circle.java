package com.client.impl.function.visual.jumpcircle;

import com.client.utils.math.MsTimer;
import net.minecraft.util.math.Vec3d;

public class Circle {
    public final Vec3d vec;
    public final MsTimer timer = new MsTimer();

    public Circle(Vec3d vec) {
        this.vec = vec;
        timer.reset();
    }

    public Vec3d position() {
        return this.vec;
    }

    public boolean update(long life) {
        return timer.passedMs(life * 1000);
    }
}