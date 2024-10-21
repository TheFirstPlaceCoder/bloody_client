package com.client.utils.render.wisetree.render.render2d.utils.shader.shaders;

import com.client.utils.render.wisetree.render.render2d.utils.shader.Shader;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.client.BloodyClient.mc;
import static com.client.utils.render.wisetree.render.render2d.utils.shader.Shader.GAUSSIAN_BLOOM_SHADER;

public class GaussianBloomShader {
    private static final Shader bloom = new Shader(GAUSSIAN_BLOOM_SHADER);
    private static final ConcurrentLinkedQueue<RenderCall> renderQueue = Queues.newConcurrentLinkedQueue();

    private static final Framebuffer inFrameBuffer = new Framebuffer(1, 1, true, false);
    private static final Framebuffer outFrameBuffer = new Framebuffer(1, 1, true, false);

    public static void registerRenderCall(RenderCall rc) {
        renderQueue.add(rc);
    }

    public static void free() {
        renderQueue.clear();
    }

    public static void draw(int radius, float exp, boolean fill, float direction) {
        if (renderQueue.isEmpty())
            return;

        setupBuffer(inFrameBuffer);
        setupBuffer(outFrameBuffer);

        inFrameBuffer.beginWrite(true);
        while (!renderQueue.isEmpty()) {
            renderQueue.poll().execute();
        }
        inFrameBuffer.endWrite();

        outFrameBuffer.beginWrite(true);

        bloom.load();
        bloom.setUniformf("radius", radius);
        bloom.setUniformf("exposure", exp);
        bloom.setUniformf("textureIn", 0);
        bloom.setUniformf("textureToCheck", 20);
        bloom.setUniformf("avoidTexture", fill ? 1 : 0);
        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(128);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(calculateGaussianValue(i, radius / 2));
        }
        weightBuffer.rewind();
        RenderSystem.glUniform1(bloom.getUniform("weights"), weightBuffer);
        bloom.setUniformf("texelSize", 1.0F / (float) MinecraftClient.getInstance().getWindow().getWidth(),
                1.0F / (float) MinecraftClient.getInstance().getWindow().getHeight());
        bloom.setUniformf("direction", direction, 0.0F);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL30.GL_ONE, GL30.GL_SRC_ALPHA);
        GL30.glAlphaFunc(GL30.GL_GREATER, 0.0001f);

        inFrameBuffer.beginRead();
        Shader.draw();

        mc.getFramebuffer().beginWrite(false);
        GlStateManager.blendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        bloom.setUniformf("direction", 0.0F, direction);

        outFrameBuffer.beginRead();
        GL30.glActiveTexture(GL30.GL_TEXTURE20);
        inFrameBuffer.beginRead();
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        Shader.draw();

        bloom.unload();
        outFrameBuffer.endWrite();
        GlStateManager.bindTexture(0);
        GlStateManager.disableBlend();
        mc.getFramebuffer().beginWrite(false);
    }

    private static Framebuffer setupBuffer(Framebuffer frameBuffer) {
        if (frameBuffer.viewportWidth != mc.getWindow().getWidth()
                || frameBuffer.viewportHeight != mc.getWindow().getHeight())
            frameBuffer.resize(Math.max(1, mc.getWindow().getWidth()), Math.max(1, mc.getWindow().getHeight()),
                    false);
        else
            frameBuffer.clear(false);
        frameBuffer.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        return frameBuffer;
    }

    private static float calculateGaussianValue(float x, float sigma) {
        double PI = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }
}