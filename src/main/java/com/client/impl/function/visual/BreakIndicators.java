package com.client.impl.function.visual;

import com.client.event.events.Render3DEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ColorSetting;
import com.client.utils.Utils;
import com.client.utils.color.ColorUtils;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import mixin.accessor.InteractionManagerAccessor;
import mixin.accessor.WorldRendererAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

import java.awt.*;
import java.util.Map;

public class BreakIndicators extends Function {
    private final BooleanSetting alpha = Boolean().name("Плавное появление").enName("Smooth Alpha").defaultValue(false).build();
    private final BooleanSetting renderSelf = Boolean().name("Нынешняя позиция").enName("Current Position").defaultValue(false).build();
    public final ColorSetting colorSetting = Color().name("Цвет").enName("Color").defaultValue(Color.CYAN).build();

    public BreakIndicators() {
        super("Break Indicators", Category.VISUAL);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        renderNormal(event);
    }

    private void renderNormal(Render3DEvent event) {
        Map<Integer, BlockBreakingInfo> blocks = ((WorldRendererAccessor) mc.worldRenderer).getBlockBreakingInfos();

        float ownBreakingStage = ((InteractionManagerAccessor) mc.interactionManager).getCurrentBreakingProgress();
        BlockPos ownBreakingPos = ((InteractionManagerAccessor) mc.interactionManager).getCurrentBreakingPos();

        if (renderSelf.get() && ownBreakingPos != null && ownBreakingStage > 0) {
            BlockState state = mc.world.getBlockState(ownBreakingPos);
            VoxelShape shape = state.getOutlineShape(mc.world, ownBreakingPos);
            if (shape == null || shape.isEmpty()) return;

            Box orig = shape.getBoundingBox();

            double shrinkFactor = 1d - ownBreakingStage;

            renderBlock(event, orig, ownBreakingPos, shrinkFactor);
        }

        blocks.values().forEach(info -> {
            BlockPos pos = info.getPos();
            int stage = info.getStage();
            if (pos.equals(ownBreakingPos)) return;

            BlockState state = mc.world.getBlockState(pos);
            VoxelShape shape = state.getOutlineShape(mc.world, pos);
            if (shape == null || shape.isEmpty()) return;

            Box orig = shape.getBoundingBox();

            double shrinkFactor = (9 - (stage + 1)) / 9d;

            renderBlock(event, orig, pos, shrinkFactor);
        });
    }

    private void renderBlock(Render3DEvent event, Box orig, BlockPos pos, double shrinkFactor) {
        Box box = orig.shrink(
                orig.getXLength() * shrinkFactor,
                orig.getYLength() * shrinkFactor,
                orig.getZLength() * shrinkFactor
        );

        double xShrink = (orig.getXLength() * shrinkFactor) / 2;
        double yShrink = (orig.getYLength() * shrinkFactor) / 2;
        double zShrink = (orig.getZLength() * shrinkFactor) / 2;

        double x1 = pos.getX() + box.minX + xShrink;
        double y1 = pos.getY() + box.minY + yShrink;
        double z1 = pos.getZ() + box.minZ + zShrink;
        double x2 = pos.getX() + box.maxX + xShrink;
        double y2 = pos.getY() + box.maxY + yShrink;
        double z2 = pos.getZ() + box.maxZ + zShrink;

        Renderer3D.prepare3d(false);

        Renderer3D.drawFilled(new Box(x1, y1, z1, x2, y2, z2), alpha.get() ? getColor(colorSetting.get(), 1 - shrinkFactor) : colorSetting.get());
        Renderer3D.drawOutline(new Box(x1, y1, z1, x2, y2, z2), alpha.get() ? getColor(ColorUtils.injectAlpha(colorSetting.get(), colorSetting.get().getAlpha() + 50), 1 - shrinkFactor) : ColorUtils.injectAlpha(colorSetting.get(), colorSetting.get().getAlpha() + 50));

        Renderer3D.end3d(false);
    }

    private Color getColor(Color color, double delta) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) Utils.clamp((int) Math.floor(color.getAlpha() * delta), 0, 255));
    }
}