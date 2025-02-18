package com.client.impl.hud;

import com.client.impl.function.combat.aura.AttackAura;
import com.client.impl.function.misc.NameProtect;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.utils.color.ColorUtils;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.math.MathUtils;
import com.client.utils.math.animation.Animation;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.EaseBackIn;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.PlayerHeadTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TargetHud extends HudFunction {
    public TargetHud() {
        super(new FloatRect(208, 23, 100, 35), "Target-Hud");
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

        drawNewClientRect(rect);

        RenderSystem.color4f(1f, 1f, 1f, a);
        GL.drawRoundedTexture(PlayerHeadTexture.resolve(target.getUuid()), rect.getX().doubleValue() + 2d + pulse, rect.getY().doubleValue() + 2f + pulse, 31f - pulse * 2, 31f - pulse * 2, 6f);
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        GL.drawRoundedRect(rect.getX() + 2 + pulse, rect.getY() + 2f + pulse, 31f - pulse * 2f, 31f - pulse * 2, 4, inject(ColorUtils.injectAlpha(Color.RED, MathHelper.clamp(alpha, 0, 255)), a));

        String name = replaceString(nameProtect.replace(target.getEntityName()), rect.getW() - 40);
        IFont.draw(IFont.COMFORTAAB, name, rect.getX() + 37, rect.getY() + 3, inject(Color.WHITE, a), 9);

        String healthString = "HP: " + Math.round(getWidth(100F, PlayerUtils.getHealth(target)) * 100.0) / 100.0 + "%";
        IFont.draw(IFont.MONTSERRAT_MEDIUM, healthString, rect.getX() + 37, rect.getY() + 3 + IFont.getHeight(IFont.COMFORTAAB, name, 9), inject(Color.WHITE, a), 6);

        healthBarWidth = getWidth(59, PlayerUtils.getHealth(target));
                //AnimationUtils.fast(healthBarWidth, getWidth(59, PlayerUtils.getHealth(target)));
        //ChatUtils.info("width: " + healthBarWidth);
        double y_pos = rect.getY() + 3 + IFont.getHeight(IFont.COMFORTAAB, name, 9) + IFont.getHeight(IFont.MONTSERRAT_MEDIUM, healthString, 6);

        GL.drawRoundedRect(new FloatRect(rect.getX() + 37, y_pos + 2f, 59, 7), 3, ColorUtils.injectAlpha(Color.LIGHT_GRAY, (int) (45 * a)));
        GL.drawRoundedRect(new FloatRect(rect.getX() + 37, y_pos + 2f, (float) Math.max(healthBarWidth, 5D), 7), 3, inject(getBarColor(target), a));

        endScale();
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

    private String replaceString(String in, double w) {
        StringBuilder f = new StringBuilder();
        double t = 0.0d;

        w -= IFont.getWidth(IFont.COMFORTAAB, "...", 9);

        for (char c : in.toCharArray()) {
            String s = "" + c;
            t += IFont.getWidth(IFont.COMFORTAAB, s, 9);
            if (t >= w) {
                f.append("...");
                break;
            }
            f.append(s);
        }

        return f.toString();
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