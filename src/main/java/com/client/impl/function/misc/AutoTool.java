package com.client.impl.function.misc;

import com.client.event.events.StartBreakingBlockEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoTool extends Function {
    public AutoTool() {
        super("Auto Tool", Category.MISC);
    }

    private final BooleanSetting silent = Boolean().name("Сайлент свап").defaultValue(false).build();

    private boolean mine;

    @Override
    public void onDisable() {
        if (silent.get()) {
            mine = false;
        }
    }

    @Override
    public void tick(TickEvent.Post event) {
        if (silent.get() && mc.options.keyAttack.isPressed() && mc.crosshairTarget.getPos() != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos bp = new BlockPos(((BlockHitResult) mc.crosshairTarget).getBlockPos());
            Direction direction = ((BlockHitResult) mc.crosshairTarget).getSide();

            if (mc.world.getBlockState(bp).getHardness(mc.world, bp) <= 0.0) {
                mine = true;
            } else if (!mc.player.isCreative()) {
                FindItemResult tool = InvUtils.findFastestTool(mc.world.getBlockState(bp));

                if (!tool.found()) {
                    mine = true;
                } else if (mc.player.inventory.selectedSlot != tool.slot()) {
                    mine = false;

                    InvUtils.swap(tool.slot());
                    mc.interactionManager.updateBlockBreakingProgress(bp, direction);
                    InvUtils.swapBack();
                }
            }
        }
    }

    @Override
    public void onBreakBlock(StartBreakingBlockEvent event) {
        BlockPos pos = event.blockPos;
        FindItemResult item = InvUtils.findFastestTool(mc.world.getBlockState(pos));
        if (!silent.get()) {
            InvUtils.swap(item.slot());
        } else {
            if (mc.crosshairTarget instanceof BlockHitResult hitResult) {
                BlockPos bp = new BlockPos(hitResult.getBlockPos());
                FindItemResult tool = InvUtils.findFastestTool(mc.world.getBlockState(bp));

                if (!tool.found()) return;
                if (mc.player.inventory.selectedSlot != tool.slot() && !mine && !mc.player.isCreative()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}