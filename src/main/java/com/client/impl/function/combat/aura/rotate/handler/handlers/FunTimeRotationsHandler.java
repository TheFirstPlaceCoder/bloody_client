package com.client.impl.function.combat.aura.rotate.handler.handlers;

import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.combat.aura.rotate.AttackAuraUtils;
import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.impl.function.combat.aura.rotate.handler.Handler;
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

    @Override
    public void tick(Entity target, double range) {
        rotate.a = (float) Utils.lerp(rotate.a, rotationYaw == -9999 ? mc.player.yaw : rotationYaw, assistAcceleration);
        rotate.b = (float) Utils.lerp(rotate.b, rotationPitch == -9999 ? mc.player.pitch : rotationPitch, pitchAcceleration);
    }

    public void tick1(Entity target, double range, boolean isEnabled) {
        AttackAura aura = FunctionManager.get(AttackAura.class);

        if (target != null && isEnabled) {
            if (target == mc.player) {
                rotationYaw = mc.player.yaw;
                rotationPitch = mc.player.pitch;
                return;
            }

            if ((AttackAuraUtils.checkTargetEntity(RotationHandler.serverYaw, RotationHandler.serverPitch, range, target)))
                aimTicks++;
            else
                aimTicks = 0;

            if (aimTicks >= 1) {
                assistAcceleration = 0;
                pitchAcceleration = 0;
                return;
            }

            assistAcceleration += (mc.player.age % 50 == 0 ? Utils.random(28, 30) : 30) / 10000f;
            pitchAcceleration += 18 / 10000f;

            if (!mc.player.canSee(target)) {
                if (!aura.wallsAttack.get())
                    visibleTime.reset();
            }

            if (!visibleTime.passedMs(40)) {
                rotationYaw = -9999;
                rotationPitch = -9999;
                return;
            }

            if (rotationYaw == -9999)
                rotationYaw = mc.player.yaw;

            if (rotationPitch == -9999)
                rotationPitch = mc.player.pitch;

            float delta_yaw = wrapDegrees((float) wrapDegrees(Math.toDegrees(Math.atan2(target.getPos().add(0, target.getEyeHeight(target.getPose()), 0).z - mc.player.getZ(), (target.getPos().add(0, target.getEyeHeight(target.getPose()), 0).x - mc.player.getX()))) - 90) - rotationYaw);
            if (delta_yaw > 180)
                delta_yaw = delta_yaw - 180;
            float deltaYaw = MathHelper.clamp(MathHelper.abs(delta_yaw), -180, 180);
            float newYaw = rotationYaw + (delta_yaw > 0 ? deltaYaw : -deltaYaw);
            double gcdFix = (Math.pow(mc.options.mouseSensitivity * 0.6 + 0.2, 3.0)) * 1.2;
            rotationYaw = (float) (newYaw - (newYaw - rotationYaw) % gcdFix);

            rotationPitch = (float) Rotations.getPitch(target.getPos().add(0, oldY == -1 || mc.player.age % 20 == 0 ? oldY = (target.getY() > mc.player.getY() ? Utils.random(0, target.getHeight() / 2) : Utils.random(target.getHeight() / 2, target.getHeight())) : oldY, 0));
        } else {
            rotationYaw = mc.player.yaw;
            rotationPitch = mc.player.pitch;
            assistAcceleration = 90 / 10000f;
            pitchAcceleration = 54 / 10000f;
        }
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