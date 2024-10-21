package com.client.utils.render.wisetree.render.render3d;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.client.BloodyClient.mc;

public class Renderer3D {
    public static void prepare3d() {
        prepare3d(true);
    }

    public static void prepare3d(boolean depthTest) {
        prepare3d(depthTest, true);
    }

    public static void prepare3d(boolean depthTest, boolean matrix) {
        GL11.glPushMatrix();

        RenderSystem.enableBlend();

        if (depthTest) {
            RenderSystem.enableDepthTest();
        } else {
            RenderSystem.disableDepthTest();
        }

        RenderSystem.blendFunc(770, 771);
        RenderSystem.disableTexture();
        RenderSystem.disableCull();
        RenderSystem.defaultAlphaFunc();
        GL11.glShadeModel(GL11.GL_SMOOTH);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        if (matrix) {
            GL11.glRotated(MathHelper.wrapDegrees(mc.gameRenderer.getCamera().getPitch()), 1, 0, 0);
            GL11.glRotated(MathHelper.wrapDegrees(mc.gameRenderer.getCamera().getYaw() + 180.0), 0, 1, 0);
        }
    }

    public static void end3d() {
        end3d(true);
    }

    public static void end3d(boolean depthTest) {
        color(1f, 1f, 1f, 1f);

        GL11.glLineWidth(1f);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        RenderSystem.defaultAlphaFunc();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
        RenderSystem.enableTexture();

        if (depthTest) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }

        RenderSystem.disableBlend();

        GL11.glPopMatrix();
    }

    public static void enableSmoothLine(float width) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(width);
    }

    public static void disableSmoothLine() {
        GL11.glLineWidth(1f);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void begin(int mode) {
        GL11.glBegin(mode);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void color(int color) {
        color(new Color(color));
    }

    public static void color(Color color) {
        if (color == null) return;
        color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static void color(int r, int g, int b) {
        color(r, g, b, 255);
    }

    public static void color(int r, int g, int b, int a) {
        color(r / 255F, g / 255F, b / 255F, a / 255F);
    }

    public static void color(double r, double g, double b) {
        color(r, g, b, 1d);
    }

    public static void color(double r, double g, double b, double a) {
        color((float) r, (float) g, (float) b, (float) a);
    }

    public static void color(float r, float g, float b) {
        color(r, g, b, 1f);
    }

    public static void color(float r, float g, float b, float a) {
        GL11.glColor4f(MathHelper.clamp(r, 0, 1), MathHelper.clamp(g, 0, 1), MathHelper.clamp(b, 0, 1), MathHelper.clamp(a, 0, 1));
    }

    public static void reset() {
        color(1f, 1f, 1f, 1f);
    }

    public static void drawLine(Vec3d start, Vec3d end, Color color) {
        drawLine(start, end, color, 1f);
    }

    public static void drawLine(Vec3d start, Vec3d end, Color color, float lineW) {
        GL11.glLineWidth(lineW);
        begin(GL11.GL_LINES);
        color(color);
        GL11.glVertex3d(start.x, start.y, start.z);
        GL11.glVertex3d(end.x, end.y, end.z);
        end();
        GL11.glLineWidth(1);
    }

    public static void drawOutline(BlockPos pos, Color color) {
        drawOutline(pos, color, 1.4f);
    }

    public static void drawOutline(BlockPos pos, Color color, float lineW) {
        drawOutline(new Box(pos), color, lineW);
    }

    public static void drawOutline(Box box, Color color) {
        drawOutline(box, color, 1.4f);
    }

    public static void drawOutline(Box box, Color color, float lineW) {
        VoxelShape shape = VoxelShapes.cuboid(box);
        GL11.glLineWidth(lineW);
        Renderer3D.begin(GL11.GL_LINES);
        Renderer3D.color(color);
        shape.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
            GL11.glVertex3f((float) ((float) x1 - getCamera().getPos().x), (float) ((float) y1 - getCamera().getPos().y), (float) ((float) z1 - getCamera().getPos().z));
            GL11.glVertex3f((float) ((float) x2 - getCamera().getPos().x), (float) ((float) y2 - getCamera().getPos().y), (float) ((float) z2 - getCamera().getPos().z));
        });
        Renderer3D.end();
        GL11.glLineWidth(1f);
    }

    public static void drawFilled(BlockPos pos, Color color) {
        drawFilled(new Box(pos), color);
    }

    public static void drawFilled(Box box, Color color) {
        float minX = (float) ((float) box.minX - getCamera().getPos().x);
        float minY = (float) ((float) box.minY - getCamera().getPos().y);
        float minZ = (float) ((float) box.minZ - getCamera().getPos().z);
        float maxX = (float) ((float) box.maxX - getCamera().getPos().x);
        float maxY = (float) ((float) box.maxY - getCamera().getPos().y);
        float maxZ = (float) ((float) box.maxZ - getCamera().getPos().z);

        begin(GL11.GL_QUADS);
        color(color);

        GL11.glVertex3f(minX, minY, minZ);
        GL11.glVertex3f(maxX, minY, minZ);
        GL11.glVertex3f(maxX, minY, maxZ);
        GL11.glVertex3f(minX, minY, maxZ);

        GL11.glVertex3f(minX, maxY, minZ);
        GL11.glVertex3f(minX, maxY, maxZ);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(maxX, maxY, minZ);

        GL11.glVertex3f(minX, minY, minZ);
        GL11.glVertex3f(minX, maxY, minZ);
        GL11.glVertex3f(maxX, maxY, minZ);
        GL11.glVertex3f(maxX, minY, minZ);

        GL11.glVertex3f(maxX, minY, minZ);
        GL11.glVertex3f(maxX, maxY, minZ);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(maxX, minY, maxZ);

        GL11.glVertex3f(minX, minY, maxZ);
        GL11.glVertex3f(maxX, minY, maxZ);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(minX, maxY, maxZ);

        GL11.glVertex3f(minX, minY, minZ);
        GL11.glVertex3f(minX, minY, maxZ);
        GL11.glVertex3f(minX, maxY, maxZ);
        GL11.glVertex3f(minX, maxY, minZ);

        end();
    }

    public static Vec3d getSmoothPos(Entity entity) {
        double ix = entity.prevX + (entity.getX() - entity.prevX) * mc.getTickDelta();
        double iy = entity.prevY + (entity.getY() - entity.prevY) * mc.getTickDelta();
        double iz = entity.prevZ + (entity.getZ() - entity.prevZ) * mc.getTickDelta();
        return new Vec3d(ix, iy, iz);
    }

    public static Vec3d getRenderPosition(Entity entity) {
        return getRenderPosition(entity.getX(), entity.getY(), entity.getZ());
    }

    public static Vec3d getRenderPosition(Box box) {
        return getRenderPosition(box.getCenter());
    }

    public static Vec3d getRenderPosition(Vec3d vec3d) {
        return getRenderPosition(vec3d.x, vec3d.y, vec3d.z);
    }

    public static Vec3d getRenderPosition(BlockPos blockPos) {
        return getRenderPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static Vec3d getRenderPosition(double x, double y, double z) {
        double minX = x - getCamera().getPos().x;
        double minY = y - getCamera().getPos().y;
        double minZ = z - getCamera().getPos().z;
        return new Vec3d(minX, minY, minZ);
    }

    public static Camera getCamera() {
        return mc.getEntityRenderDispatcher().camera;
    }
}