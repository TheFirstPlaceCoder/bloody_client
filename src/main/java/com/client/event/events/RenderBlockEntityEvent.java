package com.client.event.events;

import com.client.event.IEvent;
import net.minecraft.block.entity.BlockEntity;

public class RenderBlockEntityEvent extends IEvent {
    private static final RenderBlockEntityEvent INSTANCE = new RenderBlockEntityEvent();

    public BlockEntity blockEntity;

    public static RenderBlockEntityEvent get(BlockEntity blockEntity) {
        INSTANCE.setCancelled(false);
        INSTANCE.blockEntity = blockEntity;
        return INSTANCE;
    }
}