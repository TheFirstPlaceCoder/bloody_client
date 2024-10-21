package com.client.impl.function.visual.trajectories;

import com.client.system.hud.HudFunction;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.math.vector.Vec3;
import com.client.utils.render.TagUtils;
import com.client.utils.render.text.TextRenderer;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.client.BloodyClient.mc;

public class Path {
    public final List<Vec3d> points = new ArrayList<>();;
    public final Vec3 tag = new Vec3(0, 0, 0);

    public final ItemStack stack;

    public long lifetime;
    public int id;

    public Path(Entity entity) {
        stack = entity instanceof TridentEntity ? Items.TRIDENT.getDefaultStack() : entity instanceof ArrowEntity ? Items.ARROW.getDefaultStack() : Items.ENDER_PEARL.getDefaultStack();

        build(entity);

        id = entity.getEntityId();
    }

    public void build(Entity entity) {
        points.clear();
        Vec3d pos = entity.getPos();
        Vec3d vel = entity.getVelocity();

        double posX = pos.x;
        double posY = pos.y;
        double posZ = pos.z;
        double velX = vel.x;
        double velY = vel.y;
        double velZ = vel.z;

        lifetime = 0;

        while (true) {
            pos = new Vec3d(posX += velX, posY += velY, posZ += velZ);

            double drag = isTouchingWater(posX, posY, posZ) ? 0.8 : 0.99;

            velY *= drag;
            vel = new Vec3d(velX *= drag, velY -= 0.03, velZ *= drag);

            HitResult hitResult = getCollision(entity, pos, vel);

            if (hitResult.getType() != HitResult.Type.MISS || pos.y <= 0) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    points.add(hitResult.getPos());
                }

                break;
            }

            points.add(new Vec3d(pos.x, pos.y, pos.z));

            lifetime += 50L;
        }

        if (!points.isEmpty()) {
            tag.set(points.get(points.size() - 1));
            tag.add(0, -0.2f, 0);
        }
    }

    private boolean isTouchingWater(double x, double y, double z) {
        FluidState fluidState = mc.world.getFluidState(new BlockPos(x, y, z));
        if (fluidState.getFluid() != Fluids.WATER && fluidState.getFluid() != Fluids.FLOWING_WATER) {
            return false;
        }
        return y - (double)((int)y) <= (double)fluidState.getHeight();
    }

    private HitResult getCollision(Entity pearl, Vec3d pos, Vec3d vel) {
        EntityHitResult hitResult2;
        Vec3d vec3d3 = pos;
        Vec3d vec3d4 = vec3d3.add(vel);

        HitResult hitResult = mc.world.raycast(new RaycastContext(vec3d3, vec3d4, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, pearl));

        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d3 = hitResult.getPos();
        }

        if ((hitResult2 = ProjectileUtil.getEntityCollision(mc.world, pearl, vec3d3, vec3d4, pearl.getBoundingBox().stretch(pearl.getVelocity()).expand(1.0), entity -> !entity.isSpectator() && entity.isAlive())) != null) {
            hitResult = hitResult2;
        }

        return hitResult;
    }

    public void draw3d() {
        Renderer3D.prepare3d(false);
        Renderer3D.enableSmoothLine(2F);
        Renderer3D.begin(GL11.GL_LINE_STRIP);

        for (int i = 0; i < points.size(); i++) {
            Vec3d position = Renderer3D.getRenderPosition(points.get(i).x, points.get(i).y, points.get(i).z);
            Renderer3D.color(ColorUtils.injectAlpha(Colors.getColor(i * 16), i <= Math.min(points.size(), 7) ? i == 0 ? 0 : 36 * i : 255));
            GL11.glVertex3d(position.x, position.y, position.z);
        }

        Renderer3D.end();
        Renderer3D.disableSmoothLine();
        Renderer3D.end3d(false);
    }

    public void draw2d() {
        if (tag == null) return;

        if (TagUtils.to2D(tag, 1.4F)) {
            TextRenderer text = TextRenderer.get();
            TagUtils.begin(tag);
            text.begin(1.2F, false, true);
            String time = round(lifetime);

            double w = text.getWidth(time) + 20;
            double h = text.getHeight();

            HudFunction.drawRect(new FloatRect(
                    -(w / 2 + 4), -1, w + 8, h + 2
            ), 1F);


            mc.getItemRenderer().renderInGui(stack, (int) -(w / 2 + 2), (int) (h / 2 - 8));

            text.render(time, 20 + (-w / 2), 0, Color.WHITE);
            text.end();
            TagUtils.end();
        }
    }

    private String round(long time) {
        int sec = (int) (time / 1000L);
        int ms = (int) ((int) (time - ((time / 1000L) * 1000L)) / 100L);

        return sec + "." + ms;
    }
}