package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class BlockCollisionEvent extends IEvent {
    private BlockPos blockPos;
    private VoxelShape voxelShape;

    public BlockCollisionEvent(BlockPos var1, VoxelShape var2) {
        this.blockPos = var1;
        this.voxelShape = var2;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public VoxelShape getVoxelShape() {
        return this.voxelShape;
    }

    public void method13904(BlockPos var1) {
        this.blockPos = var1;
    }

    public void setBoxelShape(VoxelShape var1) {
        if (var1 == null) {
            var1 = VoxelShapes.UNBOUNDED;
        }

        this.voxelShape = var1;
    }
}
