package com.client.utils.render;

import com.client.interfaces.IInGameHud;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.entity.PlayerUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

import static com.client.BloodyClient.mc;

public class Renderer2D {
    public static MeshBuilder COLOR = new MeshBuilder();
    public static MeshBuilder COLOR_3D = new MeshBuilder();

    public static void drawVignette(float threshold, float power) {
        boolean dif = EntityUtils.getTotalHealth() <= threshold;
        float f = (float) Math.abs((dif ? EntityUtils.getTotalHealth() / threshold : 1f) - 1f) * power;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.color4f(0f, f, f, 1.0f);

        mc.getTextureManager().bindTexture(((IInGameHud) mc.inGameHud).getVignette());
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(0.0D, (double)mc.getWindow().getScaledHeight(), -90.0D).texture(0.0F, 1.0F).next();
        bufferBuilder.vertex((double)mc.getWindow().getScaledWidth(), (double)mc.getWindow().getScaledHeight(), -90.0D).texture(1.0F, 1.0F).next();
        bufferBuilder.vertex((double)mc.getWindow().getScaledWidth(), 0.0D, -90.0D).texture(1.0F, 0.0F).next();
        bufferBuilder.vertex(0.0D, 0.0D, -90.0D).texture(0.0F, 0.0F).next();
        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
    }
}
