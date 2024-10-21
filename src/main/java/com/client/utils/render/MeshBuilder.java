package com.client.utils.render;

import com.client.utils.color.ColorUtils;
import com.client.utils.render.texture.TextureRegion;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class MeshBuilder {
    private final BufferBuilder buffer;

    public double alpha = 1;

    public boolean depthTest = false;
    public boolean texture = false;
    public boolean isTextRender;
    public MatrixStack matrixStack;

    public MeshBuilder(int initialCapacity) {
        buffer = new BufferBuilder(initialCapacity);
        isTextRender = false;
    }

    public MeshBuilder(boolean isTexture) {
        buffer = new BufferBuilder(2097152);
        isTextRender = false;
    }

    public MeshBuilder() {
        buffer = new BufferBuilder(2097152);
        isTextRender = false;
    }

    public MeshBuilder(int initialCapacity, boolean isTextRender) {
        buffer = new BufferBuilder(initialCapacity);
        this.isTextRender = isTextRender;
        matrixStack = new MatrixStack();
    }

    public void begin(DrawMode drawMode, VertexFormat format) {
        buffer.begin(drawMode.toOpenGl(), format);
    }

    public void begin(int drawMode, VertexFormat format) {
        buffer.begin(drawMode, format);
    }

    public void end() {
        buffer.end();

        //if (count > 0) {
        glPushMatrix();
        //if (isTextRender) matrixStack.scale(1, 1, 0);
        RenderSystem.multMatrix(
                //isTextRender ? matrixStack.peek().getModel() :
                Matrices.getTop()
        );

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        if (depthTest) RenderSystem.enableDepthTest();
        else RenderSystem.disableDepthTest();
        RenderSystem.disableAlphaTest();
        if (texture) RenderSystem.enableTexture();
        else RenderSystem.disableTexture();
        RenderSystem.disableLighting();
        RenderSystem.disableCull();
        glEnable(GL_LINE_SMOOTH);
        RenderSystem.lineWidth(1);
        RenderSystem.color4f(1, 1, 1, 1);
        GlStateManager.shadeModel(GL_SMOOTH);

        BufferRenderer.draw(buffer);

        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        glDisable(GL_LINE_SMOOTH);

        glPopMatrix();
        //}
    }

    public void end(float lineWidth) {
        buffer.end();

        //if (count > 0) {
        glPushMatrix();
        //if (isTextRender) matrixStack.scale(1, 1, 0);
        RenderSystem.multMatrix(
                //isTextRender ? matrixStack.peek().getModel() :
                Matrices.getTop()
        );

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        if (depthTest) RenderSystem.enableDepthTest();
        else RenderSystem.disableDepthTest();
        RenderSystem.disableAlphaTest();
        if (texture) RenderSystem.enableTexture();
        else RenderSystem.disableTexture();
        RenderSystem.disableLighting();
        RenderSystem.disableCull();
        glEnable(GL_LINE_SMOOTH);
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.color4f(1, 1, 1, 1);
        GlStateManager.shadeModel(GL_SMOOTH);

        BufferRenderer.draw(buffer);

        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        glDisable(GL_LINE_SMOOTH);

        glPopMatrix();
        //}
    }

    public boolean isBuilding() {
        return buffer.isBuilding();
    }

    public MeshBuilder pos(double x, double y, double z) {
        buffer.vertex(x, y, z);
        return this;
    }

    public MeshBuilder texture(double x, double y) {
        buffer.texture((float) x, (float) y);
        return this;
    }

    public MeshBuilder color(Color color) {
        buffer.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f * (float) alpha);
        return this;
    }

    public MeshBuilder color(int color) {
        buffer.color(toRGBAR(color) / 255f, toRGBAG(color) / 255f, toRGBAB(color) / 255f, toRGBAA(color) / 255f * (float) alpha);
        return this;
    }

    public static int fromRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + (b) + (a << 24);
    }

    public static int toRGBAR(int color) {
        return (color >> 16) & 0x000000FF;
    }

    public static int toRGBAG(int color) {
        return (color >> 8) & 0x000000FF;
    }

    public static int toRGBAB(int color) {
        return (color) & 0x000000FF;
    }

    public static int toRGBAA(int color) {
        return (color >> 24) & 0x000000FF;
    }

    public void endVertex() {
        buffer.next();
    }

    // Quads, 2 dimensional, top left to bottom right

    public void closeButton(double fromX, double fromY, double toX, double toY) {
        pos(fromX, fromY, 0).color(Color.WHITE).endVertex();
        pos(toX, toY, 0).color(Color.WHITE).endVertex();
        pos(fromX, toY, 0).color(Color.WHITE).endVertex();
        pos(toX, fromY, 0).color(Color.WHITE).endVertex();
    }

    public void loginButton(double fromX, double fromY, double toX, double toY) {
        pos(fromX, fromY + (toY - fromY) / 2 + 2, 0).color(Color.WHITE).endVertex();
        pos(fromX + (toX - fromX) / 2, toY - (toX - fromY) / 4, 0).color(Color.WHITE).endVertex();
        pos(fromX + (toX - fromX) / 2, toY, 0).color(Color.WHITE).endVertex();
        pos(toX, fromY, 0).color(Color.WHITE).endVertex();
    }

    public void roundedQuad(double fromX, double fromY, double toX, double toY, double radC1, double samples, Color color, Color color1) {
        double[][] map = new double[][]{new double[]{toX - radC1, toY - radC1, radC1}, new double[]{toX - radC1, fromY + radC1, radC1}, new double[]{fromX + radC1, fromY + radC1, radC1}, new double[]{fromX + radC1, toY - radC1, radC1}};

        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            double rad = current[2];
            for (double r = i * 90; r < (90 + i * 90); r += (90 / samples)) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                switch (i) {
                    case 0, 1 -> pos((float) current[0] + sin, (float) current[1] + cos, 0.0F).color(color).endVertex();
                    default -> pos((float) current[0] + sin, (float) current[1] + cos, 0.0F).color(color1).endVertex();
                }
            }
        }
    }

    public void renderRoundedShadow(Color one, Color two, double fromX, double fromY, double toX, double toY, double rad, double wid) {
        double toX1 = toX - rad;
        double toY1 = toY - rad;
        double fromX1 = fromX + rad;
        double fromY1 = fromY + rad;
        double[][] map = new double[][] { new double[] { toX1, toY1 }, new double[] { toX1, fromY1 }, new double[] { fromX1, fromY1 },
                new double[] { fromX1, toY1 } };
        for (int i = 0; i < map.length; i++) {
            double[] current = map[i];
            for (double r = i * 90; r < (90 + i * 90); r += 10) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                switch (i) {
                    case 0, 3 -> pos((float) current[0] + sin, (float) current[1] + cos, 0.0F).color(one).endVertex();
                    default -> pos( (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(two).endVertex();
                }


                float sin1 = (float) (sin + Math.sin(rad1) * wid);
                float cos1 = (float) (cos + Math.cos(rad1) * wid);
                switch (i) {
                    case 0, 3 -> pos( (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(ColorUtils.injectAlpha(one, 0)).endVertex();
                    default -> pos( (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(ColorUtils.injectAlpha(two, 0)).endVertex();
                }
                //bufferBuilder.vertex(matrix, (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(cr, cg, cb, 0f).next();
            }
        }
        {
            double[] current = map[0];
            float rad1 = (float) Math.toRadians(0);
            float sin = (float) (Math.sin(rad1) * rad);
            float cos = (float) (Math.cos(rad1) * rad);
            pos( (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(one).endVertex();
            float sin1 = (float) (sin + Math.sin(rad1) * wid);
            float cos1 = (float) (cos + Math.cos(rad1) * wid);
            pos( (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(ColorUtils.injectAlpha(one, 0)).endVertex();
        }
    }

    public void renderRoundedOutline(Color one, Color two, double fromX, double fromY, double toX, double toY, double rad, double wid) {
        double toX1 = toX - rad;
        double toY1 = toY - rad;
        double fromX1 = fromX + rad;
        double fromY1 = fromY + rad;
        double[][] map = new double[][] { new double[] { toX1, toY1 }, new double[] { toX1, fromY1 }, new double[] { fromX1, fromY1 },
                new double[] { fromX1, toY1 } };
        for (int i = 0; i < map.length; i++) {
            double[] current = map[i];
            for (double r = i * 90; r < (90 + i * 90); r += 10) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                switch (i) {
                    case 0, 1 -> pos((float) current[0] + sin, (float) current[1] + cos, 0.0F).color(one).endVertex();
                    default -> pos( (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(two).endVertex();
                }


                float sin1 = (float) (sin + Math.sin(rad1) * wid);
                float cos1 = (float) (cos + Math.cos(rad1) * wid);
                switch (i) {
                    case 0, 1 -> pos( (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(one).endVertex();
                    default -> pos( (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(two).endVertex();
                }
                //bufferBuilder.vertex(matrix, (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(cr, cg, cb, 0f).next();
            }
        }
        {
            double[] current = map[0];
            float rad1 = (float) Math.toRadians(0);
            float sin = (float) (Math.sin(rad1) * rad);
            float cos = (float) (Math.cos(rad1) * rad);
            pos( (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(one).endVertex();
            float sin1 = (float) (sin + Math.sin(rad1) * wid);
            float cos1 = (float) (cos + Math.cos(rad1) * wid);
            pos( (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(one).endVertex();
        }
    }

    public void quad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color topLeft, Color topRight, Color bottomRight, Color bottomLeft) {
        pos(x1, y1, z1).color(topLeft).endVertex();
        pos(x2, y2, z2).color(topRight).endVertex();
        pos(x3, y3, z3).color(bottomRight).endVertex();
        pos(x1, y1, z1).color(topLeft).endVertex();
        pos(x3, y3, z3).color(bottomRight).endVertex();
        pos(x4, y4, z4).color(bottomLeft).endVertex();
    }

    public void quadCoords(double x, double y, double x1, double y1, Color topLeft, Color topRight, Color bottomRight, Color bottomLeft) {
        pos(x, y, 0).color(topLeft).endVertex();
        pos(x, y1, 0).color(bottomLeft).endVertex();
        pos(x1, y1, 0).color(bottomRight).endVertex();
        pos(x1, y, 0).color(topRight).endVertex();
    }

    public void quad(double x, double y, double width, double height, Color topLeft, Color topRight, Color bottomRight, Color bottomLeft) {
        quad(x, y, 0, x + width, y, 0, x + width, y + height, 0, x, y + height, 0, topLeft, topRight, bottomRight, bottomLeft);
    }

    public void verticalGradientQuad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color top, Color bottom) {
        quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, top, top, bottom, bottom);
    }

    public void verticalGradientQuad(double x, double y, double width, double height, Color top, Color bottom) {
        verticalGradientQuad(x, y, 0, x + width, y, 0, x + width, y + height, 0, x, y + height, 0, top, bottom);
    }

    public void horizontalGradientQuad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color left, Color right) {
        quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, left, right, right, left);
    }

    public void horizontalGradientQuad(double x, double y, double width, double height, Color left, Color right) {
        horizontalGradientQuad(x, y, 0, x + width, y, 0, x + width, y + height, 0, x, y + height, 0, left, right);
    }

    public void quad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color color) {
        quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, color, color, color, color);
    }

    public void quad(double x, double y, double width, double height, Color color) {
        quad(x, y, 0, x + width, y, 0, x + width, y + height, 0, x, y + height, 0, color);
    }

    public void horizontalQuad(double x1, double z1, double x2, double z2, double y, Color color) {
        quad(x1, y, z1, x1, y, z2, x2, y, z2, x2, y, z1, color);
    }

    public void verticalQuad(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
        quad(x1, y1, z1, x1, y2, z1, x2, y2, z2, x2, y1, z2, color);
    }

    public void texQuad(double x, double y, double width, double height, TextureRegion tex, Color color) {
        pos(x, y, 0).color(color).texture(tex.x1, tex.y1).endVertex();
        pos(x + width, y, 0).color(color).texture(tex.x2, tex.y1).endVertex();
        pos(x + width, y + height, 0).color(color).texture(tex.x2, tex.y2).endVertex();

        pos(x, y, 0).color(color).texture(tex.x1, tex.y1).endVertex();
        pos(x + width, y + height, 0).color(color).texture(tex.x2, tex.y2).endVertex();
        pos(x, y + height, 0).color(color).texture(tex.x1, tex.y2).endVertex();
    }

    // TRIANGLES

    public void triangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, Color first, Color second, Color third) {
        pos(x1, y1, z1).color(first).endVertex();
        pos(x2, y2, z2).color(second).endVertex();
        pos(x3, y3, z3).color(third).endVertex();
    }

    public void triangle(double x1, double y1, double x2, double y2, double x3, double y3, Color first, Color second, Color third) {
        triangle(x1, y1, 0, x2, y2, 0, x3, y3, 0, first, second, third);
    }

    public void triangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, Color color) {
        triangle(x1, y1, z1, x2, y2, z2, x3, y3, z3, color, color, color);
    }

    public void triangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
        triangle(x1, y1, 0, x2, y2, 0, x3, y3, 0, color);
    }

    // LINES

    public void line(double x1, double y1, double z1, double x2, double y2, double z2, Color startColor, Color endColor) {
        pos(x1, y1, z1).color(startColor).endVertex();
        pos(x2, y2, z2).color(endColor).endVertex();
    }

    public void line(double x1, double y1, double x2, double y2, Color startColor, Color endColor) {
        line(x1, y1, 0, x2, y2, 0, startColor, endColor);
    }

    public void line(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
        line(x1, y1, z1, x2, y2, z2, color, color);
    }

    public void line(double x1, double y1, double x2, double y2, Color color) {
        line(x1, y1, 0, x2, y2, 0, color);
    }
}