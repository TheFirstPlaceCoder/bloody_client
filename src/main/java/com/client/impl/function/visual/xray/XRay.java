package com.client.impl.function.visual.xray;

import api.interfaces.EventHandler;
import com.client.event.events.AmbientOcclusionEvent;
import com.client.event.events.ChunkOcclusionEvent;
import com.client.event.events.RenderBlockEntityEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.List;

public class XRay extends Function {
    private final MultiBooleanSetting targetBlocks = MultiBoolean().name("Блоки").defaultValue(List.of(
            new MultiBooleanValue(false, "Уголь"),
            new MultiBooleanValue(true, "Железо"),
            new MultiBooleanValue(true, "Золото"),
            new MultiBooleanValue(false, "Редстоун"),
            new MultiBooleanValue(false, "Лазурит"),
            new MultiBooleanValue(true, "Алмазы"),
            new MultiBooleanValue(false, "Изумруд"),
            new MultiBooleanValue(false, "Кварц"),
            new MultiBooleanValue(true, "Древние обломки")
    )).build();

    public XRay() {
        super("X-RAY", Category.VISUAL);
    }

    @Override
    public void onEnable() {
        mc.worldRenderer.reload();
    }

    @Override
    public void onDisable() {
        mc.worldRenderer.reload();
    }

    @EventHandler
    private void onRenderBlockEntity(RenderBlockEntityEvent event) {
        if (!isCkecked(event.blockEntity.getCachedState().getBlock())) event.cancel();
    }

    @EventHandler
    private void onChunkOcclusion(ChunkOcclusionEvent event) {
        event.cancel();
    }

    @EventHandler
    private void onAmbientOcclusion(AmbientOcclusionEvent event) {
        event.lightLevel = 1;
    }

    public boolean modifyDrawSide(BlockState state, BlockView view, BlockPos pos, Direction facing, boolean returns) {
        if (returns) {
            if (!isCkecked(state.getBlock())) return false;
        }
        else {
            if (isCkecked(state.getBlock())) {
                BlockPos adjPos = pos.offset(facing);
                BlockState adjState = view.getBlockState(adjPos);

                return adjState.getCullingFace(view, adjPos, facing.getOpposite()) != VoxelShapes.fullCube() || adjState.getBlock() != state.getBlock();
            }
        }

        return returns;
    }

    public boolean isCkecked(Block block) {
        return ((block == Blocks.COAL_ORE || block == Blocks.COAL_BLOCK) && targetBlocks.get(0))
                || ((block == Blocks.IRON_ORE || block == Blocks.IRON_BLOCK) && targetBlocks.get(1))
                || ((block == Blocks.GOLD_ORE || block == Blocks.GOLD_BLOCK || block == Blocks.NETHER_GOLD_ORE) && targetBlocks.get(2))
                || ((block == Blocks.REDSTONE_ORE || block == Blocks.REDSTONE_BLOCK) && targetBlocks.get(3))
                || ((block == Blocks.LAPIS_ORE || block == Blocks.LAPIS_BLOCK) && targetBlocks.get(4))
                || ((block == Blocks.DIAMOND_ORE || block == Blocks.DIAMOND_BLOCK) && targetBlocks.get(5))
                || ((block == Blocks.EMERALD_ORE || block == Blocks.EMERALD_BLOCK) && targetBlocks.get(6))
                || (block == Blocks.NETHER_QUARTZ_ORE && targetBlocks.get(7))
                || ((block == Blocks.ANCIENT_DEBRIS || block == Blocks.NETHERITE_BLOCK) && targetBlocks.get(8));
    }
}