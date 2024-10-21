package com.client.utils.classes;

import com.client.event.events.StartBreakingBlockEvent;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.block.Block;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.awt.*;

import static com.client.BloodyClient.mc;

public class MyBlock {
    public BlockPos blockPos;
    public Direction direction;
    public Block originalBlock;
    public int timer;
    public boolean mining;

    public void set(StartBreakingBlockEvent event, int delay) {
        this.blockPos = event.blockPos;
        this.direction = event.direction;
        this.originalBlock = mc.world.getBlockState(blockPos).getBlock();
        this.timer = delay;
        this.mining = false;
    }

    public boolean shouldRemove() {
        boolean remove = mc.world.getBlockState(blockPos).getBlock() != originalBlock || new Vec3d(mc.player.getX() - 0.5, mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ() - 0.5).distanceTo(new Vec3d(blockPos.getX() + direction.getOffsetX(), blockPos.getY() + direction.getOffsetY(), blockPos.getZ() + direction.getOffsetZ())) > mc.interactionManager.getReachDistance();

        if (remove) {
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, blockPos, direction));
            mc.player.swingHand(Hand.MAIN_HAND);
        }

        return remove;
    }

    public void mine() {
        sendMinePackets();
    }

    private void sendMinePackets() {
        if (timer <= 0) {
            if (!mining) {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, direction));

                mining = true;
            } else {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
            }
        }
        else {
            timer--;
        }
    }

    public void render(boolean isStatic, Color color, int alpha) {
        VoxelShape shape = mc.world.getBlockState(blockPos).getOutlineShape(mc.world, blockPos);

        double x1 = blockPos.getX();
        double y1 = blockPos.getY();
        double z1 = blockPos.getZ();
        double x2 = blockPos.getX() + 1;
        double y2 = blockPos.getY() + 1;
        double z2 = blockPos.getZ() + 1;

        if (!shape.isEmpty()) {
            x1 = blockPos.getX() + shape.getMin(Direction.Axis.X);
            y1 = blockPos.getY() + shape.getMin(Direction.Axis.Y);
            z1 = blockPos.getZ() + shape.getMin(Direction.Axis.Z);
            x2 = blockPos.getX() + shape.getMax(Direction.Axis.X);
            y2 = blockPos.getY() + shape.getMax(Direction.Axis.Y);
            z2 = blockPos.getZ() + shape.getMax(Direction.Axis.Z);
        }

        Box box = new Box(x1, y1, z1, x2, y2, z2);

        Renderer3D.prepare3d(false);

        Renderer3D.drawFilled(box, ColorUtils.injectAlpha(isStatic ? color : Colors.getColor(0), alpha));
        Renderer3D.drawOutline(box, ColorUtils.injectAlpha(isStatic ? color : Colors.getColor(0), alpha + 50));

        Renderer3D.end3d(false);
    }
}