package com.client.impl.function.movement.jesus.aac;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.jesus.JesusMode;
import com.client.interfaces.IVec3d;
import net.minecraft.block.Blocks;

public class AACNew extends JesusMode {
    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player.isInsideWaterOrBubbleColumn()) {
            ((IVec3d) mc.player.getVelocity()).set(mc.player.getVelocity().x * 1.17, mc.player.getVelocity().y, mc.player.getVelocity().z * 1.17);

            if (mc.player.horizontalCollision)
                ((IVec3d) mc.player.getVelocity()).setY(0.24);
            else if (mc.world.getBlockState(mc.player.getBlockPos().up()).getBlock() != Blocks.AIR)
                mc.player.addVelocity(0, 0.04, 0);
        }
        if (mc.player.hurtTime != 0) mc.player.setOnGround(false);
    }
}
