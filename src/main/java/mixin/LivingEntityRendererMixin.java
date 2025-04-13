package mixin;

import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import net.minecraft.client.render.RenderLayer;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

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

    @Unique private Function function;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/entity/LivingEntity;ZZZ)Lnet/minecraft/client/render/RenderLayer;"))
    private RenderLayer getRenderLayer(LivingEntityRenderer<T, M> livingEntityRenderer, T livingEntity, boolean showBody, boolean translucent, boolean showOutline) {
        if (function == null) function = FunctionManager.get("Anti Vanish");
        return getRenderLayer(livingEntity, function.isEnabled() || !livingEntity.isInvisible(), function.isEnabled() || !livingEntity.isInvisible(), function.isEnabled() || !livingEntity.isInvisible());
    }
}