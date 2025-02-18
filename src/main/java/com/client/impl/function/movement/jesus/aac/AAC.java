package com.client.impl.function.movement.jesus.aac;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.jesus.JesusMode;
import com.client.interfaces.IVec3d;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class AAC extends JesusMode {
    @Override
    public void tick(TickEvent.Pre event) {
        BlockPos blockPos = mc.player.getBlockPos().down();
        if (!mc.player.isOnGround() && mc.world.getBlockState(blockPos).getBlock() == Blocks.WATER || mc.player.isInsideWaterOrBubbleColumn()) {
            ((IVec3d) mc.player.getVelocity()).set(mc.player.getVelocity().x * 0.99999, 0, mc.player.getVelocity().z * 0.99999);
            if (mc.player.horizontalCollision) ((IVec3d) mc.player.getVelocity()).setY(((int) (mc.player.getY() - (int) (mc.player.getY() - 1)) / 8f));
            if (mc.player.fallDistance >= 4) ((IVec3d) mc.player.getVelocity()).setY(-0.004);
            else if (mc.player.isInsideWaterOrBubbleColumn()) ((IVec3d) mc.player.getVelocity()).setY(0.09);
        }
        if (mc.player.hurtTime != 0) mc.player.setOnGround(false);
    }
}
