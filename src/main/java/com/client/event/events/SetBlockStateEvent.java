package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class SetBlockStateEvent extends IEvent {
    public final BlockPos pos;
    public final BlockState state;
    public final BlockState prevState;

    public SetBlockStateEvent(BlockPos pos, BlockState state, BlockState prevState) {
        this.pos = pos;
        this.state = state;
        this.prevState = prevState;
    }
}