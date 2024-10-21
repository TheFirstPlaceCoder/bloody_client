package com.client.impl.function.visual;

import com.client.event.events.Render3DEvent;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Tracers extends Function {
    public final MultiBooleanSetting filter = MultiBoolean().name("Рисовать к").defaultValue(List.of(
            new MultiBooleanValue(true, "Игрокам"),
            new MultiBooleanValue(true, "Друзьям"),
            new MultiBooleanValue(false, "Инвизам"),
            new MultiBooleanValue(false, "Предметам")
    )).build();

    public final ColorSetting colorSetting = Color().name("Цвет игроков").defaultValue(Color.CYAN).build();
    public final ColorSetting colorSettingItems = Color().name("Цвет предметов").defaultValue(Color.CYAN).build();

    public Tracers() {
        super("Tracers", Category.VISUAL);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        for (Entity entity : getEntities()) {
            Vec3d entityPos = Renderer3D.getRenderPosition(entity.getPos().add(0, entity.getHeight() / 2, 0));
            Vec3d eyePos = new Vec3d(0, 0, 150).rotateX((float) -(Math.toRadians(mc.cameraEntity.pitch))).rotateY((float) -Math.toRadians(mc.cameraEntity.yaw));

            if (FunctionManager.get(Freecam.class).isEnabled()) {
                eyePos = new Vec3d(0, 0, 150).rotateX((float) -(Math.toRadians(FunctionManager.get(Freecam.class).pitch))).rotateY((float) -Math.toRadians(FunctionManager.get(Freecam.class).yaw));
            }

            Renderer3D.prepare3d(false);
            Renderer3D.drawLine(eyePos, entityPos, getColor(entity), 1.5F);
            Renderer3D.end3d(false);
        }
    }

    private Color getColor(Entity entity) {
        if (entity instanceof ItemEntity) return colorSettingItems.get();
        if (entity instanceof PlayerEntity p && FriendManager.isFriend(p)) return FriendManager.getFriendsColor();
        return colorSetting.get();
    }

    private List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();

        for (Entity player : mc.world.getEntities()) {
            if (!player.isAlive() || player instanceof PlayerEntity || !EntityUtils.isInRenderDistance(player)) continue;
            if (player instanceof ItemEntity && filter.get(4)) {
                entities.add(player);
            }
        }

        entities.addAll(mc.world.getPlayers().stream().filter(player -> {
            if (player.isDead() || !EntityUtils.isInRenderDistance(player) || player == mc.player) return false;
            if (EntityUtils.isBot(player)) return false;
            if (!filter.get(2) && FriendManager.isFriend(player)) return false;
            if (!filter.get(3) && player.isInvisible()) return false;
            return filter.get(0);
        }).toList());

        return entities;
    }
}
