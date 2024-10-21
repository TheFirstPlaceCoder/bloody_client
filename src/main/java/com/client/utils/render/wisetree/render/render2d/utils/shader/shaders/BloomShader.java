package com.client.utils.render.wisetree.render.render2d.utils.shader.shaders;

import com.client.utils.render.wisetree.render.render2d.utils.shader.Shader;
import com.client.utils.render.wisetree.render.render2d.utils.shader.Utils;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderCall;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL30;

import java.util.concurrent.ConcurrentLinkedQueue;

import static com.client.BloodyClient.mc;

public class BloomShader {
    public static Shader BLOOM = new Shader(Shader.BLOOM_FRAG);
    private static final ConcurrentLinkedQueue<RenderCall> renderQueue = Queues.newConcurrentLinkedQueue();
    private static final Framebuffer inFrameBuffer = new Framebuffer((int)(mc.getWindow().getScaledWidth() / 2d), (int)(mc.getWindow().getScaledHeight() / 2d), true, false);
    private static final Framebuffer outFrameBuffer = new Framebuffer((int)(mc.getWindow().getScaledWidth() / 2d), (int)(mc.getWindow().getScaledHeight() / 2d), true, false);

    public static void registerRenderCall(RenderCall rc) {
        renderQueue.add(rc);
    }

    public static void draw(int radius) {
        if(renderQueue.isEmpty())
            return;

        setupBuffer(inFrameBuffer);
        setupBuffer(outFrameBuffer);

        inFrameBuffer.beginWrite(true);

        while (!renderQueue.isEmpty()) {
            renderQueue.poll().execute();
        }

        outFrameBuffer.beginWrite(true);

        BLOOM.load();
        BLOOM.setUniformf("radius", radius);
        BLOOM.setUniformi("sampler1", 0);
        BLOOM.setUniformi("sampler2", 20);
        BLOOM.setUniformfb("kernel", Utils.getKernel(radius));
        BLOOM.setUniformf("texelSize", 1.0F / (float) mc.getWindow().getScaledWidth(), 1.0F / (float) mc.getWindow().getScaledHeight());
        BLOOM.setUniformf("direction", 2.0F, 0.0F);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL30.GL_ONE, GL30.GL_SRC_ALPHA);
        GL30.glAlphaFunc(GL30.GL_GREATER, 0.0001f);

        inFrameBuffer.beginRead();
        Shader.draw();

        mc.getFramebuffer().beginWrite(true);
        GlStateManager.blendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        BLOOM.setUniformf("direction", 0.0F, 2.0F);

        outFrameBuffer.beginRead();
        GL30.glActiveTexture(GL30.GL_TEXTURE20);
        inFrameBuffer.beginRead();
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        Shader.draw();

        BLOOM.unload();
        inFrameBuffer.endRead();
        GlStateManager.disableBlend();
        renderQueue.clear();
    }

    private static void setupBuffer(Framebuffer frameBuffer) {
        if (frameBuffer.viewportWidth != MinecraftClient.getInstance().getWindow().getScaledWidth() || frameBuffer.viewportHeight != MinecraftClient.getInstance().getWindow().getScaledHeight()) {
            frameBuffer.resize(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight(), false);
        } else {
            frameBuffer.clear(false);
        }

        frameBuffer.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }
}
