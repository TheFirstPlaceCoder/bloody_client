package com.client.impl.function.visual.esp;

import com.client.event.events.ESPRenderEvent;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.color.Colors;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.math.vector.Vec3;
import com.client.utils.render.DrawMode;
import com.client.utils.render.Renderer2D;
import com.client.utils.render.TagUtils;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ESP extends Function {
    public ESP() {
        super("ESP", Category.VISUAL);
    }

    public final MultiBooleanSetting filter = MultiBoolean().name("Отображать").defaultValue(List.of(
            new MultiBooleanValue(true, "Игроков"),
            new MultiBooleanValue(true, "Себя"),
            new MultiBooleanValue(true, "Друзей"),
            new MultiBooleanValue(false, "Инвизов"),
            new MultiBooleanValue(false, "Предметы")
    )).build();

    public final DoubleSetting width = Double().name("Ширина линий").defaultValue(1.2).min(1).max(2).build();
    public final ListSetting mode = List().name("Режим").list(List.of("Отключен", "Квадрат", "Углы")).defaultValue("Квадрат").build();
    public final ListSetting color = List().name("Режим цвета").list(List.of("Клиентский", "Статичный")).defaultValue("Клиентский").build();
    public final ColorSetting colorSetting = Color().name("Цвет").defaultValue(Color.CYAN).visible(() -> color.get().equals("Статичный")).build();
    public final ListSetting healthBar = List().name("Режим бара").list(List.of("Отключен", "Клиентский", "Здоровье", "Индивидуальный")).defaultValue("Здоровье").build();
    public final ColorSetting downColor = Color().name("Нижний цвет бара").defaultValue(new Color(255, 0, 0, 255)).visible(() -> healthBar.get().equals("Индивидуальный")).build();
    public final ColorSetting upColor = Color().name("Верхний цвет бара").defaultValue(new Color(0, 255, 13, 255)).visible(() -> healthBar.get().equals("Индивидуальный")).build();

    private final Vec3 pos0 = new Vec3();
    private final Vec3 pos1 = new Vec3();
    private final Vec3 pos2 = new Vec3();

    @Override
    public void onRenderESP(ESPRenderEvent event) {
        Renderer2D.COLOR.begin(DrawMode.Quads, VertexFormats.POSITION_COLOR);

        for (Entity entity : getEntities()) {
            Box box = entity.getBoundingBox();

            double x = MathHelper.lerp(event.tickDelta, entity.lastRenderX, entity.getX()) - entity.getX();
            double y = MathHelper.lerp(event.tickDelta, entity.lastRenderY, entity.getY()) - entity.getY();
            double z = MathHelper.lerp(event.tickDelta, entity.lastRenderZ, entity.getZ()) - entity.getZ();

            pos1.set(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            pos2.set(0, 0, 0);

            if (checkCorner(box.minX + x, box.minY + y, box.minZ + z, pos1, pos2)) continue;
            if (checkCorner(box.maxX + x, box.minY + y, box.minZ + z, pos1, pos2)) continue;
            if (checkCorner(box.minX + x, box.minY + y, box.maxZ + z, pos1, pos2)) continue;
            if (checkCorner(box.maxX + x, box.minY + y, box.maxZ + z, pos1, pos2)) continue;

            if (checkCorner(box.minX + x, box.maxY + y, box.minZ + z, pos1, pos2)) continue;
            if (checkCorner(box.maxX + x, box.maxY + y, box.minZ + z, pos1, pos2)) continue;
            if (checkCorner(box.minX + x, box.maxY + y, box.maxZ + z, pos1, pos2)) continue;
            if (checkCorner(box.maxX + x, box.maxY + y, box.maxZ + z, pos1, pos2)) continue;

            Color color0 = FriendManager.isFriend(entity) ? FriendManager.getFriendsColor() : color.get().equals("Клиентский") ? Colors.getColor(0) : colorSetting.get();
            Color color1 = FriendManager.isFriend(entity) ? FriendManager.getFriendsColor() : color.get().equals("Клиентский") ? Colors.getColor(270) : colorSetting.get();

            if (mode.get().equals("Квадрат")) {
                Renderer2D.COLOR.quadCoords(pos1.x - width.get() * calculateWidth(entity), pos1.y - width.get() * calculateWidth(entity), pos1.x, pos2.y + width.get() * calculateWidth(entity), color0, color0, color1, color1);
                Renderer2D.COLOR.quadCoords(pos2.x, pos1.y - width.get() * calculateWidth(entity), pos2.x + width.get() * calculateWidth(entity), pos2.y + width.get() * calculateWidth(entity), color1, color1, color0, color0);
                Renderer2D.COLOR.quadCoords(pos1.x, pos1.y - width.get() * calculateWidth(entity), pos2.x, pos1.y, color0, color1, color1, color0);
                Renderer2D.COLOR.quadCoords(pos1.x, pos2.y, pos2.x, pos2.y + width.get() * calculateWidth(entity), color1, color0, color0, color1);
            } else if (mode.get().equals("Углы")) {
                renderCorner(pos1.x, pos1.y, (pos2.x - pos1.x) / 3, (pos2.y - pos1.y) / 3, color0, DirectRender.LeftUp);
                renderCorner(pos2.x, pos1.y, (pos2.x - pos1.x) / 3, (pos2.y - pos1.y) / 3, color1, DirectRender.RightUp);
                renderCorner(pos1.x, pos2.y, (pos2.x - pos1.x) / 3, (pos2.y - pos1.y) / 3, color1, DirectRender.LeftDown);
                renderCorner(pos2.x, pos2.y, (pos2.x - pos1.x) / 3, (pos2.y - pos1.y) / 3, color0, DirectRender.RightDown);
            }

            if (!healthBar.get().equals("Отключен") && (entity instanceof LivingEntity)) {
                Renderer2D.COLOR.quadCoords(pos1.x - width.get() * calculateWidth(entity) - calculateOffset(entity) - width.get() * calculateWidth(entity),
                        pos2.y - width.get() * calculateWidth(entity) + (((pos1.y - pos2.y) * (Math.min(PlayerUtils.getHealth((LivingEntity) entity) / ((LivingEntity) entity).getMaxHealth(), 1)))),
                        pos1.x - width.get() * calculateWidth(entity) - calculateOffset(entity), pos2.y + width.get() * calculateWidth(entity),
                        getColor(entity, false), getColor(entity, false), getColor(entity, true), getColor(entity, true)
                );
            }
        }

        Renderer2D.COLOR.end();
    }

    private void renderCorner(double x, double y, double width, double height, Color color, DirectRender directRender) {
        switch (directRender.toString()) {
            case "LeftUp" -> {
                Renderer2D.COLOR.quadCoords(x, y, x + width + this.width.get(), y + this.width.get(), color, color, color, color);
                Renderer2D.COLOR.quadCoords(x, y, x + this.width.get(), y + height + this.width.get(), color, color, color, color);
            }

            case "RightUp" -> {
                Renderer2D.COLOR.quadCoords(x, y, x - width - this.width.get(), y + this.width.get(), color, color, color, color);
                Renderer2D.COLOR.quadCoords(x, y, x - this.width.get(), y + height + this.width.get(), color, color, color, color);
            }

            case "LeftDown" -> {
                Renderer2D.COLOR.quadCoords(x, y, x + width + this.width.get(), y - this.width.get(), color, color, color, color);
                Renderer2D.COLOR.quadCoords(x, y, x + this.width.get(), y - height - this.width.get(), color, color, color, color);
            }

            case "RightDown" -> {
                Renderer2D.COLOR.quadCoords(x, y, x - width - this.width.get(), y - this.width.get(), color, color, color, color);
                Renderer2D.COLOR.quadCoords(x, y, x - this.width.get(), y - height - this.width.get(), color, color, color, color);
            }
        }
    }

    private double calculateWidth(Entity entity) {
        double distance = mc.player.distanceTo(entity);
        if (distance <= 15) {
            return 1;
        } else {
            return Math.max(mode.get().equals("Квадраты") ? 0.3 : 0.05, 1 - (distance * (mode.get().equals("Квадраты") ?  0.005 : 0.01)));
        }
    }

    private double calculateOffset(Entity entity) {
        double distance = mc.player.distanceTo(entity);
        if (distance <= 10) {
            return 7;
        } else {
            return Math.max(1, (7 - 0.15 * distance));
        }
    }

    private boolean checkCorner(double x, double y, double z, Vec3 min, Vec3 max) {
        pos0.set(x, y, z);

        if (!TagUtils.to2D(pos0, 1)) return true;

        if (pos0.x < min.x) min.x = pos0.x;
        if (pos0.y < min.y) min.y = pos0.y;
        if (pos0.z < min.z) min.z = pos0.z;

        if (pos0.x > max.x) max.x = pos0.x;
        if (pos0.y > max.y) max.y = pos0.y;
        if (pos0.z > max.z) max.z = pos0.z;

        return false;
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
            if (player.isDead() || !EntityUtils.isInRenderDistance(player)) return false;
            if (EntityUtils.isBot(player)) return false;
            if (!filter.get(2) && FriendManager.isFriend(player)) return false;
            if (!filter.get(3) && player.isInvisible()) return false;
            return filter.get(0) || (player == mc.player && filter.get(1) && mc.options.getPerspective() != Perspective.FIRST_PERSON);
        }).toList());

        return entities;
    }

    private Color getColor(Entity entity, boolean flag) {
        if (healthBar.get().equals("Здоровье")) {
            return formattingByHealth((LivingEntity) entity);
        } else if (healthBar.get().equals("Клиентский")) {
            return Colors.getColor(flag ? 270 : 0);
        } else if (healthBar.get().equals("Индивидуальный")) {
            return flag ? downColor.get() : upColor.get();
        }
        return null;
    }

    private Color formattingByHealth(LivingEntity livingEntity) {
        double hp = PlayerUtils.getHealth(livingEntity);
        double div = livingEntity.getMaxHealth() / 5;

        double[] state = new double[]{
                div,
                div * 2,
                div * 3,
                div * 4,
        };

        if (hp <= state[0])
            return Color.RED;

        if (hp <= state[1])
            return Color.ORANGE;

        if (hp <= state[2])
            return Color.YELLOW;

        if (hp <= state[3])
            return Color.GREEN.darker();

        return Color.GREEN;
    }
}