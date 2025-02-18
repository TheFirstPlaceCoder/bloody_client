package com.client.impl.function.movement.jesus.spartan;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.jesus.JesusMode;
import com.client.interfaces.IVec3d;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;

public class Spartan extends JesusMode {
    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player.isInsideWaterOrBubbleColumn()) {
            if (mc.player.horizontalCollision) {
                mc.player.addVelocity(0, 0.15, 0);
                return;
            }

            Block block = mc.world.getBlockState(mc.player.getBlockPos().up()).getBlock();
            Block blockUp = mc.world.getBlockState(new BlockPos(mc.player.getX(), mc.player.getY() + 1.1, mc.player.getZ())).getBlock();

            if (blockUp instanceof FluidBlock) {
                ((IVec3d) mc.player.getVelocity()).setY(0.1);
            } else if (block instanceof FluidBlock) {
                ((IVec3d) mc.player.getVelocity()).setY(0.0);
            }

            mc.player.setOnGround(true);
            ((IVec3d) mc.player.getVelocity()).set(mc.player.getVelocity().x * 1.085, mc.player.getVelocity().y, mc.player.getVelocity().z * 1.085);
        }
    }
}
