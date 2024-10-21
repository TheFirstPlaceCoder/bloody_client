package com.client.utils.optimization.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;

public interface EntityRendererInter<T extends Entity> {
    boolean shadowShouldShowName(T var1);

    void shadowRenderNameTag(T var1, Text var2, MatrixStack var3, VertexConsumerProvider var4, int var5);
}