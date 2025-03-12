package com.client.impl.function.combat.aura.rotate.handler.handlers.grim;

import com.client.impl.function.combat.aura.rotate.handler.Handler;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.game.rotate.Rotations;
import com.client.utils.math.MathUtils;
import com.client.utils.math.vector.floats.V2F;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.*;

import static com.client.BloodyClient.mc;

public class GrimRotationsHandler extends Handler {

    public GrimRotationsHandler() {
        super("Grim");
    }

    private final Random RANDOM = new Random();

    private final V2F rotate = new V2F(0, 0);

    private final Map<Integer, List<Float>> RANDOMIZE = new HashMap<>();

    @Override
    public void tick(Entity target, double range) {
        V2F vec = getBestPoint(target, range);

        boolean flag = mc.player.isFallFlying();

        double s = MovementUtils.isInWater() ? 1.4D : 1.8D;

        s = func(s);

        double mul = (flag ? MathUtils.random(func(1.4d), func(1.8d)) : s);

        if (target.equals(mc.player)) {
            mul = (flag ? func(2.9D) : func(5.9D));
        }

        rotate.a += (float) (calculate(mul, vec.a, rotate.a));
        rotate.b += (float) (calculate(mul, vec.b, rotate.b));
    }

    @Override
    public void elytraTick(Entity target, double range) {
        tick(target, range);
    }

    private double calculate(double m, double a, double b) {
        double d, s;
        d = MathHelper.wrapDegrees(a - b);
        s = Math.abs(d / m);
        return s * (d >= 0 ? 1 : -1);
    }

    private double func(double in) {
        int i = (int) in;
        float at = RANDOM.nextFloat();

        if (!RANDOMIZE.containsKey(i)) {
            List<Float> floats = new ArrayList<>();
            floats.add(at);

            RANDOMIZE.put(i, floats);

            return in + at;
        }

        List<Float> floats = RANDOMIZE.get(i);

        while (floats.contains(at)) {
            at = RANDOM.nextFloat();
        }

        floats.add(at);
        return in + at;
    }

    @Override
    public V2F getBestPoint(Entity target, double range) {
        if (target == null || target == mc.player) {
            return new V2F(mc.player.yaw, mc.player.pitch);
        }

        Vec3d position = target.getPos().add(0D, target.getHeight() * MathUtils.random(0.3f, 0.7f), 0D);

        return new V2F((float) (Rotations.getYaw(position) + MathUtils.offset(1.2f)), (float) (Rotations.getPitch(position) + MathUtils.offset(1.2f)));
    }

    @Override
    public V2F getRotate() {
        return rotate;
    }
}
