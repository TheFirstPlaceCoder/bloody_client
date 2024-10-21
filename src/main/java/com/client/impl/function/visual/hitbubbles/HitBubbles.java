package com.client.impl.function.visual.hitbubbles;

import api.interfaces.EventHandler;
import com.client.event.events.AttackEntityEvent;
import com.client.event.events.Render3DEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.ListSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class HitBubbles extends Function {
    private final ListSetting mode = List().name("Режим").defaultValue("Первый").list(List.of("Первый", "Второй")).build();

    public HitBubbles() {
        super("Hit Bubbles", Category.VISUAL);
    }

    private final List<Particle> objects = new ArrayList<>();

    @Override
    public void onEnable() {
        objects.clear();
    }

    @EventHandler
    public void onAttackEntityEvent(AttackEntityEvent.Post event) {
        if (event.entity instanceof LivingEntity) {
            AttackAura aura = FunctionManager.get(AttackAura.class);
            float yaw = mc.player.yaw;
            float pitch = mc.player.pitch;
            double dist = mc.interactionManager.getReachDistance();
            if (aura.isEnabled()) {
                yaw = RotationHandler.serverYaw;
                pitch = RotationHandler.serverPitch;
                dist = aura.range.get();
            }
            Vec3d pos = raycast(dist, mc.getTickDelta(), yaw, pitch, event.entity);
            if (pos == null) return;
            objects.add(new Particle(pos));
        }
    }

    private Vec3d raycast(double maxDistance, float tickDelta, float yaw, float pitch, Entity target) {
        Vec3d vec3d = mc.player.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = getRotationVector(pitch, yaw);
        double d = 0;
        while (d < maxDistance) {
            d += 0.5f;
            Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
            Entity cameraE = mc.getCameraEntity();
            float oldYaw = cameraE.yaw;
            float oldPitch = cameraE.pitch;
            cameraE.yaw = yaw;
            cameraE.pitch = pitch;
            HitResult hr = mc.player.raycast(d, tickDelta, false);
            cameraE.yaw = oldYaw;
            cameraE.pitch = oldPitch;
            if (hr != null) {
                Vec3d hrvec = hr.getPos();
                if (new Box(hrvec.x - 0.2f, hrvec.y - 0.2f, hrvec.z - 0.2f, hrvec.x + 0.2f, hrvec.y + 0.2f, hrvec.z + 0.2f).intersects(target.getBoundingBox())){
                    return vec3d3;
                }
            }
        }
        return null;
    }

    private Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        objects.removeIf(Particle::update);
    }

    @EventHandler
    private void onRender3DEvent(Render3DEvent event) {
        for (Particle object : objects) {
            object.draw(event.getMatrices(), mode.get());
        }
    }
}