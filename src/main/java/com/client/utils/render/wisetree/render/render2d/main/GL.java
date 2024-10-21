package com.client.utils.render.wisetree.render.render2d.main;

import com.client.utils.color.ColorUtils;
import com.client.utils.math.rect.DoubleRect;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.math.rect.IntRect;
import com.client.utils.render.wisetree.render.render2d.utils.AntiAliasing;
import com.client.utils.render.wisetree.render.render2d.utils.GlowFilter;
import com.client.utils.render.wisetree.render.render2d.utils.shader.Shader;
import com.client.utils.render.wisetree.render.render2d.utils.shader.Utils;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static com.mojang.blaze3d.platform.GlStateManager.bindTexture;
import static org.lwjgl.opengl.GL11.*;

public class GL {
    public static final HashMap<Integer, Integer> glowCache = new HashMap<>();
    public static Shader ROUNDED = new Shader(Shader.ROUNDED_FRAG);
    public static Shader ROUNDED_BLURRED = new Shader(Shader.ROUNDED_BLURRED);
    public static Shader ROUNDED_BLURRED_GRADIENT = new Shader(Shader.ROUNDED_BLURRED_GRADIENT);
    public static Shader ROUNDED_GRADIENT = new Shader(Shader.ROUNDED_GRADIENT);
    public static Shader ROUNDED_TEXTURE = new Shader(Shader.ROUNDED_TEXTURE_FRAG);
    public static Shader ROUNDED_GLOW = new Shader(Shader.ROUNDED_GLOW);
    public static Shader ROUNDED_OUTLINE = new Shader(Shader.ROUNDED_OUTLINE);

    public static final int STEPS = 60;
    public static final double ANGLE =  Math.PI * 2 / STEPS;
    public static final int EX_STEPS = 120;
    public static final double EX_ANGLE =  Math.PI * 2 / EX_STEPS;

    public static void drawCircle(double x, double y, double radius, double blurRadius, Color color) {
        Color transparent = ColorUtils.injectAlpha(color, 0);
        prepare();
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.0001f);
        glShadeModel(GL_SMOOTH);
        Renderer3D.color(color);

        glBegin(GL_TRIANGLE_FAN);
        for(int i = 0; i <= EX_STEPS; i++) {
            glVertex2d(x + radius * Math.sin(EX_ANGLE * i),
                    y + radius * Math.cos(EX_ANGLE * i)
            );
        }
        glEnd();

        glBegin(GL_TRIANGLE_STRIP);
        for (int i = 0; i <= EX_STEPS + 1; i++) {
            if(i % 2 == 1) {
                Renderer3D.color(transparent);
                glVertex2d(x + (radius + blurRadius) * Math.sin(EX_ANGLE * i),
                        y + (radius + blurRadius) * Math.cos(EX_ANGLE * i));
            } else {
                Renderer3D.color(color);
                glVertex2d(x + radius * Math.sin(EX_ANGLE * i),
                        y + radius * Math.cos(EX_ANGLE * i));
            }
        }
        glEnd();

