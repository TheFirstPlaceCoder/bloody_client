package mixin;

import api.main.EventUtils;
import com.client.event.events.AmbientOcclusionEvent;
import com.client.event.events.BlockShapeEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "getAmbientOcclusionLightLevel", at = @At("HEAD"), cancellable = true)
    private void onGetAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> info) {
        AmbientOcclusionEvent event = AmbientOcclusionEvent.get();
        EventUtils.post(event);
        if (event.lightLevel != -1) info.setReturnValue(event.lightLevel);
    }

    @ModifyReturnValue(method = "getCollisionShape", at = @At("RETURN"))
    private VoxelShape hookCollisionShape(VoxelShape original, BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (pos == null) {
            return original;
        }

        BlockShapeEvent shapeEvent = new BlockShapeEvent(state, pos, original);
        shapeEvent.post();

        return shapeEvent.shape;
    }
}