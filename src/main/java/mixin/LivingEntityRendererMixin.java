package mixin;

import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.impl.function.visual.Chams;
import com.client.system.function.FunctionManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

import static com.client.BloodyClient.mc;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    @Shadow @Nullable
    protected abstract RenderLayer getRenderLayer(T entity, boolean showBody, boolean translucent, boolean showOutline);

    protected LivingEntityRendererMixin(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }
    @Shadow protected abstract void scale(T entity, MatrixStack matrices, float amount);

    @Shadow protected abstract boolean isVisible(T entity);

    @ModifyVariable(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 2, at = @At(value = "STORE", ordinal = 0))
    public float changeYaw(float oldValue, LivingEntity entity) {
        if (!RotationHandler.checkIdle() && entity.equals(mc.player)) {
            return RotationHandler.serverYaw;
        }

        return oldValue;
    }

    @ModifyVariable(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 3, at = @At(value = "STORE", ordinal = 0))
    public float changeHeadYaw(float oldValue, LivingEntity entity) {
        if (!RotationHandler.checkIdle() && entity.equals(mc.player)) {
            return RotationHandler.serverYaw;
        }
        return oldValue;
    }

    @ModifyVariable(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 5, at = @At(value = "STORE", ordinal = 3))
    public float changePitch(float oldValue, LivingEntity entity) {
        if (!RotationHandler.checkIdle() && entity.equals(mc.player)) {
            return RotationHandler.serverPitch;
        }
        return oldValue;
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void modifyColor(Args args, T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Chams module = FunctionManager.get(Chams.class);
        if (!module.isEnabled() || !module.shouldDraw(livingEntity)) return;

        Color color = module.getEntityColor(livingEntity);
        args.set(4, color.getRed() / 255f);
        args.set(5, color.getGreen() / 255f);
        args.set(6, color.getBlue() / 255f);
        args.set(7, color.getAlpha() / 255f);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/entity/LivingEntity;ZZZ)Lnet/minecraft/client/render/RenderLayer;"))
    private RenderLayer getRenderLayer(LivingEntityRenderer<T, M> livingEntityRenderer, T livingEntity, boolean showBody, boolean translucent, boolean showOutline) {
        Chams module = FunctionManager.get(Chams.class);
        if (!module.isEnabled() || !module.shouldDraw(livingEntity))
            return getRenderLayer(livingEntity, FunctionManager.isEnabled("Anti Vanish") || !livingEntity.isInvisible(), FunctionManager.isEnabled("Anti Vanish") || !livingEntity.isInvisible(), FunctionManager.isEnabled("Anti Vanish") || !livingEntity.isInvisible());

        return getRenderLayer(livingEntity, ((FunctionManager.isEnabled("Anti Vanish") || module.filter.get(3)) || !livingEntity.isInvisible()), ((FunctionManager.isEnabled("Anti Vanish") || module.filter.get(3)) || !livingEntity.isInvisible()), ((FunctionManager.isEnabled("Anti Vanish") || module.filter.get(3)) || !livingEntity.isInvisible()));
    }
}