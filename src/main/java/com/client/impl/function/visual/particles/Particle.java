package com.client.impl.function.visual.particles;

import com.client.interfaces.IBox;
import com.client.system.function.FunctionManager;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.vector.doubles.V3D;
import com.client.utils.misc.FunctionUtils;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.client.BloodyClient.mc;
import static com.client.utils.math.MathUtils.offset;

public class Particle {
    private final float size = 0.2f;
    private float angel;
    private final Identifier texture;
    private V3D position;
    private long initTime;

    private final V3D velocity;
    private final boolean gravity;

    public double def, factor;

    private Box box;
    private final int color;

    private final boolean animType;
    private float scale = 1f;
    public final float m;

    private boolean revers = true, revY;
    private int alpha = 0;

    public Particle(double x, double y, double z) {
        this(x, y, z, 10F);
    }

    public Particle(double x, double y, double z, boolean gravity) {
        this(x, y, z, 10F, gravity);
    }

    public Particle(double x, double y, double z, double f) {
        this(x, y, z, f, true);
    }

    public Particle(double x, double y, double z, double f, boolean gravity) {
        this.def = f;
        this.factor = f;
        this.position = new V3D(x, y, z).add(offset(0.25), offset(0.1), offset(0.25));
        V3D vector = new V3D(offset(1f / f), 0, offset(1f / f));
        this.velocity = new V3D(vector.getX(), vector.getY(), vector.getZ());
        this.m = (float) f;

        this.angel = new Random().nextFloat() * 360f;
        this.box = new Box(position.x - size, position.y - size, position.z - size, position.x + size, position.y + size, position.z + size);
        this.color = new Random().nextInt(360);
        this.animType = false /*new Random().nextBoolean()*/;
        this.gravity = gravity;

        List<String> id = new ArrayList<>();

        if (FunctionManager.get(Particles.class).type.get(0)) id.add("star.png");
        if (FunctionManager.get(Particles.class).type.get(2)) id.add("snow.png");
        if (FunctionManager.get(Particles.class).type.get(1)) id.add("heart.png");

        if (id.isEmpty()) {
            texture = null;
            return;
        }

        this.texture = new Identifier("bloody-client", "/client/" + id.get(new Random().nextInt(id.size())));
        this.initTime = System.currentTimeMillis();
    }

    public void draw(MatrixStack matrix) {
        if (texture == null) return;
        tick();

        Vec3d fix = Renderer3D.getRenderPosition(new Vec3d(position.x, position.y, position.z));
        double x = fix.getX();
        double y = fix.getY();
        double z = fix.getZ();

        matrix.push();
        matrix.translate(x, y, z);
        float f = 0.07f * scale;
        matrix.scale(-f, -f, -f);

        matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-mc.gameRenderer.getCamera().getYaw()));
        matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(mc.gameRenderer.getCamera().getPitch()));
        matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(angel));

        TextureGL.create()
                .bind(texture)
                .draw(matrix, new TextureGL.TextureRegion(3F), true, ColorUtils.injectAlpha(Colors.getColor(color), alpha));

        matrix.pop();
    }

    private void tick() {
        //if (def < factor) def = AnimationUtils.fast(def, factor);

        if (animType && alpha >= 255 && System.currentTimeMillis() - initTime > 100L) {
            scale = AnimationUtils.fast(scale, 0f);
        }

        angel += 0.15F;

        box = new Box(
                position.x - (size + size / 2), position.y - size, position.z - (size + size / 2),
                position.x + (size + size / 2), position.y + size, position.z + (size + size / 2)
        );

        if (velocity.y < 0.01f && !revY) {
            velocity.y += gravity ? 0.00124f : 0.00124f / m;
            if (velocity.y >= 0.01f) revY = true;
        }

        updateCollisions(position.x, position.z);

        if (revY && gravity) velocity.y -= 0.00124f;
        position = position.add(velocity.x, velocity.y, velocity.z);

        ((IBox) box).addY(-size / 2);
        if (hasCollisions(box) && gravity) velocity.y = 0.03f;
        ((IBox) box).addY(size / 2);
        ((IBox) box).addY(size / 2);
        if (hasCollisions(box) && gravity) velocity.y = -0.03f;
        ((IBox) box).addY(-size / 2);
    }

    private boolean hasCollisions(Box box) {
        return mc.world.getBlockCollisions(null, box.expand(-size / 4, 0, -size / 4)).findAny().isPresent();
    }

    private void updateCollisions(double x, double z) {
        BlockPos blockPos = new BlockPos(x, position.y - size, z);
        if (wouldCollideAt(blockPos)) {
            double e = x - (double)blockPos.getX();
            double f = z - (double)blockPos.getZ();
            double g = Double.MAX_VALUE;
            Direction direction = null;
            Direction[] directions = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

            for (Direction direction2 : directions) {
                double h = direction2.getAxis().choose(e, 0.0, f);
                double i = direction2.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - h : h;
                if (i < g && !(wouldCollideAt(blockPos.offset(direction2)))) {
                    g = i;
                    direction = direction2;
                }
            }

            if (direction != null) {
                if (direction.getAxis() == Direction.Axis.X) {
                    velocity.x = - velocity.x;
                } else {
                    velocity.z = - velocity.z;
                }
            }
        }
    }

    private boolean wouldCollideAt(BlockPos blockPos) {
        Box box2 = (new Box(blockPos.getX(), box.minY + size, blockPos.getZ(), (double)blockPos.getX() + 1.0, box.maxY + size, (double)blockPos.getZ() + 1.0)).contract(1.0E-7);
        return !mc.world.isBlockSpaceEmpty(null, box2, (blockState, blockPosx) -> blockState != null);
    }

    public boolean update() {
        if (revers) {
            alpha += 17;
            if (alpha >= 255 && (!animType || scale <= 0.7F)) {
                revers = false;
            }
        } else {
            alpha -= 12;
        }
        alpha = MathHelper.clamp(alpha, 0, 255);
        return (animType ? scale < 0.1F && alpha <= 0 : !revers && alpha <= 0) || texture == null;
    }
}