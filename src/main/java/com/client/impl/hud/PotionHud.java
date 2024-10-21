package com.client.impl.hud;

import com.client.impl.function.client.Hud;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.utils.math.animation.Animation;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.misc.InputUtils;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PotionHud extends HudFunction {
    public PotionHud() {
        super(new FloatRect(153, 4, 91, 16), "Potion-Hud");
    }

    public FloatRect potionRect = new FloatRect();
    public Identifier potion = new Identifier("bloody-client", "hud/potion.png");

    @Override
    public void draw(float alpha) {
        potionRect.setX(rect.getX());
        potionRect.setW(rect.getW());
        potionRect.setY(rect.getY() + 18);

        List<StatusEffectInstance> potionList = new ArrayList<>();

        for (StatusEffectInstance statusEffectInstance : mc.player.getStatusEffects()) {
            StatusEffect statusEffect = statusEffectInstance.getEffectType();
            if (statusEffect == StatusEffects.NIGHT_VISION) continue;
            potionList.add(statusEffectInstance);
        }

        potionRect.setH(AnimationUtils.fast(potionRect.getH(), potionList.isEmpty() ? 0 : potionList.size() * 12 + 2));
        rect.setH(16 + potionRect.getH() + 2);

        drawNewClientRect(new FloatRect(rect.getX(), rect.getY(), rect.getW(), 16));

        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(rect.getX() + 2.5f, rect.getY(), 0);
        GL.drawRoundedTexture(potion, 0, 0, 16, 16, 0);
        GL11.glPopMatrix();

        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, "Potions", rect.getCenteredX(), rect.getY() + 8, Color.WHITE, 9);

        if (!potionList.isEmpty()) drawNewClientRect(potionRect);

        ScissorUtils.enableScissor(potionRect);
        float y = potionRect.getY() + 1;
        double w = 0;
        for (StatusEffectInstance potion : potionList) {
            MatrixStack stack = new MatrixStack();
            stack.scale(1f, 1f, 1f);
            stack.translate(potionRect.getX() + 2.5f, y + 1, 0);

            Sprite sprite = mc.getStatusEffectSpriteManager().getSprite(potion.getEffectType());
            mc.getTextureManager().bindTexture(sprite.getAtlas().getId());

            drawSprite(stack, 0, 0, 200, 10, 10, 1f, sprite);
            IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, potion.getEffectType().getName().getString() + (potion.getAmplifier() == 0 ? "" : Formatting.AQUA + " " + potion.getAmplifier()), potionRect.getX() + 16, y + (float) 12 / 2, Color.WHITE, 7);
            String time = getTime(potion.getDuration());
            IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, time, potionRect.getX2() - 4 - IFont.getWidth(IFont.MONTSERRAT_BOLD, time, 7), y + (float) 12 / 2, Color.WHITE, 7);
            double tempW = 14 + IFont.getWidth(IFont.MONTSERRAT_BOLD, potion.getEffectType().getName().getString() + (potion.getAmplifier() == 0 ? "" : Formatting.AQUA + " " + potion.getAmplifier()), 7) + IFont.getWidth(IFont.MONTSERRAT_BOLD, "   " + time, 7);
            if (tempW > w) {
                w = tempW;
            }
            y += 12;
        }
        if (w > 91) {
            w = w + 10;
        } else {
            w = 91;
        }
        rect.setW(AnimationUtils.fast(rect.getW().floatValue(), (float) w));
        ScissorUtils.disableScissor();
    }

    public static void drawSprite(MatrixStack matrices, int x, int y, int z, int width, int height, float a, Sprite sprite) {
        drawTexturedQuad(matrices.peek().getModel(), x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(), a);
    }

    public static void drawTexturedQuad(Matrix4f matrices, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1, float a) {
        RenderSystem.enableAlphaTest();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrices, (float)x0, (float)y1, (float)z).color(1f, 1f, 1f, a).texture(u0, v1).next();
        bufferBuilder.vertex(matrices, (float)x1, (float)y1, (float)z).color(1f, 1f, 1f, a).texture(u1, v1).next();
        bufferBuilder.vertex(matrices, (float)x1, (float)y0, (float)z).color(1f, 1f, 1f, a).texture(u1, v0).next();
        bufferBuilder.vertex(matrices, (float)x0, (float)y0, (float)z).color(1f, 1f, 1f, a).texture(u0, v0).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    public String getTime(int duration) {
        if (duration == -1) {
            return "**:**";
        }

        int dur = duration;
        dur /= 20;

        int min = dur / 60;
        int sec = dur - min * 60;

        return Math.min(99, min) + ":" + (sec < 10 ? "0" + sec : sec);
    }
}
