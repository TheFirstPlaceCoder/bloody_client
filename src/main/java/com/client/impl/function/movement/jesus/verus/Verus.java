package com.client.impl.function.movement.jesus.verus;

import com.client.event.events.BlockShapeEvent;
import com.client.event.events.PacketEvent;
import com.client.impl.function.movement.jesus.JesusMode;
import mixin.accessor.PlayerMoveC2SPacketAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;

public class Verus extends JesusMode {
    private boolean spoof;

    @Override
    public void onBlockState(BlockShapeEvent event) {
        if (mc.player.input.sneaking || mc.player.fallDistance > 3.0f || mc.player.isOnFire()) {
            return;
        }

        Block block = event.state.getBlock();

        if (block instanceof FluidBlock && !(mc.world.getBlockState(mc.player.getBlockPos()).getBlock() instanceof FluidBlock)) {
            event.shape = VoxelShapes.fullCube();
        }
    }

    @Override
    public void onPacket(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket p) {
            if (!mc.player.input.sneaking &&
                    !mc.player.isTouchingWater() &&
                    standingOnWater() &&
                    !collidesWithAnythingElse()) {

                    ((PlayerMoveC2SPacketAccessor) p).setOnGround(spoof);
                    spoof = !spoof;
            } else spoof = true;
        }
    }

    public boolean standingOnWater() {
        Box boundingBox = mc.player.getBoundingBox();
        Box detectionBox = new Box(boundingBox.minX, boundingBox.minY - 0.01, boundingBox.minZ,
                boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);

        return mc.world.getBlockState(new BlockPos(detectionBox.minX, detectionBox.minY, detectionBox.minZ)).getBlock() instanceof FluidBlock;
    }

    public boolean collidesWithAnythingElse() {
        Box boundingBox = mc.player.getBoundingBox();
        Box detectionBox = new Box(boundingBox.minX, boundingBox.minY - 0.5, boundingBox.minZ,
                boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);

        return mc.world.getBlockCollisions(mc.player, detectionBox)
                .anyMatch(collision -> !(collision == FluidBlock.COLLISION_SHAPE));
    }
}
