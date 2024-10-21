package com.client.utils.render.wisetree.render.render2d.utils.shader.shaders;

import com.client.utils.render.wisetree.render.render2d.utils.shader.Shader;
import com.client.utils.render.wisetree.render.render2d.utils.shader.Utils;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderCall;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.concurrent.ConcurrentLinkedQueue;

import static com.client.system.function.Function.mc;

public class BlurShader {
    public static Shader BLUR = new Shader(Shader.BLUR_FRAG);
    private static final ConcurrentLinkedQueue<RenderCall> renderQueue = Queues.newConcurrentLinkedQueue();
    private static final Framebuffer inFrameBuffer = new Framebuffer((int)(mc.getWindow().getScaledWidth() / 2d), (int)(mc.getWindow().getScaledHeight() / 2d), true, false);
    private static final Framebuffer outFrameBuffer = new Framebuffer((int)(mc.getWindow().getScaledWidth() / 2d), (int)(mc.getWindow().getScaledHeight() / 2d), true, false);

    public static void registerRenderCall(RenderCall rc) {
        renderQueue.add(rc);
    }

    public static void draw(int radius) {
        if (renderQueue.isEmpty()) return;

        setupBuffer(inFrameBuffer);
        setupBuffer(outFrameBuffer);

        inFrameBuffer.beginWrite(true);

        while (!renderQueue.isEmpty()) {
            renderQueue.poll().execute();
        }

        outFrameBuffer.beginWrite(true);

        BLUR.load();
        BLUR.setUniformf("radius", radius);
        BLUR.setUniformi("sampler1", 0);
        BLUR.setUniformi("sampler2", 20);
        BLUR.setUniformfb("kernel", Utils.getKernel((int) radius));
        BLUR.setUniformf("texelSize", 1f/(float)mc.getWindow().getScaledWidth(),1f/ (float)mc.getWindow().getScaledHeight());
        BLUR.setUniformf("direction", 2.0F, 0.0F);

        GlStateManager.disableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        mc.getFramebuffer().beginRead();
        Shader.draw();

        mc.getFramebuffer().beginWrite(true);

        BLUR.setUniformf("direction", 0.0F, 2.0F);

        outFrameBuffer.beginRead();
        GL30.glActiveTexture(GL30.GL_TEXTURE20);
        inFrameBuffer.beginRead();
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        Shader.draw();

        BLUR.unload();
        inFrameBuffer.endRead();
        GlStateManager.disableBlend();
        renderQueue.clear();
    }

    private static void setupBuffer(Framebuffer frameBuffer) {
        if(frameBuffer.textureWidth != mc.getWindow().getScaledWidth() || frameBuffer.textureHeight != mc.getWindow().getScaledHeight()) {
            frameBuffer.resize(mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), false);
        } else {
            frameBuffer.clear(false);
        }
    }
}
