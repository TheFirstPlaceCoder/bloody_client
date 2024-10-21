package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public class BlockShapeEvent extends IEvent {
    public BlockState state;
    public BlockPos pos;
    public VoxelShape shape;

    public BlockShapeEvent(BlockState state, BlockPos pos, VoxelShape shape) {
        this.state = state;
        this.pos = pos;
        this.shape = shape;
    }
}