        glShadeModel(GL_FLAT);
        glDisable(GL_ALPHA_TEST);
        end();
    }

    public static void drawGlowOffset(double x, double y, double width, double height, double offset, int glowRadius, Color... color) {
        int texture = getGlowTexture((int) width, (int) height, glowRadius);
        if (texture == -1) return;

        GlStateManager.enableBlend();

        GL11.glEnable(GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL_GREATER, 0.0001f);

        GlStateManager.bindTexture(texture);
        width += glowRadius * 2;
        height += glowRadius * 2;
        x -= glowRadius;
        y -= glowRadius;

        GlStateManager.shadeModel(GL_SMOOTH);

        GL11.glBegin(GL_QUADS);
        Renderer3D.color(color[0]);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex2d(x + offset, y);

        if (color.length > 1) Renderer3D.color(color[1]);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2d(x, y + height);

        if (color.length > 2) Renderer3D.color(color[2]);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex2d(x + width - offset, y + height);

        if (color.length > 3) Renderer3D.color(color[3]);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(x + width, y);

        GL11.glEnd();
        GlStateManager.shadeModel(GL_FLAT);

        GlStateManager.bindTexture(0);
        GL11.glDisable(GL_ALPHA_TEST);
        GlStateManager.disableBlend();
    }

    public static void drawOutlineQuad(IntRect rect, Color... colors) {
        drawOutlineQuad(rect.getX(), rect.getY(), rect.getW(), rect.getH(), colors);
    }

    public static void drawOutlineQuad(DoubleRect rect, Color... colors) {
        drawOutlineQuad(rect.getX(), rect.getY(), rect.getW(), rect.getH(), colors);
    }

    public static void drawOutlineQuad(FloatRect rect, Color... colors) {
        drawOutlineQuad(rect.getX(), rect.getY(), rect.getW(), rect.getH(), colors);
    }

    public static void drawOutlineQuad(double x, double y, double w, double h, Color... colors) {
        prepare();
        Renderer3D.begin(GL_LINE_STRIP);

        Renderer3D.color(colors[0]);
        GL11.glVertex2d(x, y);

        if (colors.length > 1) Renderer3D.color(colors[1]);
        GL11.glVertex2d(x, y + h);

        if (colors.length > 2) Renderer3D.color(colors[2]);
        GL11.glVertex2d(x + w, y + h);

        if (colors.length > 3) Renderer3D.color(colors[3]);
        GL11.glVertex2d(x + w, y);

        Renderer3D.color(colors[0]);
        GL11.glVertex2d(x, y);

        Renderer3D.end();
        end();
    }
    public static void drawQuad(IntRect rect, Color... colors) {
        drawQuad(rect.getX(), rect.getY(), rect.getW(), rect.getH(), colors);
    }

    public static void drawQuad(DoubleRect rect, Color... colors) {
        drawQuad(rect.getX(), rect.getY(), rect.getW(), rect.getH(), colors);
    }

    public static void drawQuad(FloatRect rect, Color... colors) {
        drawQuad(rect.getX(), rect.getY(), rect.getW(), rect.getH(), colors);
    }

    public static void drawQuad(double x, double y, double w, double h, Color... colors) {
        prepare();
        Renderer3D.begin(GL_QUADS);

        Renderer3D.color(colors[0]);
        GL11.glVertex2d(x, y);

        if (colors.length > 1) Renderer3D.color(colors[1]);
        GL11.glVertex2d(x, y + h);

        if (colors.length > 2) Renderer3D.color(colors[2]);
        GL11.glVertex2d(x + w, y + h);

        if (colors.length > 3) Renderer3D.color(colors[3]);
        GL11.glVertex2d(x + w, y);

        Renderer3D.end();
        end();
    }

    public static void drawQuadCoords(double x, double y, double x1, double y1, Color... colors) {
        prepare();
        Renderer3D.begin(GL_QUADS);

        Renderer3D.color(colors[0]);
        GL11.glVertex2d(x, y);

        if (colors.length > 1) Renderer3D.color(colors[1]);
        GL11.glVertex2d(x, y1);

        if (colors.length > 2) Renderer3D.color(colors[2]);
        GL11.glVertex2d(x1, y1);

        if (colors.length > 3) Renderer3D.color(colors[3]);
        GL11.glVertex2d(x1, y);

        Renderer3D.end();
        end();
    }

    public static void drawOutline(IntRect rect, float lineW, Color... colors) {
        drawOutline(rect.getX(), rect.getY(), rect.getX2(), rect.getY2(), lineW, colors);
    }

    public static void drawOutline(DoubleRect rect, float lineW, Color... colors) {
        drawOutline(rect.getX(), rect.getY(), rect.getX2(), rect.getY2(), lineW, colors);
    }

    public static void drawOutline(FloatRect rect, float lineW, Color... colors) {
        drawOutline(rect.getX(), rect.getY(), rect.getX2(), rect.getY2(), lineW, colors);
    }

    public static void drawOutline(double x, double y, double x2, double y2, float lineW, Color... colors) {
        prepare();
        GL11.glLineWidth(lineW);
        Renderer3D.begin(GL_LINE_STRIP);

        if (colors.length >= 1) {
            Renderer3D.color(colors[0]);
        }
        GL11.glVertex2d(x, y);

        if (colors.length >= 2) {
            Renderer3D.color(colors[1]);
        }
        GL11.glVertex2d(x, y2);

        if (colors.length >= 3) {
            Renderer3D.color(colors[2]);
        }
        GL11.glVertex2d(x2, y2);

        if (colors.length >= 4) {
            Renderer3D.color(colors[3]);
        }
        GL11.glVertex2d(x2, y);

        if (colors.length >= 1) {
            Renderer3D.color(colors[0]);
        }
        GL11.glVertex2d(x, y);

        Renderer3D.end();
        end();
    }

    public static void drawLine(double x1, double y1, double x2, double y2, Color color) {
        drawLine(x1, y1, x2, y2, 1f, color, color);
    }

    public static void drawLine(double x1, double y1, double x2, double y2, float lineW, Color color) {
        drawLine(x1, y1, x2, y2, lineW, color, color);
    }

    public static void drawLine(double x1, double y1, double x2, double y2, Color color, Color color2) {
        drawLine(x1, y1, x2, y2, 1f, color, color2);
    }

    public static void drawLine(double x1, double y1, double x2, double y2, float lineW, Color color, Color color2) {
        prepare();
        GL11.glLineWidth(lineW);
        Renderer3D.begin(GL_LINES);
        Renderer3D.color(color);
        GL11.glVertex2d(x1, y1);
        Renderer3D.color(color2);
        GL11.glVertex2d(x2, y2);
        Renderer3D.end();
        end();
    }

    public static void drawShadowRect(FloatRect rect, Direction direction, Color color) {
        drawShadowRect(rect.getX(), rect.getY(), rect.getW(), rect.getH(), direction, color);
    }

    public static void drawShadowRect(DoubleRect rect, Direction direction, Color color) {
        drawShadowRect(rect.getX(), rect.getY(), rect.getW(), rect.getH(), direction, color);
    }

    public static void drawShadowRect(IntRect rect, Direction direction, Color color) {
        drawShadowRect(rect.getX(), rect.getY(), rect.getW(), rect.getH(), direction, color);
    }

    public static void drawShadowRect(double x, double y, double w, double h, Direction direction, Color color) {
        Color shadow = ColorUtils.injectAlpha(color, 0);

        prepare();
        GL11.glBegin(GL_QUADS);
        switch (direction) {
            case UP -> {
                Renderer3D.color(shadow);
                GL11.glVertex2d(x, y);
                Renderer3D.color(color);
                GL11.glVertex2d(x, y + h);
                Renderer3D.color(color);
                GL11.glVertex2d(x + w, y + h);
                Renderer3D.color(shadow);
                GL11.glVertex2d(x + w, y);
            }
            case DOWN -> {
                Renderer3D.color(color);
                GL11.glVertex2d(x, y);
                Renderer3D.color(shadow);
                GL11.glVertex2d(x, y + h);
                Renderer3D.color(shadow);
                GL11.glVertex2d(x + w, y + h);
                Renderer3D.color(color);
                GL11.glVertex2d(x + w, y);
            }
            case LEFT -> {
                Renderer3D.color(color);
                GL11.glVertex2d(x, y);
                GL11.glVertex2d(x, y + h);
                Renderer3D.color(shadow);
                GL11.glVertex2d(x + w, y + h);
                GL11.glVertex2d(x + w, y);
            }
            case RIGHT -> {
                Renderer3D.color(shadow);
                GL11.glVertex2d(x, y);
                GL11.glVertex2d(x, y + h);
                Renderer3D.color(color);
                GL11.glVertex2d(x + w, y + h);
                GL11.glVertex2d(x + w, y);
            }
        }
        GL11.glEnd();
        end();
    }

    public static void drawRoundedGradientRect(FloatRect rect, double radius, Color... colors) {
        drawRoundedGradientRect(rect.getX(), rect.getY(), rect.getW(), rect.getH(), radius, colors);
    }

    public static void drawRoundedGradientRect(DoubleRect rect, double radius, Color... colors) {
        drawRoundedGradientRect(rect.getX(), rect.getY(), rect.getW(), rect.getH(), radius, colors);
    }

    public static void drawRoundedGradientRect(IntRect rect, double radius, Color... colors) {
        drawRoundedGradientRect(rect.getX(), rect.getY(), rect.getW(), rect.getH(), radius, colors);
    }

    public static void drawRoundedGradientRect(double x, double y, double width, double height, double radius, Color... colors) {
        float[] c = ColorUtils.getColorComps(colors[0]);
        float[] c1 = ColorUtils.getColorComps(colors[1]);
        float[] c2 = ColorUtils.getColorComps(colors[2]);
        float[] c3 = ColorUtils.getColorComps(colors[3]);

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        ROUNDED_GRADIENT.load();
        ROUNDED_GRADIENT.setUniformf("size", (float)width * 2, (float)height * 2);
        ROUNDED_GRADIENT.setUniformf("round", (float)radius * 2);
        ROUNDED_GRADIENT.setUniformf("color1", c[0], c[1], c[2], c[3]);
        ROUNDED_GRADIENT.setUniformf("color2", c1[0], c1[1], c1[2], c1[3]);
        ROUNDED_GRADIENT.setUniformf("color3", c2[0], c2[1], c2[2], c2[3]);
        ROUNDED_GRADIENT.setUniformf("color4", c3[0], c3[1], c3[2], c3[3]);
        Shader.draw(x, y, width, height);
        ROUNDED_GRADIENT.unload();

        RenderSystem.defaultBlendFunc();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    public static void drawRoundedBlurredGradientRect(FloatRect data, double radius, double strength, Color... color) {
        drawRoundedBlurredGradientRect(data.getX(), data.getY(), data.getW(), data.getH(), radius, strength, color);
    }

    public static void drawRoundedBlurredGradientRect(DoubleRect data, double radius, double strength, Color... color) {
        drawRoundedBlurredGradientRect(data.getX(), data.getY(), data.getW(), data.getH(), radius, strength, color);
    }

    public static void drawRoundedBlurredGradientRect(IntRect data, double radius, double strength, Color... color) {
        drawRoundedBlurredGradientRect(data.getX(), data.getY(), data.getW(), data.getH(), radius, strength, color);
    }

    public static void drawRoundedBlurredGradientRect(double x, double y, double width, double height, double round, double strength, Color... colors) {
        float[] c = ColorUtils.getColorComps(colors[0]);
        float[] c1 = ColorUtils.getColorComps(colors[1]);
        float[] c2 = ColorUtils.getColorComps(colors[2]);
        float[] c3 = ColorUtils.getColorComps(colors[3]);

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.0001f);

        ROUNDED_BLURRED_GRADIENT.load();
        ROUNDED_BLURRED_GRADIENT.setUniformf("size", (float)(width + 2 * strength), (float)(height + 2 * strength));
        ROUNDED_BLURRED_GRADIENT.setUniformf("softness", (float) strength);
        ROUNDED_BLURRED_GRADIENT.setUniformf("radius", (float)round);
        ROUNDED_BLURRED_GRADIENT.setUniformf("color1", c[0], c[1], c[2], c[3]);
        ROUNDED_BLURRED_GRADIENT.setUniformf("color2", c1[0], c1[1], c1[2], c1[3]);
        ROUNDED_BLURRED_GRADIENT.setUniformf("color3", c2[0], c2[1], c2[2], c2[3]);
        ROUNDED_BLURRED_GRADIENT.setUniformf("color4", c3[0], c3[1], c3[2], c3[3]);
        Shader.draw(x - strength, y - strength, width + strength * 2, height + strength * 2);
        ROUNDED_BLURRED_GRADIENT.unload();

        glDisable(GL_ALPHA_TEST);

        RenderSystem.defaultBlendFunc();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    public static void drawRoundedBlurredRect(FloatRect data, double radius, double strength, Color color) {
        drawRoundedBlurredRect(data.getX(), data.getY(), data.getW(), data.getH(), radius, strength, color);
    }

    public static void drawRoundedBlurredRect(DoubleRect data, double radius, double strength, Color color) {
        drawRoundedBlurredRect(data.getX(), data.getY(), data.getW(), data.getH(), radius, strength, color);
    }

    public static void drawRoundedBlurredRect(IntRect data, double radius, double strength, Color color) {
        drawRoundedBlurredRect(data.getX(), data.getY(), data.getW(), data.getH(), radius, strength, color);
    }

    public static void drawRoundedBlurredRect(double x, double y, double width, double height, double round, double strength, Color color) {
        float[] c = ColorUtils.getColorComps(color);

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        ROUNDED_BLURRED.load();
        ROUNDED_BLURRED.setUniformf("size", (float)(width + 2 * strength), (float)(height + 2 * strength));
        ROUNDED_BLURRED.setUniformf("softness", (float) strength);
        ROUNDED_BLURRED.setUniformf("radius", (float)round);
        ROUNDED_BLURRED.setUniformf("glowRadius", 1f);
        ROUNDED_BLURRED.setUniformf("color", c[0], c[1], c[2], c[3]);
        Shader.draw(x - strength, y - strength, width + strength * 2, height + strength * 2);
        ROUNDED_BLURRED.unload();

        RenderSystem.defaultBlendFunc();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    public static void drawRoundedGlowRect(FloatRect rect, double round, double glowRadius, Color... color) {
        drawRoundedGlowRect(rect.getX(), rect.getY(), rect.getW(), rect.getH(), round, glowRadius, color);
    }

    public static void drawRoundedGlowRect(FloatRect rect, double round, double glowRadius, Color color) {
        drawRoundedGlowRect(rect.getX(), rect.getY(), rect.getW(), rect.getH(), round, glowRadius, color, color, color, color);
    }

    public static void drawRoundedGlowRect(double x, double y, double width, double height, double round, double glowRadius, Color... colors) {
        float[] c = ColorUtils.getColorComps(colors[0]);
        float[] c1 = ColorUtils.getColorComps(colors[1]);
        float[] c2 = ColorUtils.getColorComps(colors[2]);
        float[] c3 = ColorUtils.getColorComps(colors[3]);

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glowRadius = Math.max(1.5, glowRadius);

        ROUNDED_GLOW.load();
        ROUNDED_GLOW.setUniformf("size", (float)(width + 2 * glowRadius), (float)(height + 2 * glowRadius));
        ROUNDED_GLOW.setUniformf("softness", (float) glowRadius);
        ROUNDED_GLOW.setUniformf("radius", (float)round);
        ROUNDED_GLOW.setUniformf("glowRadius", (float) 3);
        ROUNDED_GLOW.setUniformf("color1", c[0], c[1], c[2], c[3]);
        ROUNDED_GLOW.setUniformf("color2", c1[0], c1[1], c1[2], c1[3]);
        ROUNDED_GLOW.setUniformf("color3", c2[0], c2[1], c2[2], c2[3]);
        ROUNDED_GLOW.setUniformf("color4", c3[0], c3[1], c3[2], c3[3]);
        Shader.draw(x - glowRadius, y - glowRadius, width + glowRadius * 2, height + glowRadius * 2);
        ROUNDED_GLOW.unload();

        RenderSystem.defaultBlendFunc();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    public static void drawRoundedRect(FloatRect data, double radius, Color color) {
        drawRoundedRect(data.getX(), data.getY(), data.getW(), data.getH(), radius, color);
    }

    public static void drawRoundedRect(DoubleRect data, double radius, Color color) {
        drawRoundedRect(data.getX(), data.getY(), data.getW(), data.getH(), radius, color);
    }

    public static void drawRoundedRect(IntRect data, double radius, Color color) {
        drawRoundedRect(data.getX(), data.getY(), data.getW(), data.getH(), radius, color);
    }

    public static void drawRoundedRect(double x, double y, double width, double height, double radius, Color color) {
        float[] c = ColorUtils.getColorComps(color);

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        ROUNDED.load();
        ROUNDED.setUniformf("size", (float)width * 2, (float)height * 2);
        ROUNDED.setUniformf("round", (float)radius * 2);
        ROUNDED.setUniformf("color", c[0], c[1], c[2], c[3]);
        Shader.draw(x, y, width, height);
        ROUNDED.unload();

        RenderSystem.defaultBlendFunc();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    public static void drawRoundedOutline(FloatRect rect, double radius, double lineWidth, Color color) {
        drawRoundedGradientOutline(rect.getX(), rect.getY(), rect.getW(), rect.getH(), radius, lineWidth, color, color, color, color);
    }

    public static void drawRoundedGradientOutline(FloatRect rect, double radius, double lineWidth, Color... colors) {
        drawRoundedGradientOutline(rect.getX(), rect.getY(), rect.getW(), rect.getH(), radius, lineWidth, colors);
    }

    public static void drawRoundedGradientOutline(double x, double y, double width, double height, double radius, double lineWidth, Color... colors) {
        float[] c = ColorUtils.getColorComps(colors[0]);
        float[] c1 = ColorUtils.getColorComps(colors[1]);
        float[] c2 = ColorUtils.getColorComps(colors[2]);
        float[] c3 = ColorUtils.getColorComps(colors[3]);

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        ROUNDED_OUTLINE.load();
        ROUNDED_OUTLINE.setUniformf("size", (float)width * 2, (float)height * 2);
        ROUNDED_OUTLINE.setUniformf("round", (float)radius * 2);
        ROUNDED_OUTLINE.setUniformf("thickness", (float) lineWidth);
        ROUNDED_OUTLINE.setUniformf("color1", c[0], c[1], c[2], c[3]);
        ROUNDED_OUTLINE.setUniformf("color2", c1[0], c1[1], c1[2], c1[3]);
        ROUNDED_OUTLINE.setUniformf("color3", c2[0], c2[1], c2[2], c2[3]);
        ROUNDED_OUTLINE.setUniformf("color4", c3[0], c3[1], c3[2], c3[3]);
        Shader.draw(x, y, width, height);
        ROUNDED_OUTLINE.unload();

        RenderSystem.defaultBlendFunc();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    public static void drawRoundedTexture(Identifier identifier, double x, double y, double width, double height, double radius) {
        drawRoundedTexture(Utils.getTextureId(identifier), x, y, width, height, radius);
    }

    public static void drawRoundedTexture(int texId, double x, double y, double width, double height, double radius) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        ROUNDED_TEXTURE.load();
        ROUNDED_TEXTURE.setUniformf("size", (float)width * 2, (float)height * 2);
        ROUNDED_TEXTURE.setUniformf("round", (float)radius * 2);
        bindTexture(texId);
        Shader.draw(x, y, width, height);
        bindTexture(0);
        ROUNDED_TEXTURE.unload();

        GlStateManager.disableBlend();
    }

    public static void drawGlow(FloatRect data, int glowRadius, Color... color) {
        drawGlow(data.getX(), data.getY(), data.getW(), data.getH(), glowRadius, color);
    }

    public static void drawGlow(IntRect data, int glowRadius, Color... color) {
        drawGlow(data.getX(), data.getY(), data.getW(), data.getH(), glowRadius, color);
    }

    public static void drawGlow(DoubleRect data, int glowRadius, Color... color) {
        drawGlow(data.getX(), data.getY(), data.getW(), data.getH(), glowRadius, color);
    }

    public static void drawGlow(double x, double y, double width, double height, int glowRadius, Color... color) {
        int texture = getGlowTexture((int) width, (int) height, glowRadius);
        if (texture == -1) return;

        GlStateManager.enableBlend();

        GL11.glEnable(GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL_GREATER, 0.0001f);

        GlStateManager.bindTexture(texture);
        width += glowRadius * 2;
        height += glowRadius * 2;
        x -= glowRadius;
        y -= glowRadius;

        GlStateManager.shadeModel(GL_SMOOTH);

        GL11.glBegin(GL_QUADS);
        Renderer3D.color(color[0]);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex2d(x, y);

        if (color.length > 1) Renderer3D.color(color[1]);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2d(x, y + height);

        if (color.length > 2) Renderer3D.color(color[2]);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex2d(x + width, y + height);

        if (color.length > 3) Renderer3D.color(color[3]);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(x + width, y);

        GL11.glEnd();
        GlStateManager.shadeModel(GL_FLAT);

        GlStateManager.bindTexture(0);
        GL11.glDisable(GL_ALPHA_TEST);
        GlStateManager.disableBlend();
    }

    private static int getGlowTexture(int width, int height, int blurRadius) {
        int identifier = (width * 401 + height) * 407 + blurRadius;
        int texId = glowCache.getOrDefault(identifier, -1);

        if(texId == -1) {
            BufferedImage original = new BufferedImage(width + blurRadius * 2, height + blurRadius * 2, BufferedImage.TYPE_INT_ARGB_PRE);

            Graphics g = original.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(blurRadius, blurRadius, width, height);
            g.dispose();

            GlowFilter glow = new GlowFilter(blurRadius);
            BufferedImage blurred = glow.filter(original, null);
            try {
                texId = Utils.loadTexture(blurred);
                glowCache.put(identifier, texId);
            } catch (Exception ignored) {
            }
        }

        return texId;
    }

    public static void prepare() {
        prepare(true);
    }

    public static void prepare(boolean disableTexture) {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableCull();

        RenderSystem.blendFunc(770, 771);
        RenderSystem.shadeModel(GL_SMOOTH);

        if (disableTexture) {
            GL11.glDisable(GL_TEXTURE_2D);
        }

        GL11.glEnable(GL_LINE_SMOOTH);
        AntiAliasing.enable(true, true, true);
        Renderer3D.reset();
    }

    public static void end() {
        end(true);
    }

    public static void end(boolean disableTexture) {
        AntiAliasing.disable(true, true, true);
        Renderer3D.reset();
        GL11.glLineWidth(1F);
        GL11.glDisable(GL_LINE_SMOOTH);

        if (disableTexture) {
            GL11.glEnable(GL_TEXTURE_2D);
        }

        RenderSystem.shadeModel(GL_FLAT);
        RenderSystem.defaultBlendFunc();

        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
}