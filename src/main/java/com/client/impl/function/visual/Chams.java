package com.client.impl.function.visual;

import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.game.entity.EntityUtils;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Chams extends Function {
    public final MultiBooleanSetting filter = MultiBoolean().name("Применять на").defaultValue(List.of(
            new MultiBooleanValue(true, "Игроков"),
            new MultiBooleanValue(true, "Себя"),
            new MultiBooleanValue(true, "Друзей"),
            new MultiBooleanValue(true, "Инвизов")
    )).build();

    public final ColorSetting colorSetting = Color().name("Цвет игроков").defaultValue(Color.CYAN).build();

    public Chams() {
        super("Chams", Category.VISUAL);
    }

    public boolean shouldDraw(Entity entity) {
        if (!isEnabled() || entity == null || !entity.isAlive()) return false;

        List<Entity> entities = new ArrayList<>();

        entities.addAll(mc.world.getPlayers().stream().filter(player -> {
            if (player.isDead() || !EntityUtils.isInRenderDistance(player)) return false;
            if (EntityUtils.isBot(player)) return false;
            if (!filter.get(2) && FriendManager.isFriend(player)) return false;
            if (!filter.get(3) && player.isInvisible()) return false;
            return filter.get(0) || (player == mc.player && filter.get(1) && mc.options.getPerspective() != Perspective.FIRST_PERSON);
        }).toList());

        return entities.contains(entity);
    }

    public Color getEntityColor(Entity entity) {
        if (FriendManager.isFriend(entity)) return FriendManager.getFriendsColorWithoutAlpha();
        else return colorSetting.get();
    }
}