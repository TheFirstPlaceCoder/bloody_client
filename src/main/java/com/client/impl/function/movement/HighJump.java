package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.util.math.BlockPos;

public class HighJump extends Function {
    public HighJump() {
        super("High Jump", Category.MOVEMENT);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player.isOnGround() &&(mc.world.getBlockState(new BlockPos(mc.player.getPos())).getBlock() instanceof CarpetBlock || mc.world.getBlockState(new BlockPos(mc.player.getPos())).getBlock() instanceof SnowBlock)) {
            mc.player.flyingSpeed = 0.035f;
            mc.player.jump();
            mc.player.setVelocity(mc.player.getVelocity().x, 0.6, mc.player.getVelocity().z);
        }
    }
}
