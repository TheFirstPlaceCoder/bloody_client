package com.client.utils.render.wisetree.render.render2d.main;

import com.client.utils.render.wisetree.render.render2d.utils.AntiAliasing;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.client.system.function.Function.mc;
import static org.lwjgl.opengl.GL11C.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL30.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL30.GL_SAMPLE_ALPHA_TO_COVERAGE;

public class TextureGL {
    private static final MatrixStack EMPTY = new MatrixStack();

    public static TextureGL create() {
        return new TextureGL();
    }

    private Identifier identifier;
    private int id = -1;
    private boolean defaultFunc;

    public TextureGL defaultFunc() {
        defaultFunc = true;
        return this;
    }

    public TextureGL bind(Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public TextureGL bind(int id) {
        this.id = id;
        return this;
    }

    public static void begin(boolean depthTest) {
        RenderSystem.enableBlend();

        if (depthTest) {
            RenderSystem.enableDepthTest();
        } else {
            RenderSystem.disableDepthTest();
        }

        RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.blendFunc(770, 1);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glEnable(GL_MULTISAMPLE);
        GL11.glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE);
        AntiAliasing.enable(true, true, true);
    }

    public static void end(boolean depthTest) {
        AntiAliasing.disable(true, true, true);
        GL11.glDisable(GL_MULTISAMPLE);
        GL11.glDisable(GL_SAMPLE_ALPHA_TO_COVERAGE);
        GL11.glDisable(GL_LINE_SMOOTH);
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
        if (depthTest) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    public TextureGL draw(TextureRegion region) {
        return draw(region, true);
    }

    public TextureGL draw(TextureRegion region, boolean depthTest, Color... colors) {
        return draw(EMPTY, region, depthTest, colors);
    }

    public TextureGL draw(MatrixStack stack, TextureRegion region, boolean depthTest, Color... colors) {
        begin(depthTest);

        if (defaultFunc) {
            RenderSystem.defaultBlendFunc();
        }

        if (identifier != null) {
            mc.getTextureManager().bindTexture(identifier);
        } else if (id != -1) {
            RenderSystem.bindTexture(id);
        }

        Color color = Color.WHITE;
        int index = 0;

        try {
            color = colors[index];
        } catch (Exception ignore) {
        }

        Matrix4f matrix = stack.peek().getModel();

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE);

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

        bufferBuilder.vertex(matrix, x, y, 0)
                .color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F)
                .texture((u + 0.0F) / textureWidth, (v + 0.0F) / textureHeight)
                .next();

        index++;
        try {
            color = colors[index];
        } catch (Exception ignore) {
        }

        bufferBuilder.vertex(matrix, x, y1, 0)
                .color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F)
                .texture((u + 0.0F) / textureWidth, (v + regionHeight) / textureHeight)
                .next();

        index++;
        try {
            color = colors[index];
        } catch (Exception ignore) {
        }


        bufferBuilder.vertex(matrix, x1, y1, 0)
                .color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F)
                .texture((u + regionWidth) / textureWidth, (v + regionHeight) / textureHeight)
                .next();

        index++;
        try {
            color = colors[index];
        } catch (Exception ignore) {
        }

        bufferBuilder.vertex(matrix, x1, y, 0)
                .color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F)
                .texture((u + regionWidth) / textureWidth, (v + 0.0F) / textureHeight)
                .next();

        Tessellator.getInstance().draw();

        end(depthTest);
        return this;
    }

    public static class TextureRegion {
        private final float x, y, x1, y1, w, h, u, v, textureWidth, textureHeight, regionWidth, regionHeight;

        public TextureRegion(float scale) {
            this(scale, scale);
        }

        public TextureRegion(float width, float height) {
            this(-width / 2, -height / 2, width, height);
        }

        public TextureRegion(float x, float y, float w, float h) {
            this(x, y, w, h, 0, 0, w, h, w, h);
        }

        public TextureRegion(float x, float y, float w, float h, float u, float v, float textureWidth, float textureHeight, float regionWidth, float regionHeight) {
            this.x = x;
            this.y = y;
            this.x1 = x + w;
            this.y1 = y + h;
            this.w = w;
            this.h = h;
            this.u = u;
            this.v = v;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.regionWidth = regionWidth;
            this.regionHeight = regionHeight;
        }

        public float regionWidth() {
            return regionWidth;
        }

        public float regionHeight() {
            return regionHeight;
        }

        public float x1() {
            return x1;
        }

        public float y1() {
            return y1;
        }

        public float textureWidth() {
            return textureWidth;
        }

        public float textureHeight() {
            return textureHeight;
        }

        public float u() {
            return u;
        }

        public float v() {
            return v;
        }

        public float x() {
            return x;
        }

        public float y() {
            return y;
        }

        public float w() {
            return w;
        }

        public float h() {
            return h;
        }
    }
}
