package com.client.impl.function.misc;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import mixin.accessor.InteractionManagerAccessor;
import net.minecraft.util.math.BlockPos;

public class SpeedMine extends Function {
    public SpeedMine() {
        super("Speed Mine", Category.MISC);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        InteractionManagerAccessor im = (InteractionManagerAccessor) mc.interactionManager;
        float progress = im.getCurrentBreakingProgress();
        BlockPos pos = im.getCurrentBreakingPos();

        if (pos == null || progress <= 0) return;
        if (progress + mc.world.getBlockState(pos).calcBlockBreakingDelta(mc.player, mc.world, pos) >= 0.7f)
            im.setCurrentBreakingProgress(1f);
    }
}
