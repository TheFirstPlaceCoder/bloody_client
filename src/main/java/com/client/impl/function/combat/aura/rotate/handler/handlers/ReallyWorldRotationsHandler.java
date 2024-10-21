package com.client.impl.function.combat.aura.rotate.handler.handlers;

import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.combat.aura.rotate.handler.Handler;
import com.client.system.function.FunctionManager;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.game.rotate.Rotations;
import com.client.utils.math.MathUtils;
import com.client.utils.math.vector.floats.V2F;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static com.client.BloodyClient.mc;

public class ReallyWorldRotationsHandler extends Handler {

    public ReallyWorldRotationsHandler() {
        super("ReallyWorld");
    }

    private final V2F rotate = new V2F(0, 0);
    private long time = 0;

    @Override
    public void tick(Entity target, double range) {
        V2F vec = getBestPoint(System.currentTimeMillis() > time ? mc.player : target, range);

        if (FunctionManager.get(AttackAura.class).testHand() && FunctionManager.get(AttackAura.class).canAttack(false) && target != mc.player) {
            time = System.currentTimeMillis() + (FunctionManager.get(AttackAura.class).tick.get() * 50);
        }

        rotate.a = vec.a;
        rotate.b = vec.b;
    }

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

        Vec3d position = target.getPos().add(0, MathHelper.clamp((PlayerUtils.distanceTo(target) / range) * (target.getHeight()), 0D, target.getHeight()), 0);

        return new V2F((float) (Rotations.getYaw(position) + MathUtils.offset(1.5)), (float) (Rotations.getPitch(position) + MathUtils.offset(2)));
    }

    @Override
    public V2F getRotate() {
        return rotate;
    }
}
