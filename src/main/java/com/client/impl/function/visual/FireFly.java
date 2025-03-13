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

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FireFly extends Function {
    public final ListSetting type = List().name("Партиклы").enName("Particles").list(List.of(
            "Звездочки",
            "Сердечки",
            "Снежинки",
            "Треугольники",
            "Кружки"
    )).defaultValue("Звездочки").build();

    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Кометы", "Дождь")).defaultValue("Кометы").build();
    public final IntegerSetting count = Integer().name("Количество").enName("Count").defaultValue(6).min(1).max(20).build();
    public final IntegerSetting speed = Integer().name("Скорость").enName("Speed").defaultValue(4).min(1).max(20).build();
    public final BooleanSetting workInMenu = Boolean().name("Работать в меню").enName("Work in menu").defaultValue(true).build();
    public final ListSetting colorMode = List().name("Режим цвета").enName("Color Mode").list(List.of("Клиентский", "Статичный")).defaultValue("Клиентский").build();
    public final ColorSetting colorSetting = Color().name("Цвет").enName("Color").defaultValue(Color.CYAN).visible(() -> colorMode.get().equals("Статичный")).build();

    public FireFly() {
        super("Fire Fly", Category.VISUAL);
    }

    private static final CopyOnWriteArrayList<ParticleBase> particles = new CopyOnWriteArrayList<>();
    private float dynamicSpeed = 0.1f;

    @Override
    public void tick(TickEvent.Pre e) {
        dynamicSpeed = mode.get().equals("Дождь") ? 0.1f : speed.get() / 10f;

        particles.removeIf(ParticleBase::tick);

        int particlesToCreate = (int) ((count.get() * 50) - particles.size());
        if (particlesToCreate > 0) {
            for (int n = 0; n < particlesToCreate; ++n) {
                if (!workInMenu.get() && mc.currentScreen instanceof GameMenuScreen) return;

                float spawnX = (float) (mc.cameraEntity.getX() + Utils.random(-48.0F, 48.0F));
                float spawnY = (float) (mc.cameraEntity.getY() + Utils.random(-20.0F, 48.0F));
                float spawnZ = (float) (mc.cameraEntity.getZ() + Utils.random(-48.0F, 48.0F));

                float motionX = Utils.random(-dynamicSpeed, dynamicSpeed);
                float motionY = Utils.random(-0.1F, 0.1F);
                float motionZ = Utils.random(-dynamicSpeed, dynamicSpeed);

                int startAge = (int) Utils.random(0, 100);

                particles.add(new ParticleBase(spawnX, spawnY, spawnZ, motionX, motionY, motionZ, startAge));
            }
        }


        particles.removeIf(particleBase -> System.currentTimeMillis() - particleBase.time > 5000);
    }

    @Override
    public void onRender3D(Render3DEvent e) {
        particles.forEach(particleBase -> particleBase.draw(e.getMatrices()));
    }

    public class ParticleBase {
        public long time;
        protected float prevposX, prevposY, prevposZ;
        protected float posX, posY, posZ;
        protected float motionX, motionY, motionZ;
        protected int age, maxAge;
        private float alpha;
        private long collisionTime = -1L;
        private Color staticColor;

        public ParticleBase(float x, float y, float z, float motionX, float motionY, float motionZ, int startAge) {
            this.posX = x;
            this.posY = y;
            this.posZ = z;
            this.prevposX = x;
            this.prevposY = y;
            this.prevposZ = z;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.time = System.currentTimeMillis();
            this.maxAge = this.age = (int) Utils.random(120, 200);
            this.age = this.maxAge - startAge;
            this.staticColor = Colors.getColor(Utils.random(0, 359));
        }

        public void update() {
            alpha = AnimationUtils.fast(alpha, 1, 10);
            if (mode.get().equals("Дождь")) updateWithBounce();
        }

        public boolean tick() {
            this.age = mc.player.squaredDistanceTo(this.posX, (double) this.posY, (double) this.posZ) > 4096.0 ? (this.age -= 8) : --this.age;
            if (this.age < 0) {
                return true;
            } else {
                this.prevposX = this.posX;
                this.prevposY = this.posY;
                this.prevposZ = this.posZ;
                this.posX += this.motionX;
                this.posY += this.motionY;
                this.posZ += this.motionZ;

                if (mode.get().equals("Кометы")) {
                    this.motionX *= 0.9F;
                    this.motionY *= 0.9F;
                    this.motionZ *= 0.9F;
                    this.motionY -= 0.001F * (speed.get() / 10f);
                } else {
                    this.motionX = 0;
                    this.motionZ = 0;
                }
                return false;
            }
        }

        private void updateWithBounce() {
            if (this.collisionTime != -1L) {
                long timeSinceCollision = System.currentTimeMillis() - this.collisionTime;
                this.alpha = Math.max(0.0f, 1.0f - (float) timeSinceCollision / 3000.0f);
            }
            this.motionY -= 8.0E-4 * (speed.get() / 10f);
            float newPosX = this.posX + this.motionX;
            float newPosY = this.posY + this.motionY;
            float newPosZ = this.posZ + this.motionZ;

            BlockPos particlePos = new BlockPos(newPosX, newPosY, newPosZ);
            BlockState blockState = mc.world.getBlockState(particlePos);

            if (!blockState.isAir()) {
                if (this.collisionTime == -1L) {
                    this.collisionTime = System.currentTimeMillis();
                }

                if (!mc.world.getBlockState(new BlockPos(this.posX + this.motionX, this.posY, this.posZ)).isAir()) {
                    this.motionX = 0.0f;
                }
                if (!mc.world.getBlockState(new BlockPos(this.posX, this.posY + this.motionY, this.posZ)).isAir()) {
                    this.motionY = -this.motionY * 0.8f;
                }
                if (!mc.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ + this.motionZ)).isAir()) {
                    this.motionZ = 0.0f;
                }

                this.posX += this.motionX;
                this.posY += this.motionY;
                this.posZ += this.motionZ;
            } else {
                this.posX = newPosX;
                this.posY = newPosY;
                this.posZ = newPosZ;
            }
        }

        public void draw(MatrixStack matrix) {
            float size = 1 - ((System.currentTimeMillis() - time) / 5000f);
            int texture = -1;

            texture = switch (type.get()) {
                case "Звездочки" -> DownloadImage.getGlId(DownloadImage.STAR);
                case "Сердечки" -> DownloadImage.getGlId(DownloadImage.HEART);
                case "Снежинки" -> DownloadImage.getGlId(DownloadImage.SNOW);
                case "Треугольники" -> DownloadImage.getGlId(DownloadImage.TRIANGLE_GLOW);
                default -> DownloadImage.getGlId(DownloadImage.CIRCLE);
            };

            if (texture == -1) return;
            update();
            Color color = ColorUtils.injectAlpha(colorMode.get().equals("Клиентский") ? staticColor : colorSetting.get(), (int) ((255 * alpha) * size));

            Vec3d fix = Renderer3D.getRenderPosition(new Vec3d(posX, posY, posZ));
            double x = fix.getX();
            double y = fix.getY();
            double z = fix.getZ();

            matrix.push();
            matrix.translate(x, y, z);
            matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-mc.gameRenderer.getCamera().getYaw()));
            matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(mc.gameRenderer.getCamera().getPitch()));

            TextureGL.create()
                    .bind(texture)
                    .draw(matrix, new TextureGL.TextureRegion(-0.9f * size, -0.9f * size), true, color);

            matrix.pop();
        }
    }
}
