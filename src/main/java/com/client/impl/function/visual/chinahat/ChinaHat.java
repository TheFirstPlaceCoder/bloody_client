package com.client.impl.function.visual.chinahat;

import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.misc.FunctionUtils;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * __aaa__
 * 20.05.2024
 * */
public class ChinaHat extends Function {
    public ChinaHat() {
        super("China Hat", Category.VISUAL);
    }

    public final MultiBooleanSetting filter = MultiBoolean().name("Отображать на").defaultValue(List.of(
            new MultiBooleanValue(true, "Игроков"),
            new MultiBooleanValue(true, "Себя"),
            new MultiBooleanValue(true, "Друзей"),
            new MultiBooleanValue(false, "Инвизов")
    )).build();

    private final IntegerSetting fillOpacity = Integer().name("Нерозрачность").defaultValue(60).min(30).max(100).build();

    public boolean getEntity(Entity entity) {
        if (entity == null || !entity.isAlive()) return false;

        List<Entity> entities = new ArrayList<>();

        entities.addAll(mc.world.getPlayers().stream().filter(player -> {
            if (player == mc.player) return filter.get(1) && mc.options.getPerspective() != Perspective.FIRST_PERSON;
            if (player.isDead() || !EntityUtils.isInRenderDistance(player)) return false;
            if (EntityUtils.isBot(player)) return false;
            if (FriendManager.isFriend(player)) return filter.get(2);
            if (player.isInvisible()) return filter.get(3);
            return filter.get(0);
        }).toList());

        return entities.contains(entity);
    }

    public void drawHat(MatrixStack stack, PlayerEntity entity) {
        Renderer3D.prepare3d(true, false);

        boolean noRenderFlag = !FunctionManager.isEnabled("No Render") || !FunctionUtils.isRemovedArmor;
        double offset = !entity.inventory.getArmorStack(3).isEmpty() && noRenderFlag ? 0.075F : 0;

        stack.push();
        stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
        stack.translate(0, 0.43F + offset, 0);

        Matrix4f matrix4f = stack.peek().getModel();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        bufferBuilder.begin(GL11.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        Color first = Colors.getColor(1);
        bufferBuilder.vertex(matrix4f, 0F, 0 + 0.3F, 0F).color(first.getRed() / 255F, first.getGreen() / 255F, first.getBlue() / 255F, first.getAlpha() / 255F).next();

        Color color;

        for (int i = 0; i <= 360; i++) {
            color = ColorUtils.injectAlpha(Colors.getColor(i), 225);
            bufferBuilder.vertex(matrix4f, (float) (Math.cos(i * Math.PI / 180.0F) * 0.55F), 0F, (float) (Math.sin(i * Math.PI / 180.0F) * 0.55F)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, (fillOpacity.get() * 2.55f) / 255F).next();
        }

        first = Colors.getColor(360);
        bufferBuilder.vertex(matrix4f, 0F, 0 + 0.3F, 0F).color(first.getRed() / 255F, first.getGreen() / 255F, first.getBlue() / 255F, first.getAlpha() / 255F).next();

        Tessellator.getInstance().draw();

        GL11.glLineWidth(2F);
        bufferBuilder.begin(GL11.GL_LINE_LOOP, VertexFormats.POSITION_COLOR);

        for (int i = 0; i <= 360; i++) {
            Color line = ColorUtils.injectAlpha(Colors.getColor(i), 255);
            bufferBuilder.vertex(matrix4f, (float) (Math.cos(i * Math.PI / 180.0F) * 0.55F), 0, (float) (Math.sin(i * Math.PI / 180.0F) * 0.55F)).color(line.getRed() / 255F, line.getGreen() / 255F, line.getBlue() / 255F, line.getAlpha() / 255F).next();
        }

        Tessellator.getInstance().draw();

        Renderer3D.end3d(true);

        stack.pop();
    }
}