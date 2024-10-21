package com.client.impl.function.visual.jumpcircle;

import com.client.event.events.PlayerJumpEvent;
import com.client.event.events.Render3DEvent;
import com.client.event.events.TickEvent;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.color.Colors;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.render.Renderer2D;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11C;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JumpCircle extends Function {
    public JumpCircle() {
        super("Jump Circle", Category.VISUAL);
    }

    public final IntegerSetting scaleSpeed = Integer().name("Скорость").defaultValue(3).min(1).max(6).build();
    public final IntegerSetting lifetime = Integer().name("Время").defaultValue(1).min(1).max(10).build();
    public final MultiBooleanSetting filter = MultiBoolean().name("Отображать на").defaultValue(List.of(
            new MultiBooleanValue(true, "Игроков"),
            new MultiBooleanValue(true, "Себя"),
            new MultiBooleanValue(true, "Друзей"),
            new MultiBooleanValue(false, "Инвизов")
    )).build();

    public static List<Circle> circles = new ArrayList<>();
    private long time;

    @Override
    public void onEnable() {
        circles.clear();
    }

    @Override
    public void onJump(PlayerJumpEvent event) {
        if (time > System.currentTimeMillis() || !getEntity(event.entity)) return;
        circles.add(new Circle(new Vec3d(event.entity.getX(), event.entity.getY() + 0.06, event.entity.getZ())));
        time = System.currentTimeMillis() + 50L;
    }

    public boolean getEntity(Entity entity) {
        if (entity == null || !entity.isAlive()) return false;

        List<Entity> entities = new ArrayList<>();

        entities.addAll(mc.world.getPlayers().stream().filter(player -> {
            if (player == mc.player) return filter.get(1);
            if (player.isDead() || !EntityUtils.isInRenderDistance(player)) return false;
            if (EntityUtils.isBot(player)) return false;
            if (FriendManager.isFriend(player)) return filter.get(2);
            if (player.isInvisible()) return filter.get(3);
            return filter.get(0);
        }).toList());

        return entities.contains(entity);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        circles.removeIf(e -> e.update(lifetime.get().longValue()));
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (circles.isEmpty()) return;
        Collections.reverse(circles);
        try {
            for (Circle c : circles) {
                float x = (float) ((float) c.position().x - mc.getEntityRenderDispatcher().camera.getPos().x);
                float y = (float) ((float) c.position().y - mc.getEntityRenderDispatcher().camera.getPos().y);
                float z = (float) ((float) c.position().z - mc.getEntityRenderDispatcher().camera.getPos().z);
                float k = (float) c.timer.getPassedTimeMs() / (float) (lifetime.get() * 1000);
                float start = k * (1.5f + (scaleSpeed.get().floatValue() - 1) / 10);
                float middle = (start + k) / 2;

                /*RenderSystem.enableBlend();
                RenderSystem.blendFunc(770, 771);
                GL11.glEnable(2848);
                GlStateManager._disableDepthTest();
                GL11.glDisable(2884);*/
                Renderer2D.COLOR_3D.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                for (int i = 0; i <= 360; i += 5) {
                    int clr = getColor(i);
                    float v = (float) Math.sin(Math.toRadians(i));
                    float u = (float) Math.cos(Math.toRadians(i));
                    Renderer2D.COLOR_3D.pos((float) x + (u * start), (float) y, (float) z - (v * start)).color(injectAlpha(new Color(clr), 0)).endVertex();
                    Renderer2D.COLOR_3D.pos((float) x + (u * middle), (float) y, (float) z - (v * middle)).color(injectAlpha(new Color(clr), (int) (255 * (1.0F - (float) c.timer.getPassedTimeMs() / (float) (lifetime.get() * 1000))))).endVertex();
                }
                Renderer2D.COLOR_3D.end();

                Renderer2D.COLOR_3D.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                for (int i = 0; i <= 360; i += 5) {
                    int clr = getColor(i);
                    float v = (float) Math.sin(Math.toRadians(i));
                    float u = (float) Math.cos(Math.toRadians(i));

                    Renderer2D.COLOR_3D.pos( (float) x + (u * middle), (float) y, (float) z - (v * middle)).color(injectAlpha(new Color(clr), (int) (255 * (1.0F - (float) c.timer.getPassedTimeMs() / (float) (lifetime.get() * 1000))))).endVertex();
                    Renderer2D.COLOR_3D.pos((float) x + (u * k), (float) y, (float) z - (v * k)).color(injectAlpha(new Color(clr), 0)).endVertex();
                }
                Renderer2D.COLOR_3D.end();

                Renderer2D.COLOR_3D.begin(GL11C.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                for (int i = 0; i <= 360; i += 5) {
                    int clr = getColor(i);
                    float v = (float) Math.sin(Math.toRadians(i));
                    float u = (float) Math.cos(Math.toRadians(i));

                    Renderer2D.COLOR_3D.pos((float) x + (u * middle), (float) y, (float) z - (v * middle)).color(injectAlpha(new Color(clr), (int) (255 * (1.0F - (float) c.timer.getPassedTimeMs() / (float) (lifetime.get() * 1000))))).endVertex();
                    Renderer2D.COLOR_3D.pos((float) x + (u * (middle - 0.04f)), (float) y, (float) z - (v * (middle - 0.04f))).color(injectAlpha(new Color(clr), 0)).endVertex();
                }
                Renderer2D.COLOR_3D.end();
                /*GlStateManager._enableDepthTest();
                GL11.glDisable(2848);
                GL11.glEnable(2884);
                GlStateManager._disableBlend();*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(circles);
    }

    public int getColor(int stage) {
        return TwoColoreffect(Colors.getFirst(), Colors.getSecond(), 5 - 0.64, stage).getRGB();
    }

    public static Color injectAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(alpha, 0, 255));
    }

    public static Color TwoColoreffect(Color cl1, Color cl2, double speed, double count) {
        int angle = (int) (((System.currentTimeMillis()) / speed + count) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return interpolateColorC(cl1, cl2, angle / 360f);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount), interpolateInt(color1.getGreen(), color2.getGreen(), amount), interpolateInt(color1.getBlue(), color2.getBlue(), amount), interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return (int) interpolate(oldValue, newValue, (float) interpolationValue);
    }
}