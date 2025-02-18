package com.client.impl.function.movement.jesus.other;

import com.client.event.events.BlockShapeEvent;
import com.client.impl.function.movement.jesus.JesusMode;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.shape.VoxelShapes;

public class EncderChest extends JesusMode {
    @Override
    public void onBlockState(BlockShapeEvent event) {
        if (mc.player.input.sneaking || mc.player.fallDistance > 3.0f || mc.player.isOnFire()) {
            return;
        }

        Block block = event.state.getBlock();

        if (block instanceof FluidBlock) {
            event.state = Blocks.ENDER_CHEST.getDefaultState();
            event.shape = VoxelShapes.fullCube();
        }
    }
}
