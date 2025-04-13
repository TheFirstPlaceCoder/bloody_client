package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.util.math.BlockPos;

public class TeleportBack extends Function {
    public TeleportBack() {
        super("Teleport Back", Category.MOVEMENT);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player.isOnGround() && (mc.world.getBlockState(new BlockPos(mc.player.getPos())).getBlock() instanceof CarpetBlock || mc.world.getBlockState(new BlockPos(mc.player.getPos())).getBlock() instanceof SnowBlock)) {
            mc.player.jump();
            mc.player.setVelocity(mc.player.getVelocity().x * 1.22, 0, mc.player.getVelocity().z * 1.22);
        }
    }
}
