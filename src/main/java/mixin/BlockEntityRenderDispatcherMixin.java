package mixin;

import com.client.event.events.RenderBlockEntityEvent;
import com.client.impl.function.client.Optimization;
import com.client.system.function.FunctionManager;
import com.client.utils.optimization.EntityCullingBase;
import com.client.utils.optimization.interfaces.Cullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BlockEntityRenderDispatcher.class})
public class BlockEntityRenderDispatcherMixin {
    @Inject(
            method = {"render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public <E extends BlockEntity> void render(E blockEntity, float f, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, CallbackInfo info) {
        if (!FunctionManager.get(Optimization.class).isEnabled() || !FunctionManager.get(Optimization.class).rayTrace.get()) return;

        if (!((Cullable)blockEntity).isForcedVisible() && ((Cullable)blockEntity).isCulled()) {
            ++EntityCullingBase.instance.skippedBlockEntities;
            info.cancel();
        } else {
            ++EntityCullingBase.instance.renderedBlockEntities;
        }
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void onRenderEntity(E blockEntity, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, CallbackInfo info) {
        RenderBlockEntityEvent event = RenderBlockEntityEvent.get(blockEntity);
        event.post();
        if (event.isCancelled()) info.cancel();
    }
}
