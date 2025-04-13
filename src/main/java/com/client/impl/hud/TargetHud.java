package com.client.impl.hud;

import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.misc.NameProtect;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.math.MathUtils;
import com.client.utils.math.animation.Animation;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.EaseBackIn;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.PlayerHeadTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class TargetHud extends HudFunction {
    public TargetHud() {
        super(new FloatRect(208, 23, 100, 30), "Target-Hud");
    }

    private final List<TargetHudParticle> particles = new ArrayList<>();
    private final Animation animation = new EaseBackIn(400, 1, 1);

    private PlayerEntity target;
    private boolean has;

    private float pulse;
    private int alpha;

    private double healthBarWidth = 0, sc;
    private NameProtect nameProtect;
    private com.client.impl.function.hud.TargetHud targetHud;
    private AttackAura attackAura;

    @Override
    public void onEnable() {
        healthBarWidth = 0;
        pulse = 0;
        alpha = 0;
    }

    @Override
    public void tick() {
        if (nameProtect == null) nameProtect = FunctionManager.get(NameProtect.class);
        if (targetHud == null) targetHud = FunctionManager.get(com.client.impl.function.hud.TargetHud.class);

        updateTarget();

        animation.setDirection(!has ? Direction.BACKWARDS : Direction.FORWARDS);

        sc = animation.getOutput();

        if (sc <= 0.0f) {
            target = null;
        }

        if (target == null) {
            particles.clear();
            pulse = 0;
            alpha = 0;
            return;
        }

        if (alpha > 0) {
            alpha -= 6;
        }

        if (pulse > 0) {
            pulse -= 0.1f;
            pulse = MathHelper.clamp(pulse, 0, 2);
        }

        if (target.hurtTime > 0) {
            if (alpha < 100) {
                alpha += 10;
            }

            if (pulse <= 0.4f) {
                pulse += 0.3f;
            }

            if (targetHud.particles.get() && target.age % 4 == 0) {
                for (int i = 0; i < MathUtils.random(2, 3); i++) {
                    particles.add(new TargetHudParticle(rect));
                }
            }
        }

        particles.removeIf(TargetHudParticle::removeIf);

        while (particles.size() > 100) {
            particles.remove(particles.get(0));
        }
    }

    @Override
    public void draw(float a) {
        if (nameProtect == null) nameProtect = FunctionManager.get(NameProtect.class);
        if (targetHud == null) targetHud = FunctionManager.get(com.client.impl.function.hud.TargetHud.class);

        if (target == null) return;

        startScale(sc);

        for (TargetHudParticle particle : particles) {
            particle.draw();
        }

        List<ItemStack> stacks = getArmor();

        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.8f, 0.8f, 1f);

        float scale = 0.8f;
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            double x = i * 12.8;
            mc.getItemRenderer().renderInGui(stack, (int)((rect.getX2().intValue() - 3 - 12.8 - x) / scale), (int)((rect.getY().intValue() - 13) / scale));
            mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, stack, (int)((rect.getX2().intValue() - 3 - 12.8 - x) / scale), (int)((rect.getY().intValue() - 13) / scale));
        }

        RenderSystem.popMatrix();


        drawNewClientRect(rect);

        RenderSystem.color4f(1f, 1f, 1f, a);
        GL.drawRoundedTexture(PlayerHeadTexture.resolve(target.getUuid()), rect.getX().doubleValue() + 3 + pulse, rect.getY().doubleValue() + 3 + pulse, 24f - pulse * 2, 24f - pulse * 2, 6f);
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        GL.drawRoundedRect(rect.getX() + 3 + pulse, rect.getY() + 3 + pulse, 24f - pulse * 2f, 24f - pulse * 2, 4, inject(ColorUtils.injectAlpha(Color.RED, MathHelper.clamp(alpha, 0, 255)), a));

        String name = nameProtect.replace(target.getEntityName());

        IFont.draw(IFont.COMFORTAAB, "Name:", rect.getX() + 31, rect.getY() + 6, inject(Color.WHITE, a), 7);

        FontRenderer.color(true);
        IFont.draw(IFont.MONTSERRAT_BOLD, name, rect.getX() + 31 + IFont.getWidth(IFont.COMFORTAAB, "Name: ", 7), rect.getY() + 6, inject(Color.WHITE, a), 7);
        FontRenderer.color(false);

        healthBarWidth =
                //AnimationUtils.fast(healthBarWidth,
                getWidth(this.rect.getW() - 40 - IFont.getWidth(IFont.MONTSERRAT_BOLD, "99.9", 7), PlayerUtils.getHealth(target));
        //);
        //AnimationUtils.fast(healthBarWidth, getWidth(59, PlayerUtils.getHealth(target)));
        //ChatUtils.info("width: " + healthBarWidth);
        double y_pos = rect.getY() + 8 + IFont.getHeight(IFont.COMFORTAAB, name, 7);

        if (targetHud.barMode.get().equals("Клиентский")) {
            if (!targetHud.blur.get()) {
                GL.drawRoundedRect(new FloatRect(rect.getX() + 31, y_pos + 2f, this.rect.getW() - 40 - IFont.getWidth(IFont.MONTSERRAT_BOLD, "99.9", 7), 4), 1.5, ColorUtils.injectAlpha(Color.LIGHT_GRAY, (int) (45 * a)));
                GL.drawRoundedGradientRect(new FloatRect(rect.getX() + 31, y_pos + 2f, (float) Math.max(healthBarWidth, 5D), 4), 1.5, ColorUtils.injectAlpha(Colors.getColor(0), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (255)));
            } else GL.drawRoundedGlowRect(new FloatRect(rect.getX() + 31, y_pos + 2f, (float) Math.max(healthBarWidth, 5D), 4), 1.5,0, ColorUtils.injectAlpha(Colors.getColor(0), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (255)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (255)));
        }
        else {
            if (!targetHud.blur.get()) {
                GL.drawRoundedRect(new FloatRect(rect.getX() + 31, y_pos + 2f, this.rect.getW() - 40 - IFont.getWidth(IFont.MONTSERRAT_BOLD, "99.9", 7), 4), 1.5, ColorUtils.injectAlpha(Color.LIGHT_GRAY, (int) (45 * a)));
                GL.drawRoundedRect(new FloatRect(rect.getX() + 31, y_pos + 2f, (float) Math.max(healthBarWidth, 5D), 4), 1.5, inject(getBarColor(target), a));
            } else GL.drawRoundedGlowRect(new FloatRect(rect.getX() + 31, y_pos + 2f, (float) Math.max(healthBarWidth, 5D), 4), 1.5, 0, inject(getBarColor(target), a));
        }

        FontRenderer.color(true);
        IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, (PlayerUtils.getHealth(target) > 40 ? "20.0" : String.format("%.1f",  PlayerUtils.getHealth(target))).replace(",", "."), rect.getX() + 31 + (this.rect.getW() - 40 - IFont.getWidth(IFont.MONTSERRAT_BOLD, "99.9", 6)) + 1, (float) (y_pos + 4), inject(Color.WHITE, a), 7);
        FontRenderer.color(false);

        float width = 3 + 24 + 6 + IFont.getWidth(IFont.COMFORTAAB, "Name: ", 7) + IFont.getWidth(IFont.MONTSERRAT_BOLD, name, 7) + 3;
        this.rect.setW(AnimationUtils.fast(this.rect.getW(), width));

        endScale();
    }

    private List<ItemStack> getArmor() {
        List<ItemStack> itemStacks = new ArrayList<>();
        boolean empty = true;
        for (int i = 0; i < 4; i++) {
            ItemStack stack = target.inventory.getArmorStack(i);
            itemStacks.add(stack);
            if (!stack.isEmpty()) {
                empty = false;
            }
        }
        if (mc.currentScreen instanceof ChatScreen && empty) {
            itemStacks.clear();
            itemStacks.add(Items.NETHERITE_BOOTS.getDefaultStack());
            itemStacks.add(Items.NETHERITE_LEGGINGS.getDefaultStack());
            itemStacks.add(Items.NETHERITE_CHESTPLATE.getDefaultStack());
            itemStacks.add(Items.NETHERITE_HELMET.getDefaultStack());
        }
        return itemStacks;
    }

    public Color getBarColor(PlayerEntity target) {
        Color color1 = Color.GREEN;
        Color color2 = Color.RED;
        double progress = MathHelper.clamp(PlayerUtils.getHealth(target) / (PlayerUtils.getMaxHealth(target) - 2) * 1.0, 0, 1);
        int red = (int) Math.abs(progress * (double) (color1.getRed()) + (1.0 - progress) * (double) (color2.getRed()));
        int green = (int) Math.abs(progress * (double) (color1.getGreen()) + (1.0 - progress) * (double) (color2.getGreen()));
        int blue = (int) Math.abs(progress * (double) (color1.getBlue()) + (1.0 - progress) * (double) (color2.getBlue()));
        return new Color(MathHelper.clamp(red, 0, 255), MathHelper.clamp(green, 0, 255), MathHelper.clamp(blue, 0, 255), 255);
    }

    private float getWidth(float max, float health) {
        return max * (Math.min(health, 20) / 20);
    }

    private void updateTarget() {
        if (attackAura == null) attackAura = FunctionManager.get(AttackAura.class);
        Entity auraTarget = attackAura.target;

        if (auraTarget instanceof PlayerEntity && PlayerUtils.isInRange(auraTarget, attackAura.gerRadius())) {
            target = (PlayerEntity) auraTarget;
            has = true;
            return;
        }

        if (mc.currentScreen instanceof ChatScreen) {
            target = mc.player;
            has = true;
            return;
        }

        has = false;
    }
}