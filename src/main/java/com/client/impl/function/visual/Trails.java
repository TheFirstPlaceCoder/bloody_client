package com.client.impl.function.visual;

import com.client.event.events.Render3DEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.Utils;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * __aaa__
 * 20.05.2024
 */
public class Trails extends Function {
    public Trails() {
        super("Trails", Category.VISUAL);
    }

    private final ListSetting mode = List().name("Режим").defaultValue("Новый").list(List.of("Старый", "Новый")).build();
    public final DoubleSetting time = Double().name("Время жизни").defaultValue(3.4).min(0).max(10.0).build();
    public final DoubleSetting size = Double().name("Размер").defaultValue(3.0).min(0).max(5).visible(() -> mode.get().equals("Новый")).build();

    private final List<Pair<Long, Vec3d>> trail = new ArrayList<>();
    private Identifier identifier = new Identifier("bloody-client", "client/glow_circle.png");

    @Override
    public void onEnable() {
        trail.clear();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        trail.removeIf(t -> System.currentTimeMillis() > t.getLeft() + 100L);
        if (mc.player.prevX - mc.player.getX() != 0 || mc.player.prevY - mc.player.getY() != 0 || mc.player.prevZ - mc.player.getZ() != 0)
            trail.add(new Pair<>(System.currentTimeMillis() + (long) (time.get() * 100), Renderer3D.getSmoothPos(mc.player)));
        if (mode.get().equals("Старый"))
            draw(mc.player, trail);
        else drawNew(event.getMatrices(), mc.player, trail);
    }

    public void drawNew(MatrixStack matrix, PlayerEntity entity, List<Pair<Long, Vec3d>> posList) {
        if (entity == mc.player && mc.options.getPerspective().equals(Perspective.FIRST_PERSON)) return;

        for (Pair<Long, Vec3d> pos : posList) {
            Vec3d vec = Renderer3D.getRenderPosition(pos.getRight());

            matrix.push();
            matrix.translate(vec.x, vec.y + entity.getHeight() * 0.3, vec.z);
            matrix.scale(size.get().floatValue() / 10, size.get().floatValue() / 10, size.get().floatValue() / 10);
            matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-mc.gameRenderer.getCamera().getYaw()));
            matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(mc.gameRenderer.getCamera().getPitch()));

            TextureGL.create()
                    .bind(identifier)
                    .draw(
                            matrix, new TextureGL.TextureRegion(getScaleByIndex(posList, posList.indexOf(pos)), getScaleByIndex(posList, posList.indexOf(pos))), true,
                            ColorUtils.injectAlpha(Colors.getColor(posList.indexOf(pos) * 4), alpha(pos.getLeft()))
                    );
            matrix.pop();
        }
    }

    public float getScaleByIndex(List<?> list, int index) {
        // Нормализуем индекс от 0 до 1
        double t = (double) index / (list.size() - 1);

        // Используем lerp для получения значения
        return (float) Utils.lerp(2, 6, t);
    }

    public void draw(PlayerEntity entity, List<Pair<Long, Vec3d>> posList) {
        if (entity == mc.player && mc.options.getPerspective().equals(Perspective.FIRST_PERSON)) return;

        Renderer3D.prepare3d(true);
        GL11.glDepthMask(false);

        Renderer3D.enableSmoothLine(2.5F);

        Renderer3D.begin(GL11.GL_QUAD_STRIP);
        for (Pair<Long, Vec3d> pos : posList) {
            Vec3d vec = Renderer3D.getRenderPosition(pos.getRight());
            color(posList, pos);
            GL11.glVertex3d(vec.x, vec.y + 0.200F, vec.z);
            GL11.glVertex3d(vec.x, vec.y + entity.getHeight() - 0.100F, vec.z);
        }
        Renderer3D.end();

        drawLine(entity, posList, false);
        drawLine(entity, posList, true);

        Renderer3D.disableSmoothLine();
        Renderer3D.end3d(true);
    }

    private void drawLine(PlayerEntity entity, List<Pair<Long, Vec3d>> posList, boolean f) {
        Renderer3D.begin(GL11.GL_LINE_STRIP);
        for (Pair<Long, Vec3d> pos : posList) {
            Vec3d vec = Renderer3D.getRenderPosition(pos.getRight());
            color(posList, pos);
            GL11.glVertex3d(vec.x, vec.y + (f ? 0.2F : entity.getHeight() - 0.1F), vec.z);
        }
        Renderer3D.end();
    }

    private void color(List<Pair<Long, Vec3d>> posList, Pair<Long, Vec3d> pos) {
        Renderer3D.color(ColorUtils.injectAlpha(Colors.getColor(posList.indexOf(pos) * 4), alpha(pos.getLeft())));
    }

    private int alpha(long time) {
        return (int) MathHelper.clamp(150 * ((time - System.currentTimeMillis()) / 340d), 0, 150);
    }
}