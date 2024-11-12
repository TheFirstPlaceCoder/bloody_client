package com.client.impl.function.combat.aura.rotate.handler.handlers;

import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.combat.aura.rotate.AttackAuraUtils;
import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.impl.function.combat.aura.rotate.handler.Handler;
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
    public static double oldY = -1;
    private int aimTicks = 0;
    private float rotationYaw = -9999, rotationPitch = -9999, assistAcceleration, pitchAcceleration;
    private MsTimer visibleTime = new MsTimer();
    public float incrementTicks = 0;
    public V2F currentRot = new V2F(0, 0);

    @Override
    public void tick(Entity target, double range) {
        rotate.a = currentRot.a;
        rotate.b = currentRot.b;
    }

    public void tick1(Entity target, double range, boolean isEnabled) {
        AttackAura aura = FunctionManager.get(AttackAura.class);
        V2F targetRotation = getBestPoint(target, range);

        if (target != null && isEnabled) {
            if (((IGameRenderer) mc.gameRenderer).getTarget(rotate.a, rotate.b) == target || target == mc.player) incrementTicks = 0;

            incrementTicks += aura.increTicks.get().floatValue();
            currentRot.a = circEaseOut(rotate.a, targetRotation.a, incrementTicks * aura.rotSpeed.get().floatValue());
            currentRot.b = circEaseOut(rotate.b, targetRotation.b, incrementTicks * aura.rotSpeed.get().floatValue());
        }
    }

    private float cubicEaseInOut(float start, float end, float percent) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);
        if (percent < 0.5f) {
            return start + change / 2 * percent * percent * percent;
        } else {
            percent -= 1;
            return start + change / (1 - 2 * percent * percent * percent);
        }
    }

    private float circEaseOut(float start, float end, float percent) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return change * (float)Math.sqrt(1 - (percent=percent/FunctionManager.get(AttackAura.class).dCoef.get()-1)*percent) + start;
    }

    private double calculate(double m, double a, double b) {
        double d, s;
        d = MathHelper.wrapDegrees(a - b);
        s = Math.abs(d / m);
        return s * (d >= 0 ? 1 : -1);
    }

    @Override
    public void elytraTick(Entity target, double range) {
        V2F vec = getBestPoint(target, range);

        rotate.a = (float) Utils.lerp(rotate.a, vec.a, Utils.random(0.45, 0.60));

        if (AttackAuraUtils.rayTrace(range, rotate.a, rotate.b).getType() != HitResult.Type.ENTITY) {
            rotate.b = (float) Utils.lerp(rotate.b, vec.b, 0.05);
        }
    }

    @Override
    public V2F getBestPoint(Entity target, double range) {
        if (target == null || target == mc.player) {
            return new V2F(mc.player.yaw, mc.player.pitch);
        }

        Vec3d vec = new Vec3d(target.getX(), target.getY() + target.getHeight() * 0.75F, target.getZ());
        return new V2F((float) Rotations.getYaw(vec), (float) Rotations.getPitch(vec));
    }

    public double getBestPointDynamic(Entity target) {
        if (target == null || target == mc.player) {
            return 0;
        }

        if (target.getY() > mc.player.getY()) {
            return target.getY() + (Utils.random(0, target.getHeight() / 2));
        } else {
            return target.getY() + target.getHeight() * 0.75 + (Utils.random(0, target.getHeight() * 0.25));
        }
    }

    public double getBestPointStatic(Entity target) {
        if (target == null || target == mc.player) {
            return 0;
        }

        return target.getY() + target.getHeight() * 0.75F;
    }

    @Override
    public V2F getRotate() {
        return rotate;
    }
}