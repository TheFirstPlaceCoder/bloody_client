package com.client.impl.function.combat.aura.rotate.handler.handlers;

import com.client.impl.function.combat.aura.rotate.handler.Handler;
import com.client.utils.game.rotate.Rotations;
import com.client.utils.math.vector.floats.V2F;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import static com.client.system.function.Function.mc;

public class EmptyHandler extends Handler {
    public EmptyHandler() {
        super("HvH");
    }

    @Override
    public void tick(Entity target, double range) {
        rotate.a = 0;
        rotate.b = 0;
    }

    private final V2F rotate = new V2F(0, 0);

    @Override
    public void elytraTick(Entity target, double range) {
        V2F vec = getBestPoint(target, range);

        rotate.a = vec.a;
        rotate.b = vec.b;
    }

    @Override
    public V2F getBestPoint(Entity target, double range) {
        if (target == null || target == mc.player) {
            return new V2F(mc.player.yaw, mc.player.pitch);
        }

        Vec3d vec = new Vec3d(target.getX(), target.getY() + target.getHeight() * 0.5F, target.getZ());
        return new V2F((float) Rotations.getYaw(vec), (float) Rotations.getPitch(vec));
    }

    @Override
    public V2F getRotate() {
        return rotate;
    }
}
