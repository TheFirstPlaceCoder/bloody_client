package mixin;

import api.main.EventUtils;
import com.client.event.events.BlockCollisionEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

@Mixin(BlockCollisionSpliterator.class)
public abstract class BlockCollisionSpliteratorMixin {
    @Shadow
    @Nullable
    @Final
    private Box box;
    @Shadow
    @Final
    private ShapeContext context;
    @Shadow
    @Final
    private CuboidBlockIterator blockIterator;
    @Shadow
    @Final
    private BlockPos.Mutable pos;
    @Shadow
    @Final
    private VoxelShape boxShape;
    @Shadow
    @Final
    private CollisionView world;
    @Shadow
    @Final
    private BiPredicate<BlockState, BlockPos> blockPredicate;
    @Shadow
    @Nullable
    protected abstract BlockView getChunk(int x, int z);

    @Shadow @Final @Nullable private Entity entity;

    /**
     * @author
     * @reason
     */
    @Overwrite
    boolean offerBlockShape(Consumer<? super VoxelShape> consumer) {
        while(true) {
            if (this.blockIterator.step()) {
                int i = this.blockIterator.getX();
                int j = this.blockIterator.getY();
                int k = this.blockIterator.getZ();
                int l = this.blockIterator.getEdgeCoordinatesCount();
                if (l == 3) {
                    continue;
                }

                BlockView blockView = this.getChunk(i, k);
                if (blockView == null) {
                    continue;
                }

                this.pos.set(i, j, k);
                BlockState blockState = blockView.getBlockState(this.pos);
                if (!this.blockPredicate.test(blockState, this.pos) || l == 1 && !blockState.exceedsCube() || l == 2 && !blockState.isOf(Blocks.MOVING_PISTON)) {
                    continue;
                }

                VoxelShape voxelShape = blockState.getCollisionShape(this.world, this.pos, this.context);

                if (this.entity instanceof PlayerEntity) {
                    BlockCollisionEvent var11 = new BlockCollisionEvent(this.pos, voxelShape);
                    EventUtils.post(var11);
                    voxelShape = var11.getVoxelShape();
                    if (var11.isCancelled()) {
                        return false;
                    }
                }

                if (voxelShape == VoxelShapes.fullCube()) {
                    if (!this.box.intersects((double)i, (double)j, (double)k, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D)) {
                        continue;
                    }

                    consumer.accept(voxelShape.offset((double)i, (double)j, (double)k));
                    return true;
                }

                VoxelShape voxelShape2 = voxelShape.offset((double)i, (double)j, (double)k);
                if (!VoxelShapes.matchesAnywhere(voxelShape2, this.boxShape, BooleanBiFunction.AND)) {
                    continue;
                }

                consumer.accept(voxelShape2);
                return true;
            }

            return false;
        }
    }
}
