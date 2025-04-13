package com.client.impl.function.visual.particles;

import com.client.event.events.AttackEntityEvent;
import com.client.event.events.LostOfTotemEvent;
import com.client.event.events.Render3DEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.math.MathUtils;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * __aaa__
 * 22.05.2024
 * */
public class Particles extends Function {
    public Particles() {
        super("Particles", Category.VISUAL);
    }
    public final MultiBooleanSetting type = MultiBoolean().name("Партиклы").enName("Particles").defaultValue(List.of(
            new MultiBooleanValue(false, "Звездочки"),
            new MultiBooleanValue(false, "Сердечки"),
            new MultiBooleanValue(false, "Снежинки"),
            new MultiBooleanValue(false, "Треугольники"),
            new MultiBooleanValue(false, "Кружки")
    )).build();

    private final MultiBooleanSetting add = MultiBoolean().name("Добавлять").enName("Add when").defaultValue(List.of(
            new MultiBooleanValue(false, "При потери тотема"),
            new MultiBooleanValue(false, "При ходьбе"),
            new MultiBooleanValue(false, "При атаке"),
            new MultiBooleanValue(false, "К жемчугу"),
            new MultiBooleanValue(false, "К трезубцу"),
            new MultiBooleanValue(false, "К стреле")
    )).build();

    public final BooleanSetting firstPerspective = Boolean().name("Видеть от первого лица").enName("See while walking").defaultValue(true).visible(() -> add.get("При ходьбе")).build();
    public final BooleanSetting stopWhileAttacking = Boolean().name("Застывать при атаке").enName("Stop while attacking").defaultValue(true).visible(() -> add.get("При атаке")).build();

    public final DoubleSetting particleSpeed = Double().name("Скорость").enName("Speed").defaultValue(1.0).min(0).max(2).c(1).build();

    private final List<Particle> particles = new ArrayList<>();
    private long lastHit = 0;

    @Override
    public void onEnable() {
        particles.clear();
        lastHit = 0;
    }

    @Override
    public void tick(TickEvent.Post event) {
        particles.removeIf(Particle::update);
    }

    @Override
    public void onLostOfTotemEvent(LostOfTotemEvent event) {
        if (add.get("При потери тотема")) {
            for (int i = 0; i < 50; i++) {
                Vec3d pos = event.entity.getPos();
                Particle particle = new Particle(pos.x, pos.y + 0.2F + (new Random().nextFloat() * (mc.player.getHeight() - 0.2F)), pos.z, modifySpeed(50), false, true);
                particles.add(particle);
            }
        }
    }

    @Override
    public void onAttackEntityEvent(AttackEntityEvent.Post event) {
        if (add.get("При атаке") && event.entity instanceof LivingEntity && System.currentTimeMillis() > lastHit) {
            Vec3d pos = event.entity.getPos();
            for (int i = 0; i < 20; i++) {
                float x, z;
                if (stopWhileAttacking.get()) {
                    x = event.entity.getMovementDirection().getOpposite().getOffsetX() * (new Random().nextDouble() > 0.5 ? -event.entity.getWidth() : event.entity.getWidth());
                    z = event.entity.getMovementDirection().getOpposite().getOffsetZ() * (new Random().nextDouble() > 0.5 ? -event.entity.getWidth() : event.entity.getWidth());
                } else {
                    x = event.entity.getMovementDirection().getOpposite().getOffsetX() * (event.entity.getWidth() / 2);
                    z = event.entity.getMovementDirection().getOpposite().getOffsetZ() * (event.entity.getWidth() / 2);
                }

                Particle particle = new Particle(pos.x + x, pos.y + (event.entity.getHeight() * new Random().nextDouble()), pos.z + z, stopWhileAttacking.get() ? 1000 : modifySpeed(50), false, false);
                particles.add(particle);
            }

            lastHit = System.currentTimeMillis() + 100L;
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (add.get("При ходьбе")) {
            if ((mc.player.prevX != mc.player.getX() || mc.player.prevZ != mc.player.getZ() || mc.player.prevY != mc.player.getY()) && mc.player.age % 2 == 0 && (firstPerspective.get() || !mc.options.getPerspective().equals(Perspective.FIRST_PERSON))) {
                Vec3d pos = mc.player.getPos();
                for (int i = 0; i < 2; i++) {
                    float x = mc.player.getMovementDirection().getOpposite().getOffsetX() * (mc.player.getWidth() / 2);
                    float z = mc.player.getMovementDirection().getOpposite().getOffsetZ() * (mc.player.getWidth() / 2);
                    particles.add(new Particle(pos.x + x, pos.y + 0.2F + (new Random().nextFloat() * (mc.player.getHeight() - 0.2F)), pos.z + z, modifySpeed(70), false, false));
                }
            }
        }

        if (add.get("К жемчугу")) {
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof EnderPearlEntity) {
                    Vec3d pos = entity.getPos();
                    particles.add(new Particle(pos.x, pos.y + entity.getHeight() / 2, pos.z, modifySpeed(50), false, false));
                }
            }
        }

        if (add.get("К трезубцу")) {
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof TridentEntity tridentEntity) {
                    if (tridentEntity.getX() != tridentEntity.prevX || tridentEntity.getY() != tridentEntity.prevY || tridentEntity.getZ() != tridentEntity.prevZ) {
                        Vec3d pos = entity.getPos();
                        particles.add(new Particle(pos.x, pos.y + entity.getHeight() / 2, pos.z, modifySpeed(50), false, false));
                    }
                }
            }
        }

        if (add.get("К стреле")) {
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof ArrowEntity arrowEntity) {
                    if (arrowEntity.getX() != arrowEntity.prevX || arrowEntity.getY() != arrowEntity.prevY || arrowEntity.getZ() != arrowEntity.prevZ) {
                        Vec3d pos = entity.getPos();
                        particles.add(new Particle(pos.x, pos.y + entity.getHeight() / 2, pos.z, modifySpeed(50), false, false));
                    }
                }
            }
        }

        for (Particle particle : particles) {
            particle.draw(event.getMatrices());
        }
    }

    public double modifySpeed(double speed) {
        double normalizedSpeed = particleSpeed.get() / 2;
        double reductionFactor = 1 - normalizedSpeed;

        return speed * (0.5 + reductionFactor);
    }
}