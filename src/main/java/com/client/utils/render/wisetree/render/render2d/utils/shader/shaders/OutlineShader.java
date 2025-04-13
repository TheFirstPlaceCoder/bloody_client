package com.client.utils.render.wisetree.render.render2d.utils.shader.shaders;

import com.client.impl.function.visual.Hands;
import com.client.interfaces.IShaderEffect;
import com.client.system.function.FunctionManager;
import com.client.utils.color.Colors;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL30C;

import java.util.ArrayList;
import java.util.List;

import static com.client.BloodyClient.mc;

public class OutlineShader {
    public static ManagedShaderEffect DEFAULT;

    private final static List<RenderTask> tasks = new ArrayList<>();
    private CustomFramebuffer shaderBuffer;

    public void renderShader(Runnable runnable) {
        if (FunctionManager.get(Hands.class) != null && FunctionManager.get(Hands.class).isEnabled())
            tasks.add(new RenderTask(runnable));
    }

    public void renderShaders() {
        if (DEFAULT == null) {
            shaderBuffer = new CustomFramebuffer(mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight);
            reloadShaders();
        }

        if(shaderBuffer == null)
            return;

        tasks.forEach(t -> applyShader(t.task()));
        tasks.clear();
    }

    public boolean shouldReturn() {
        if (DEFAULT == null) {
            shaderBuffer = new CustomFramebuffer(mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight);
            reloadShaders();
        }

        if (shaderBuffer == null)
            return true;

        return false;
    }

    public void applyShader(Runnable runnable) {
        Framebuffer MCBuffer = MinecraftClient.getInstance().getFramebuffer();
        if (shaderBuffer.textureWidth != MCBuffer.textureWidth || shaderBuffer.textureHeight != MCBuffer.textureHeight)
            shaderBuffer.resize(MCBuffer.textureWidth, MCBuffer.textureHeight, false);
        GlStateManager.bindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, shaderBuffer.fbo);
        shaderBuffer.beginWrite(true);
        runnable.run();
        shaderBuffer.endWrite();
        GlStateManager.bindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, MCBuffer.fbo);
        MCBuffer.beginWrite(false);
        ManagedShaderEffect shader = DEFAULT;
        Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
        ShaderEffect effect = shader.getShaderEffect();

        if (effect != null)
            ((IShaderEffect) effect).addFakeTargetHook("bufIn", shaderBuffer);

        Framebuffer outBuffer = shader.getShaderEffect().getSecondaryTarget("bufOut");
        setupShader(shader);
        shaderBuffer.clear(false);
        mainBuffer.beginWrite(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        outBuffer.draw(outBuffer.textureWidth, outBuffer.textureHeight, false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    public void setupShader(ManagedShaderEffect effect) {
        if (FunctionManager.get(Hands.class) == null) return;
        Hands hands = FunctionManager.get(Hands.class);

        effect.setUniformValue("resolution", (float) mc.getWindow().getScaledWidth(), (float) mc.getWindow().getScaledHeight());
        effect.setUniformValue("color", hands.colorSetting.get().getRed(), hands.colorSetting.get().getGreen(), hands.colorSetting.get().getBlue());
        effect.setUniformValue("radius", hands.lineWidth.get().floatValue());
        effect.setUniformValue("fillOpacity", hands.getOpacity());
        effect.render(mc.getTickDelta());
    }

    public void reloadShaders() {
        Identifier identifier = new Identifier("bloody-client", "shaders/post/hand_outline.json");
        DEFAULT = ShaderEffectManager.getInstance().manage(identifier);

        if (DEFAULT == null) System.err.println("NULL");
        if (DEFAULT.getShaderEffect() == null) System.err.println("NULL EFFECT");
    }

    public static class CustomFramebuffer extends Framebuffer {
        public CustomFramebuffer(int width, int height) {
            super(width, height, false, false);
            resize(width, height, true);
            setClearColor(0f, 0f, 0f, 0f);
        }
    }

    public boolean fullNullCheck() {
        if (DEFAULT == null) {
            shaderBuffer = new CustomFramebuffer(mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight);
            reloadShaders();
            return true;
        }

        return false;
    }

    public record RenderTask(Runnable task) {
    }
}
