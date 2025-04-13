package com.client.impl.function.visual;

import com.client.event.events.Render3DEvent;
import com.client.impl.function.client.Companion;
import com.client.system.companion.DumboOctopusEntity;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.*;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.Utils;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.game.entity.EntityUtils;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Shaders extends Function {
    public Shaders() {
        super("Shaders", Category.VISUAL);
    }

    public final BooleanSetting brightOutline = Boolean().name("Яркая обводка вокруг").enName("Bright Outline").defaultValue(false).build();
    public final BooleanSetting lighting = Boolean().name("Мигание").enName("Lighting").defaultValue(false).build();
    public final IntegerSetting lineWidth = Integer().name("Ширина обводки").enName("Outline Width").defaultValue(5).min(1).max(10).build();
    public final IntegerSetting glowPower = Integer().name("Сила").enName("Glow Power").defaultValue(4).min(1).max(10).build();
    public final DoubleSetting fadeDistance = Double().name("Дистанция убывания").enName("Fade Distance").defaultValue(2.0).min(0).max(12).build();

    public final MultiBooleanSetting filter = MultiBoolean().name("Отображать").enName("Draw at").defaultValue(List.of(
            new MultiBooleanValue(true, "Игроков"),
            new MultiBooleanValue(true, "Себя"),
            new MultiBooleanValue(true, "Друзей"),
            new MultiBooleanValue(false, "Инвизов"),
            new MultiBooleanValue(false, "Предметы")
    )).build();

    public final ColorSetting colorSetting = Color().name("Цвет игроков").enName("Players Color").defaultValue(Color.CYAN).build();
    public final ColorSetting colorSettingItems = Color().name("Цвет предметов").enName("Items Color").defaultValue(new Color(155, 0, 255)).visible(() -> filter.get(4)).build();

    public boolean shouldDraw(Entity entity) {
        return getColore(entity) != null;
    }

    public Color getColore(Entity entity) {
        if (checkEntity(entity)) {
            return ColorUtils.injectAlpha(getColor(entity), lighting.get() ? getAlpha(getAlpha(entity)) : getAlpha(entity));
        }

        return null;
    }

    private int getAlpha(Entity entity) {
        if (entity == null || entity == mc.player || entity instanceof DumboOctopusEntity) return 255;

        double dist = mc.player.distanceTo(entity);
        double fadeDist = fadeDistance.get().floatValue() * fadeDistance.get().floatValue();
        double alpha = 1;

        if (dist <= fadeDist) alpha = (float) (dist / fadeDist);
        if (alpha <= 0.075) alpha = 0;

        return (int) (alpha * 255);
    }

    public int getAlpha(float alphaVal) {
        int period = 3 * 1000;
        long currentTime = System.currentTimeMillis();
        double timeInCycle = (currentTime % period);
        double halfPeriod = period / 2.0;

        double alphaNormalized;

        if (timeInCycle < halfPeriod) {
            alphaNormalized = timeInCycle / halfPeriod;
        } else {
            alphaNormalized = 1.0 - ((timeInCycle - halfPeriod) / halfPeriod);
        }

        return (int) (alphaNormalized * alphaVal);
    }

    public Color getColor(Entity entity) {
        if (entity instanceof ItemEntity) return colorSettingItems.get();
        if (entity instanceof DumboOctopusEntity) return colorSetting.get();

        if (FriendManager.isFriend(entity)) return ((PlayerEntity) entity).hurtTime != 0 ? ColorUtils.injectAlpha(getColorHurt((PlayerEntity) entity, FriendManager.getFriendsColor()), colorSetting.get().getAlpha()) : ColorUtils.injectAlpha(FriendManager.getFriendsColor(), colorSetting.get().getAlpha());

        return ((PlayerEntity) entity).hurtTime != 0 ? ColorUtils.injectAlpha(getColorHurt((PlayerEntity) entity, colorSetting.get()), colorSetting.get().getAlpha()) : colorSetting.get();
    }

    public Color getColorHurt(PlayerEntity player, Color color) {
        hashSet.putIfAbsent(player, new RenderPlayer(player, color));

        return hashSet.get(player).getColor();
    }

    Map<PlayerEntity, RenderPlayer> hashSet = new HashMap<>();

    @Override
    public void onRender3D(Render3DEvent event) {
        hashSet.values().removeIf(e -> e.lerping < 0 || e.entity.hurtTime == 0);
    }

    class RenderPlayer {
        PlayerEntity entity;
        float lerping;
        Color color, colorToReturn;
        boolean aa = false;

        public RenderPlayer(PlayerEntity player, Color color) {
            entity = player;
            lerping = 0;
            this.color = color;
            this.colorToReturn = color;
        }

        public Color getColor() {
            if (aa) lerping -= 0.01;
            else lerping += 0.01;
            if (!aa) aa = lerping >= 1;

            return Utils.lerp(this.color, Color.RED, MathHelper.clamp(lerping, 0, 1));
        }
    }

    private boolean checkEntity(Entity entity) {
        if (!EntityUtils.isInRenderDistance(entity)) return false;

        if (entity instanceof PlayerEntity player) {
            if (EntityUtils.isBot(player)) return false;
            if (FriendManager.isFriend(player) && !filter.get(2)) return false;
            if (player.isInvisible() && !filter.get(3)) return false;

            return filter.get(0) || (player == mc.player && filter.get(1) && mc.options.getPerspective() != Perspective.FIRST_PERSON);
        }

        if (entity instanceof DumboOctopusEntity) return FunctionManager.get(Companion.class).isEnabled() && FunctionManager.get(Companion.class).glow.get();

        return entity instanceof ItemEntity && filter.get(4);
    }
}