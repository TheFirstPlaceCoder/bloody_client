package com.client.impl.function.combat.aura.rotate.handler.handlers;

import com.client.impl.function.combat.aura.rotate.handler.Handler;
import com.client.interfaces.IGameRenderer;
import com.client.utils.Utils;
import com.client.utils.game.rotate.Rotations;
import com.client.utils.math.vector.floats.V2F;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

import static com.client.system.function.Function.mc;

public class FunTimeRotationsHandler extends Handler {

    public FunTimeRotationsHandler() {
        super("FunTime");
    }

    private final V2F rotate = new V2F(0, 0);
    private final Random random = new Random();
    private double hitBoxOffset = 0.3, xOffset, zOffset;

    @Override
    public void tick(Entity target, double range) {
        V2F vec = getBestPoint(target, range);

        rotate.a = (float) Utils.lerpCircular(rotate.a, vec.a, (float) Utils.random(0.2, 0.3));

        if (((IGameRenderer) mc.gameRenderer).getTarget(rotate.a, rotate.b) != target || System.currentTimeMillis() % 500 == 0)
            rotate.b = (float) Utils.lerp(rotate.b, vec.b, (float) Utils.random(0.1, 0.15));
    }

    @Override
    public void elytraTick(Entity target, double range) {
        // You might need a different rotation logic for elytra flight
        tick(target, range); // For now, just use the same
    }

    @Override
    public V2F getBestPoint(Entity target, double range) {
        if (target == null || target == mc.player) {
            return new V2F(mc.player.yaw, mc.player.pitch);
        }

        // Calculate random hit point within the hitbox
        if (xOffset == 0 || zOffset == 0 || System.currentTimeMillis() % 2000 == 0) {
            xOffset = (random.nextDouble() * 2 - 1) * hitBoxOffset;
            zOffset = (random.nextDouble() * 2 - 1) * hitBoxOffset;
        }

        Vec3d targetPos = target.getPos().add(xOffset, target.getHeight() * (target.getY() > mc.player.getY() + mc.player.getHeight() / 2 ? 0.25F : 0.8F), zOffset); //Randomize point

        return new V2F((float) Rotations.getYaw(targetPos), (float) Rotations.getPitch(targetPos));
    }


    @Override
    public V2F getRotate() {
        return rotate;
    }
}