package com.client.impl.hud;

import com.client.system.hud.HudFunction;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class LeftAngle extends HudFunction {
    public LeftAngle() {
        super(new FloatRect(0, 5, 100, 16), "Left-Angle");
    }

    @Override
    public void draw(float alpha) {
        rect.setX(3f);
        rect.setY(mc.getWindow().getScaledHeight() - IFont.getHeight(IFont.MONTSERRAT_BOLD, "AAA123", 9));

        FontRenderer.color(true);
        IFont.draw(IFont.MONTSERRAT_BOLD, "Coordinates:", rect.getX(), rect.getY(), Color.WHITE, 9);
        FontRenderer.color(false);

        IFont.draw(IFont.MONTSERRAT_BOLD,
                mc.player.getBlockPos().toShortString(),
                rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, "Coordinates: ", 9),
                rect.getY(),
                Color.WHITE,
                7);

        IFont.draw(IFont.MONTSERRAT_BOLD,
                toNetherPos(mc.player.getBlockPos()),
                rect.getX() + IFont.getWidth(IFont.MONTSERRAT_BOLD, "Coordinates: " + mc.player.getBlockPos().toShortString() + " ", 9),
                rect.getY(),
                Color.RED,
                7);
    }

    public String toNetherPos(BlockPos pos) {
        return "(" + new BlockPos(mc.player.getBlockPos().getX() / 8, mc.player.getBlockPos().getY(), mc.player.getBlockPos().getZ() / 8).toShortString() + ")";
    }
}
