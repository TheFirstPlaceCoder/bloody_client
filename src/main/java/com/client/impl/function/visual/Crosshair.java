package com.client.impl.function.visual;

import api.interfaces.EventHandler;
import com.client.event.events.Render2DEvent;
import com.client.event.events.RenderOverlayEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.color.Colors;
import com.client.utils.render.Renderer2D;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.awt.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Crosshair extends Function {
    public final ListSetting mode = List().name("Режим").list(List.of("Классический", "Круг", "Свастика")).defaultValue("Классический").build();
    public final BooleanSetting dynamic = Boolean().name("Динамичный").defaultValue(true).build();
    public final DoubleSetting length = Double().name("Размер").defaultValue(1.0).min(0).max(5).build();

    public Crosshair() {
        super("Crosshair", Category.VISUAL);
    }

    private float progress;

    @Override
    public void onRenderOverlayEvent(RenderOverlayEvent event) {
        if (event.type.equals(RenderOverlayEvent.Type.CROSSHAIR)) event.cancel();
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (mc.options.getPerspective() == Perspective.FIRST_PERSON) {
            float centerX = (float) mc.getWindow().getScaledWidth() / 2;
            float centerY = (float) mc.getWindow().getScaledHeight() / 2;

            if (dynamic.get()) {
                progress = MathHelper.lerp(event.tickDelta, progress, (mode.get().equals("Свастика") ? 360 : 100) * mc.player.getAttackCooldownProgress(1.0F));
            } else {
                progress = 0;
            }

            switch (mode.get()) {
                case "Классический" -> {
                    float width = 15 - (dynamic.get() ? 12 * mc.player.getAttackCooldownProgress(1.0F) : 12);
                    GL.drawQuadCoords(centerX - 0.5 - 0.5f, centerY - 0.5 - 0.5f, centerX + 0.5 + 0.5f, centerY + 0.5 + 0.5f, Color.BLACK);
                    GL.drawQuadCoords(centerX - 0.5, centerY - 0.5, centerX + 0.5, centerY + 0.5, Color.WHITE);
                    GL.drawQuadCoords(centerX - width - length.floatValue() - 0.5f, centerY - (1f / 2) - 0.5f, centerX - width + 0.5f, centerY + (1f / 2) + 0.5f, Color.BLACK);
                    GL.drawQuadCoords(centerX + width - 0.5f, centerY - (1f / 2) - 0.5f, centerX + width + length.floatValue() + 0.5f, centerY + (1f / 2) + 0.5f, Color.BLACK);
                    GL.drawQuadCoords(centerX - (1f / 2) - 0.5f, centerY - width - length.floatValue() - 0.5f, centerX + (1f / 2) + 0.5f, centerY - width + 0.5f, Color.BLACK);
                    GL.drawQuadCoords(centerX - (1f / 2) - 0.5f, centerY + width - 0.5f, centerX + (1f / 2) + 0.5f, centerY + width + length.floatValue() + 0.5f, Color.BLACK);
                    GL.drawQuadCoords(centerX - width - length.floatValue(), centerY - (1f / 2), centerX - width, centerY + (1f / 2), Color.WHITE);
                    GL.drawQuadCoords(centerX + width, centerY - (1f / 2), centerX + width + length.floatValue(), centerY + (1f / 2), Color.WHITE);
                    GL.drawQuadCoords(centerX - (1f / 2), centerY - width - length.floatValue(), centerX + (1f / 2), centerY - width, Color.WHITE);
                    GL.drawQuadCoords(centerX - (1f / 2), centerY + width, centerX + (1f / 2), centerY + width + length.floatValue(), Color.WHITE);
                }

                case "Круг" -> {
                    drawCircleOutline(centerX, centerY, 3.5 * length.floatValue(), 100, new Color(30, 30, 30, 255));
                    drawCircleOutline(centerX, centerY, 3.5 * length.floatValue(), (int) progress + 1, Colors.getColor(0));
                }

                case "Свастика" -> {
                    MatrixStack stack = new MatrixStack();
                    stack.translate(centerX, centerY, 0);
                    stack.scale(0.5f, 0.5f, 0.5f);
                    stack.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(progress));
                    TextureGL.create().bind(new Identifier("bloody-client", "client/crosshair.png")).draw(stack, new TextureGL.TextureRegion(16 * length.floatValue()), false, Colors.getColor(0));
                }
            }
        }
    }

    private void drawCircleOutline(double x, double y, double radius, int progress, Color color) {
        float steps = (60F / 100F) * progress;

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.blendFunc(770, 771);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth((float) 3.0);
        Renderer3D.color(color);
        glShadeModel(GL_SMOOTH);
        glBegin(GL_LINE_STRIP);
        for(int i = 0; i <= steps; i++) {
            Renderer3D.color(color);
            glVertex2d(x + radius * Math.sin(Math.PI * 2 / 60F * i), y + radius * Math.cos(Math.PI * 2 / 60F * i));
        }
        glEnd();
        glShadeModel(GL_FLAT);
        glDisable(GL_LINE_SMOOTH);
        RenderSystem.defaultBlendFunc();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        Renderer3D.color(Color.WHITE);
    }
}
