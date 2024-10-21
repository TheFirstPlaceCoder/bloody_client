package com.client.impl.function.misc;

import com.client.event.events.Render3DEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.utils.color.ColorUtils;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class AirPlace extends Function {
    public AirPlace() {
        super("Air Place", Category.MISC);
    }

    @Override
    public void tick(TickEvent.Post event) {
        if (!(mc.crosshairTarget instanceof BlockHitResult) || !(mc.player.getMainHandStack().getItem() instanceof BlockItem)) return;

        BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();

        if (mc.options.keyUse.isPressed()) {
            place(new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.DOWN, pos, false));
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!(mc.crosshairTarget instanceof BlockHitResult)
                || !mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).getMaterial().isReplaceable()
                || !(mc.player.getMainHandStack().getItem() instanceof BlockItem)
        ) return;

        Renderer3D.prepare3d(false);

        Renderer3D.drawFilled(((BlockHitResult) mc.crosshairTarget).getBlockPos(), ColorUtils.injectAlpha(Color.LIGHT_GRAY, 95));
        Renderer3D.drawOutline(((BlockHitResult) mc.crosshairTarget).getBlockPos(), Color.LIGHT_GRAY);

        Renderer3D.end3d(false);
    }

    private void place(BlockHitResult blockHitResult) {
        boolean wasSneaking = mc.player.input.sneaking;
        mc.player.input.sneaking = false;

        ActionResult result = mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, blockHitResult);

        if (result.shouldSwingHand()) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }

        mc.player.input.sneaking = wasSneaking;
    }
}