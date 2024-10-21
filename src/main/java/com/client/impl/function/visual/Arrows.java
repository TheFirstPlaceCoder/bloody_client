package com.client.impl.function.visual;

import com.client.event.events.Render2DEvent;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.Utils;
import com.client.utils.color.Colors;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.client.utils.math.MathUtils.getRotations;

/**
 * __aaa__
 * 22.05.2024
 * */
public class Arrows extends Function {
    public final MultiBooleanSetting filter = MultiBoolean().name("Отображать").defaultValue(List.of(
            new MultiBooleanValue(true, "Игроков"),
            new MultiBooleanValue(true, "Друзей"),
            new MultiBooleanValue(false, "Инвизов"),
            new MultiBooleanValue(false, "Предметы")
    )).build();

    public final ListSetting color = List().name("Режим цвета").list(List.of("Клиентский", "Статичный")).defaultValue("Клиентский").build();
    public final ColorSetting playerColor = Color().name("Цвет игроков").defaultValue(Color.CYAN).visible(() -> color.get().equals("Статичный")).build();
    public final ColorSetting invisColor = Color().name("Цвет инвизов").defaultValue(Color.GREEN).visible(() -> color.get().equals("Статичный")).build();
    public final ColorSetting itemsColor = Color().name("Цвет предметов").defaultValue(Color.RED).visible(() -> color.get().equals("Статичный")).build();

    public final IntegerSetting zazor = Integer().name("Зазор").defaultValue(70).min(0).max(70).build();

    public Arrows() {
        super("Arrows", Category.VISUAL);
    }

    private final Identifier arrow = new Identifier("bloody-client", "/client/arrows.png");
    private float yaw, step, offsetX, offsetY;
    private float size;

    @Override
    public void onRender2D(Render2DEvent event) {
        float size = 30 + zazor.get();

        if (mc.currentScreen instanceof InventoryScreen) {
            size = 150;
        }

        if (MovementUtils.isMoving()) {
            size += 10;
        }

        offsetX = AnimationUtils.fast(offsetX, (mc.player.input.movementSideways) * 3, 5);
        offsetY = AnimationUtils.fast(offsetY, (mc.player.input.movementForward) * 3, 5);

        step = AnimationUtils.fast(step, size);
        yaw = AnimationUtils.fast(yaw, mc.player.renderYaw);

        Utils.rescaling(() -> {
            for (Entity entity : getEntities()) {
                if (entity != mc.player && entity.isAlive()) {
                    drawArrow(entity);
                }
            }
        });

        this.size = size;
    }

    private void drawArrow(Entity entity) {
        float x = (float) mc.getWindow().getWidth() / 4;
        float y = (float) mc.getWindow().getHeight() / 4;

        double look = mc.options.getPerspective().equals(Perspective.THIRD_PERSON_FRONT) ? getRotations(entity) + yaw : getRotations(entity) - yaw;
        double rad = Math.toRadians(look);
        double sin = Math.sin(rad) * (step);
        double cos = Math.cos(rad) * (step);

        GL11.glPushMatrix();
        GL11.glTranslated(x + sin + offsetX, y - cos + offsetY, 0);
        GL11.glScalef(17.5f / 128f, 17.5f / 128f, 17.5f / 128f);
        GL11.glRotated(getRotations(x, y, (float) (x + sin), (float) (y - cos)), 0, 0, 1);

        Color[] colors = new Color[4];

        if (FriendManager.isFriend(entity)) {
            colors[0] = FriendManager.getFriendsColor();
            colors[1] = FriendManager.getFriendsColor();
            colors[2] = FriendManager.getFriendsColor();
            colors[3] = FriendManager.getFriendsColor();
        } else {
            colors[0] = getColor(entity, Colors.getColor((int) (270f * (MathHelper.clamp(((x + sin + offsetX) - (x - sin / 2)) * size, 0F, 1F))), 50));
            colors[1] = getColor(entity, Colors.getColor((int) (270f * (MathHelper.clamp(((x + sin + offsetX) - (x - sin / 2)) * size, 0F, 1F))), 50));
            colors[2] = getColor(entity, Colors.getColor((int) (270f * (MathHelper.clamp(((x + sin + offsetX) - (x - sin / 2)) * size, 0F, 1F))), 50));
            colors[3] = getColor(entity, Colors.getColor((int) (270f * (MathHelper.clamp(((x + sin + offsetX) - (x - sin / 2)) * size, 0F, 1F))), 50));
        }

        TextureGL.create().bind(arrow).draw(new TextureGL.TextureRegion(128, 128), true, colors[0], colors[1], colors[2], colors[3]);
        GL11.glPopMatrix();
    }

    private Color getColor(Entity entity, Color color) {
        if (this.color.get().equals("Клиентский")) return color;

        if (entity instanceof ItemEntity) return itemsColor.get();
        if (entity.isInvisible()) return invisColor.get();
        return playerColor.get();
    }

    private List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof ItemEntity && filter.get(3) && EntityUtils.isInRenderDistance(entity)) entities.add(entity);
        }

        entities.addAll(mc.world.getPlayers().stream().filter(player -> {
            if (player == mc.player) return false;
            if (player.isDead() || !EntityUtils.isInRenderDistance(player)) return false;
            if (EntityUtils.isBot(player)) return false;
            if (!filter.get(1) && FriendManager.isFriend(player)) return false;
            if (!filter.get(2) && player.isInvisible()) return false;
            return filter.get(0);
        }).toList());

        return entities;
    }
}