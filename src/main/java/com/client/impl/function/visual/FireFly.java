package com.client.impl.function.visual;

import com.client.event.events.Render3DEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.*;
import com.client.system.textures.DownloadImage;
import com.client.utils.Utils;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.Heightmap;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FireFly extends Function {
    public final IntegerSetting count = Integer().name("Количество").enName("Count").defaultValue(6).min(1).max(20).build();
    public final DoubleSetting speed = Double().name("Скорость").enName("Speed").defaultValue(1.0).min(0).max(5).c(1).build();
    public final IntegerSetting range = Integer().name("Дистанция").enName("Range").defaultValue(16).min(4).max(64).build();
    public final DoubleSetting particleScale = Double().name("Размер").enName("Scale").defaultValue(1.0).min(0).max(1).c(1).build();
    public final IntegerSetting lifeTime = Integer().name("Время жизни").enName("Life Time").defaultValue(3500).min(1500).max(5000).build();
    public final BooleanSetting ground = Boolean().name("Появляться на земле").enName("Ground Spawn").defaultValue(true).build();
    public final BooleanSetting physic = Boolean().name("Отталкиваться от земли").enName("Pushing From Ground").defaultValue(true).build();

    public final ListSetting colorMode = List().name("Режим цвета").enName("Color Mode").list(List.of("Клиентский", "Статичный")).defaultValue("Клиентский").build();
    public final ColorSetting colorSetting = Color().name("Цвет").enName("Color").defaultValue(Color.CYAN).visible(() -> colorMode.get().equals("Статичный")).build();

    public FireFly() {
        super("Fire Fly", Category.VISUAL);
    }

    private static final CopyOnWriteArrayList<ParticleBase> particles = new CopyOnWriteArrayList<>();

    @Override
    public void tick(TickEvent.Pre e) {
        float range = this.range.get();
        for (int i = 0; i < count.get(); i++) {
            Vec3d additional = mc.player.getPos().add(Utils.random(-range, range), 0, Utils.random(-range, range));
            BlockPos pos = mc.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(additional));
            particles.add(new ParticleBase(new Vec3d(pos.getX() + Utils.random(0f, 1f), ground.get() ? pos.getY() : mc.player.getY() + Utils.random(mc.player.getHeight(), range), pos.getZ() + Utils.random(0f, 1f)), new Vec3d(0, Utils.random(0.0, speed.get()) * (ground.get() ? 1 : -1), 0), (int) Utils.random(0, 100)));
        }

        particles.removeIf(particleBase -> System.currentTimeMillis() - particleBase.time > lifeTime.get());
    }

    @Override
    public void onRender3D(Render3DEvent e) {
        particles.forEach(particleBase -> particleBase.draw(e.getMatrices()));
    }

    public class ParticleBase {
        public long time;
        private Vec3d position;
        private Vec3d velocity;
        protected int age, maxAge;
        private float alpha, scale;
        private Color staticColor;

        public ParticleBase(Vec3d position, final Vec3d velocity, int startAge) {
            this.position = position;
            this.velocity = velocity.multiply(0.01f);
            this.time = System.currentTimeMillis();
            this.maxAge = this.age = (int) Utils.random(120, 200);
            this.age = this.maxAge - startAge;
            this.staticColor = Colors.getColor(Utils.random(0, 359));
            this.scale = particleScale.get().floatValue();
        }

        public void update() {
            alpha = AnimationUtils.fast(alpha, 1, 10);
            update(physic.get());
        }

        public void update(boolean physic) {
            if (physic) {
                if (isBlockSolid(this.position.x, this.position.y, this.position.z + this.velocity.z)) {
                    this.velocity = this.velocity.multiply(1, 1, -0.8);
                }
                if (isBlockSolid(this.position.x, this.position.y + this.velocity.y, this.position.z)) {
                    this.velocity = this.velocity.multiply(0.999, -0.6, 0.999);
                }
                if (isBlockSolid(this.position.x + this.velocity.x, this.position.y, this.position.z)) {
                    this.velocity = this.velocity.multiply(-0.8, 1, 1);
                }
                this.velocity = this.velocity.multiply(0.999999).subtract(new Vec3d(0, 0.00005, 0));
            }
            this.position = this.position.add(this.velocity);
        }

        public boolean isBlockSolid(double x, double y, double z) {
            return mc.world.getBlockState(new BlockPos(x, y, z)).getMaterial().isSolid();
        }

        public void draw(MatrixStack matrix) {
            float size = 1 - ((System.currentTimeMillis() - time) / 5000f);
            int texture = DownloadImage.getGlId(DownloadImage.CIRCLE);

            if (texture == -1) return;
            update();
            Color color = ColorUtils.injectAlpha(colorMode.get().equals("Клиентский") ? staticColor : colorSetting.get(), (int) ((255 * alpha) * size));

            Vec3d fix = Renderer3D.getRenderPosition(position);
            double x = fix.getX();
            double y = fix.getY();
            double z = fix.getZ();

            matrix.push();
            matrix.translate(x, y, z);
            matrix.scale(scale, scale, scale);
            matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-mc.gameRenderer.getCamera().getYaw()));
            matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(mc.gameRenderer.getCamera().getPitch()));

            TextureGL.create()
                    .bind(texture)
                    .draw(matrix, new TextureGL.TextureRegion(-0.9f * size, -0.9f * size), true, color);

            matrix.pop();
        }
    }
}
