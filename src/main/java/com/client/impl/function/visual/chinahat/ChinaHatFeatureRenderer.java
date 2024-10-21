package com.client.impl.function.visual.chinahat;

import com.client.system.friend.FriendManager;
import com.client.system.function.FunctionManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import static com.client.system.function.Function.mc;

public class ChinaHatFeatureRenderer <T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {
    public ChinaHatFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ChinaHat function = FunctionManager.get(ChinaHat.class);

        if (function.isEnabled() && function.getEntity(entity)) {
            matrices.push();
            matrices.scale(1f, 1f, 1f);
            getContextModel().getHead().rotate(matrices);

            function.drawHat(matrices, (PlayerEntity) entity);
            matrices.pop();
        }
    }
}