package com.client.utils.render;

import com.client.utils.color.ColorUtils;
import com.client.utils.render.texture.TextureRegion;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
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

    public void texQuad(int textureId, TextureGL.TextureRegion region, Color color) {
        GlStateManager.bindTexture(textureId);

        float x = region.x();
        float x1 = region.x1();
        float y = region.y();
        float y1 = region.y1();

        float textureWidth = region.textureWidth();
        float textureHeight = region.textureHeight();

        float u = region.u();
        float v = region.v();

        float regionWidth = region.regionWidth();
        float regionHeight = region.regionHeight();

        pos(x, y, 0).color(color).texture((u + 0.0F) / textureWidth, (v + 0.0F) / textureHeight).endVertex();
        pos(x1, y, 0).color(color).texture((u + regionWidth) / textureWidth, (v + 0.0F) / textureHeight).endVertex();
        pos(x1, y1, 0).color(color).texture((u + regionWidth) / textureWidth, (v + regionHeight) / textureHeight).endVertex();

        pos(x, y, 0).color(color).texture((u + 0.0F) / textureWidth, (v + 0.0F) / textureHeight).endVertex();
        pos(x1, y1, 0).color(color).texture((u + regionWidth) / textureWidth, (v + regionHeight) / textureHeight).endVertex();
        pos(x, y1, 0).color(color).texture((u + 0.0F) / textureWidth, (v + regionHeight) / textureHeight).endVertex();
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