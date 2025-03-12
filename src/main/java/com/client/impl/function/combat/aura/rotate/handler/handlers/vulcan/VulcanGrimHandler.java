package com.client.impl.function.combat.aura.rotate.handler.handlers.vulcan;

import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.combat.aura.rotate.handler.Handler;
import com.client.interfaces.IGameRenderer;
import com.client.system.function.FunctionManager;
import com.client.utils.Utils;
import com.client.utils.game.rotate.Rotations;
import com.client.utils.math.vector.floats.V2F;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import static com.client.system.function.Function.mc;

public class VulcanGrimHandler extends Handler {
    public VulcanGrimHandler() {
        super("VulcanGrim");
    }

    private final V2F rotate = new V2F(0, 0);
    AttackAura aura;

    @Override
    public void tick(Entity target, double range) {
        if (aura == null) aura = FunctionManager.get(AttackAura.class);
        V2F vec = getBestPoint(target, range);

        rotate.a = (float) Utils.lerpCircular(rotate.a, vec.a, (float) Utils.random(0.2, 0.3));

        if (((IGameRenderer) mc.gameRenderer).getTarget(rotate.a, rotate.b) != target)
            rotate.b = (float) Utils.lerpCircular(rotate.b, vec.b, (float) Utils.random(0.2, 0.7));
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

        Vec3d vec = new Vec3d(target.getX(), target.getY() + target.getHeight() * 0.8F, target.getZ());
        return new V2F((float) Rotations.getYaw(vec), (float) Rotations.getPitch(vec));
    }

    @Override
    public V2F getRotate() {
        return rotate;
    }
}