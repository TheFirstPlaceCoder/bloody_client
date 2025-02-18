package com.client.impl.function.combat.aura.rotate.handler.handlers;

import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.combat.aura.rotate.AttackAuraUtils;
import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.impl.function.combat.aura.rotate.handler.Handler;
import com.client.impl.function.combat.aura.rotate.handler.Interpolates;
import com.client.interfaces.IGameRenderer;
import com.client.interfaces.IInGameHud;
import com.client.system.function.FunctionManager;
import com.client.utils.Utils;
import com.client.utils.game.rotate.Rotations;
import com.client.utils.math.MsTimer;
import com.client.utils.math.vector.floats.V2F;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static com.client.system.function.Function.mc;
import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class FunTimeRotationsHandler extends Handler {

    public FunTimeRotationsHandler() {
        super("FunTime");
    }

    private static final V2F rotate = new V2F(0, 0);
    public float incrementTicks = 0;
    public V2F currentRot = new V2F(0, 0);
    public Interpolates interpolates = new Interpolates();
    AttackAura aura;

    @Override
    public void tick(Entity target, double range) {
        rotate.a = currentRot.a;
        rotate.b = currentRot.b;
    }

    public void tick1(Entity target, double range, boolean isEnabled) {
        if (aura == null) aura = FunctionManager.get(AttackAura.class);
        V2F targetRotation = getBestPoint(target, range);

        if (target != null && isEnabled) {
            if (((IGameRenderer) mc.gameRenderer).getTarget(rotate.a, rotate.b) == target || target == mc.player) incrementTicks = 0;

            incrementTicks += 0.05;
            currentRot.a = interpolates.calculateInterpolate(aura.boostMode.get(), rotate.a, targetRotation.a, incrementTicks);

            if (((IGameRenderer) mc.gameRenderer).getTarget(rotate.a, rotate.b) != target)
                currentRot.b = (float) Utils.lerp(rotate.b, targetRotation.b, 0.1);
        }
    }

    @Override
    public void elytraTick(Entity target, double range) {
        if (aura == null) aura = FunctionManager.get(AttackAura.class);
        V2F targetRotation = getBestPoint(target, range);

        if (target != null) {
            if (((IGameRenderer) mc.gameRenderer).getTarget(rotate.a, rotate.b) == target || target == mc.player) incrementTicks = 0;

            incrementTicks += 0.05;
            rotate.a = interpolates.calculateInterpolate(aura.boostMode.get(), rotate.a, targetRotation.a, incrementTicks);

            if (((IGameRenderer) mc.gameRenderer).getTarget(rotate.a, rotate.b) != target)
                rotate.b = (float) Utils.lerp(rotate.b, targetRotation.b, 0.1);
        }
    }

    @Override
    public V2F getBestPoint(Entity target, double range) {
        if (target == null || target == mc.player) {
            return new V2F(mc.player.yaw, mc.player.pitch);
        }

        Vec3d vec = new Vec3d(target.getX(), target.getY() + target.getHeight() * (target.getY() > mc.player.getY() ? 0.25F : 0.75F), target.getZ());
        return new V2F((float) Rotations.getYaw(vec), (float) Rotations.getPitch(vec));
    }

    @Override
    public V2F getRotate() {
        return rotate;
    }
}