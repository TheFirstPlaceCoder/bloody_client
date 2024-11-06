package com.client.utils.render;

import mixin.accessor.ShaderEffectAccessor;
import mixin.accessor.WorldRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.Identifier;

import java.io.IOException;

import static com.client.BloodyClient.mc;

public class OutlinesCompanion {
    public static boolean loadingOutlineShader;
    public static boolean renderingOutlines;

    public static Framebuffer outlinesFbo;
    public static OutlineVertexConsumerProvider vertexConsumerProvider;
    private static ShaderEffect outlinesShader;

    public static void load() {
        try {
            if (outlinesShader != null) {
                outlinesShader.close();
            }

            Identifier identifier = new Identifier("bloody-client", "shaders/post/my_entity_outline.json");

            loadingOutlineShader = true;
            outlinesShader = new ShaderEffect(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(),
                    identifier
            );
            outlinesShader.setupDimensions(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
            outlinesFbo = outlinesShader.getSecondaryTarget("final");
            vertexConsumerProvider = new OutlineVertexConsumerProvider(mc.getBufferBuilders().getEntityVertexConsumers());
            loadingOutlineShader = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void beginRender() {
        outlinesFbo.clear(MinecraftClient.IS_SYSTEM_MAC);
        mc.getFramebuffer().beginWrite(false);
    }

    public static void endRender(float tickDelta) {
        WorldRenderer worldRenderer = mc.worldRenderer;
        WorldRendererAccessor wra = (WorldRendererAccessor) worldRenderer;

        Framebuffer fbo = worldRenderer.getEntityOutlinesFramebuffer();
        wra.setEntityOutlinesFramebuffer(outlinesFbo);
        vertexConsumerProvider.draw();
        wra.setEntityOutlinesFramebuffer(fbo);

        outlinesShader.render(tickDelta);
        mc.getFramebuffer().beginWrite(false);
    }

    public static void renderFbo() {
        outlinesFbo.draw(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), false);
    }

    public static void onResized(int width, int height) {
        if (outlinesShader != null) outlinesShader.setupDimensions(width, height);
    }

    public static void setUniform(String name, float value) {
        ((ShaderEffectAccessor) outlinesShader).getPasses().get(0).getProgram().getUniformByName(name).set(value);
    }

    public static void setUniform(String name, float r, float g, float b, float a) {
        ((ShaderEffectAccessor) outlinesShader).getPasses().get(0).getProgram().getUniformByName(name).set(r, g, b, a);
    }

    public static void setUniform(String name, float r, float g, float b) {
        ((ShaderEffectAccessor) outlinesShader).getPasses().get(0).getProgram().getUniformByName(name).set(r, g, b);
    }

    public static void setUniform(String name, float first, float second) {
        ((ShaderEffectAccessor) outlinesShader).getPasses().get(0).getProgram().getUniformByName(name).set(first, second);
    }
}