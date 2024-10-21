package com.client.utils.render.wisetree.render.render3d;

import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.AnimationUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class CircleRenderer {
    public Vec3d center;
    public float height, width, maxHeight;

    private double animProgress;
    private double value;
    private boolean down;

    public void setCenter(Vec3d center) {
        this.center = center;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void tick() {
        float animSpeed = 0.03f;
        value += animSpeed;
        float targetHeight = (float) (0.5 * (1.0 + Math.sin(Math.PI * 2 * (value * (double) 0.3f))));
        float h = height;
        if (targetHeight > 0.99) {
            down = false;
        } else if ((targetHeight < 0.01)) {
            down = true;
        }
        if (down) {
            animProgress = AnimationUtils.fast(animProgress, -h);
        } else {
            animProgress = AnimationUtils.fast(animProgress, h);
        }
    }

    public void draw(Entity entity, float alpha) {
        setCenter(Renderer3D.getRenderPosition(Renderer3D.getSmoothPos(entity)));
        setWidth(entity.getWidth());
        setMaxHeight(entity.getHeight());
        setHeight(0.4F);

        tick();

        double x = center.getX();
        double y = center.getY();
        double z = center.getZ();
        float target = (float) (0.5 * (1.0 + Math.sin(Math.PI * 2 * (value * (double) 0.3f))));
        float end = (float) ((float) ((double) maxHeight + 0.2) * (double) target);

        Renderer3D.prepare3d(false);
        RenderSystem.blendFunc(770, 1);

        Renderer3D.begin(GL11.GL_TRIANGLE_STRIP);
        for (int i = 0; i <= 360; ++i) {
            double sin = Math.sin(Math.toRadians(i)) * width;
            double cos = Math.cos(Math.toRadians(i)) * width;

            Renderer3D.color(ColorUtils.injectAlpha(Colors.getColor(i * 2), (int) (alpha * 155F)));
            GL11.glVertex3d(x + cos, y + (double) end, z - sin);

            Renderer3D.color(ColorUtils.injectAlpha(Colors.getColor(i * 2), 0));
            GL11.glVertex3d(x + cos, y + (double) end + animProgress, z - sin);
        }
        Renderer3D.end();

        Renderer3D.enableSmoothLine(2.5F);
        GL11.glDepthMask(true);
        Renderer3D.begin(GL11.GL_LINE_LOOP);
        for (int i = 0; i <= 360; ++i) {
            double sin = Math.sin(Math.toRadians(i)) * width;
            double cos = Math.cos(Math.toRadians(i)) * width;

            Renderer3D.color(ColorUtils.injectAlpha(Colors.getColor(i * 2), (int) (alpha * 155F)));
            GL11.glVertex3d(x + cos, y + (double) end, z - sin);
        }
        Renderer3D.end();

        Renderer3D.disableSmoothLine();
        Renderer3D.end3d(false);
    }
}
