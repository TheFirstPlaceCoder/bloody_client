package com.client.impl.function.movement.speedmodes.other;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.util.math.BlockPos;

public class FunTimeSnow extends SpeedMode {
    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player.isOnGround() && (mc.world.getBlockState(new BlockPos(mc.player.getPos())).getBlock() instanceof CarpetBlock || mc.world.getBlockState(new BlockPos(mc.player.getPos())).getBlock() instanceof SnowBlock)) {
            mc.player.setVelocity(mc.player.getVelocity().x * 1.22, 0, mc.player.getVelocity().z * 1.22);
        }
    }
}