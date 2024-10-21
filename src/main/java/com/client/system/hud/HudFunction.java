package com.client.system.hud;

import api.main.EventUtils;
import com.client.BloodyClient;
import com.client.event.events.ToggleEvent;
import com.client.impl.function.client.ClickGui;
import com.client.impl.function.client.Hud;
import com.client.system.friend.FriendManager;
import com.client.system.function.FunctionManager;
import com.client.system.hud.setting.HudValue;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.SmoothStepAnimation;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.math.vector.floats.V2F;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.shader.shaders.BlurShader;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class HudFunction {
    public static MinecraftClient mc = BloodyClient.mc;

    public List<HudValue> values = new ArrayList<>();
    private final HudSettingPanel panel = new HudSettingPanel();

    public boolean draggable = true;
    public FloatRect rect;
    public final FloatRect defaultRect;
    private final String name;

    private boolean dragged;

    private final List<V2F> PATH = new ArrayList<>();
    private int pid;
    private long uptime;

    private boolean toggled = false, draw;

    private final SmoothStepAnimation panel_anim = new SmoothStepAnimation(300, 1);
    public final SmoothStepAnimation alpha_anim = new SmoothStepAnimation(300, 1);

    public HudFunction(FloatRect rect, String name) {
        this.rect = rect;
        this.defaultRect = rect;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void toggle() {
        toggled = !toggled;

        if (toggled) on();
        else off();
    }

    public void setEnabled(boolean bo) {
        this.toggled = bo;

        if (toggled) on();
        else off();
    }

    public boolean isEnabled() {
        return toggled;
    }

    private void on() {
        EventUtils.register(this);

        if (!canUpdate()) return;
        onEnable();
    }

    public void onEnable() {
    }

    private void off() {
        EventUtils.unregister(this);

        if (!canUpdate()) return;
        onDisable();
    }

    public void onDisable() {
    }

    public abstract void draw(float alpha);

    public void onToggle(ToggleEvent event) {
    }

    public void handle(int mouseX, int mouseY) {
        //FloatRect rect = new FloatRect(this.rect.getX() * 2 / mc.options.guiScale, this.rect.getY() * 2 / mc.options.guiScale, this.rect.getW() * 2 / mc.options.guiScale, this.rect.getH() * 2 / mc.options.guiScale);
        if (dragged) {
            if (!PATH.isEmpty()) {
                try {
                    V2F vec = PATH.get(pid);
                    rect.setX(Math.round(AnimationUtils.fast(rect.getX(), vec.a + mouseX) * 100.0f) / 100.0f);
                    rect.setY(Math.round(AnimationUtils.fast(rect.getY(), vec.b + mouseY) * 100.0f) / 100.0f);
                    if (rect.intersect(vec.a, vec.b)) {
                        PATH.remove(pid);
                        pid++;
                    }
                } catch (Exception ignore) {
                }
            } else {
                pid = 0;
            }
        } else {
            rect.setX((float) rect.getX().intValue());
            rect.setY((float) rect.getY().intValue());
        }

        this.rect.setX(rect.getX());
        this.rect.setY(rect.getY());
    }

    public void drawPanel() {
        if (values.isEmpty()) return;

        panel_anim.setDirection(draw ? Direction.FORWARDS : Direction.BACKWARDS);
        double out = panel_anim.getOutput();
        if (out > 0) {
            if (rect.getX2() >= mc.getWindow().getWidth() / 2 - 110) {
                panel.rect.setX(rect.getX() - 110);
            } else {
                panel.rect.setX(rect.getX2() + 10);
            }
            panel.rect.setX(Math.round(panel.rect.getX() * 10.0f) / 10.0f);
            panel.rect.setY(rect.getY());
            panel.setValues(values);
            panel.draw(0, 0, (float) out);
        }
    }

    public boolean click(int mx, int my, int b) {
        //FloatRect rect = new FloatRect(this.rect.getX() * 2 / mc.options.guiScale, this.rect.getY() * 2 / mc.options.guiScale, this.rect.getW() * 2 / mc.options.guiScale, this.rect.getH() * 2 / mc.options.guiScale);

        if (rect.intersect(mx, my)) {
            if (b == GLFW.GLFW_MOUSE_BUTTON_2) {
                draw = !draw;
            }
            if (b == GLFW.GLFW_MOUSE_BUTTON_1 && draggable) {
                dragged = true;
                if (System.currentTimeMillis() > uptime) {
                    PATH.add(new V2F((int) (rect.getX() - mx), (int) (rect.getY() - my)));
                    uptime = System.currentTimeMillis() + 500L;
                }
            }
        }

        if (draw && !values.isEmpty()) {
            panel.click(mx, my, b);
        }

        return dragged;
    }

    public void release(int mx, int my, int b) {
        dragged = false;

        rect.setX((float) rect.getX().intValue());
        rect.setY((float) rect.getY().intValue());
    }

    public void close() {
        dragged = false;
        draw = false;
        if (!isEnabled()) {
            alpha_anim.setDirection(Direction.BACKWARDS);
        }
    }

    public static Color inject(Color color, float alpha) {
        int inject = (int) (color.getAlpha() * alpha);
        return ColorUtils.injectAlpha(color, inject);
    }

    public void drawNewClientRect(FloatRect rect) {
        if (FunctionManager.get(Hud.class).blur.get()) {
            BlurShader.registerRenderCall(() -> {
                GL.drawRoundedRect(rect, 3.5, Color.WHITE);
            });

            BlurShader.draw(4);
        }

        if (FunctionManager.get(Hud.class).glow.get()) GL.drawRoundedGlowRect(rect, 3.5, 4, Colors.getColor(0), Colors.getColor(90), Colors.getColor(270), Colors.getColor(180));
            //GL.drawGlow(rect, 8, ColorUtils.injectAlpha(Colors.getColor(0), 105), ColorUtils.injectAlpha(Colors.getColor(90), 105), ColorUtils.injectAlpha(Colors.getColor(180), 105), ColorUtils.injectAlpha(Colors.getColor(270), 105));
        else GL.drawRoundedGradientRect(rect, 3.5, ColorUtils.injectAlpha(Colors.getColor(0), 120), ColorUtils.injectAlpha(Colors.getColor(90), 120), ColorUtils.injectAlpha(Colors.getColor(270), 120), ColorUtils.injectAlpha(Colors.getColor(180), 120));
        GL.drawRoundedGradientOutline(rect, 3.5, 1d, Colors.getColor(0), Colors.getColor(90), Colors.getColor(270), Colors.getColor(180));
    }

    public static void drawRect(FloatRect rect, float a) {
        GL.drawQuad(rect,
                ColorUtils.injectAlpha(Colors.getColor(0), (int) (95f * a)),
                ColorUtils.injectAlpha(Colors.getColor(90), (int) (95f * a)),
                ColorUtils.injectAlpha(Colors.getColor(180), (int) (95f * a)),
                ColorUtils.injectAlpha(Colors.getColor(270), (int) (95f * a))
        );

        GL.prepare();
        Renderer3D.enableSmoothLine(2.5F);
        Renderer3D.begin(GL11.GL_LINE_STRIP);
        Renderer3D.color(ColorUtils.injectAlpha(Colors.getColor(0), (int) (a * 255)));
        GL11.glVertex2d(rect.getX(), rect.getY());
        Renderer3D.color(ColorUtils.injectAlpha(Colors.getColor(90), (int) (a * 255)));
        GL11.glVertex2d(rect.getX(), rect.getY2());
        Renderer3D.color(ColorUtils.injectAlpha(Colors.getColor(180), (int) (a * 255)));
        GL11.glVertex2d(rect.getX2(), rect.getY2());
        Renderer3D.color(ColorUtils.injectAlpha(Colors.getColor(270), (int) (a * 255)));
        GL11.glVertex2d(rect.getX2(), rect.getY());
        Renderer3D.color(ColorUtils.injectAlpha(Colors.getColor(0), (int) (a * 255)));
        GL11.glVertex2d(rect.getX(), rect.getY());
        Renderer3D.end();
        Renderer3D.disableSmoothLine();
        GL.end();
    }

    public static void drawRectHotbar(FloatRect rect) {
        if (FunctionManager.get(Hud.class).blur.get()) {
            BlurShader.registerRenderCall(() -> {
                GL.drawRoundedRect(rect, 3.5, Color.WHITE);
            });

            BlurShader.draw(4);
        }

        if (FunctionManager.get(Hud.class).glow.get()) GL.drawRoundedGlowRect(rect, 3.5, 4, Colors.getColor(0), Colors.getColor(90), Colors.getColor(270), Colors.getColor(180));
            //GL.drawGlow(rect, 8, ColorUtils.injectAlpha(Colors.getColor(0), 105), ColorUtils.injectAlpha(Colors.getColor(90), 105), ColorUtils.injectAlpha(Colors.getColor(180), 105), ColorUtils.injectAlpha(Colors.getColor(270), 105));
        else GL.drawRoundedGradientRect(rect, 3.5, ColorUtils.injectAlpha(Colors.getColor(0), 120), ColorUtils.injectAlpha(Colors.getColor(90), 120), ColorUtils.injectAlpha(Colors.getColor(270), 120), ColorUtils.injectAlpha(Colors.getColor(180), 120));
        GL.drawRoundedGradientOutline(rect, 3.5, 1d, Colors.getColor(0), Colors.getColor(90), Colors.getColor(270), Colors.getColor(180));
    }

    public static void drawRectGui(FloatRect rect, float a) {
        if (FunctionManager.get(Hud.class).blur.get()) {
            BlurShader.registerRenderCall(() -> {
                GL.drawRoundedRect(rect, 3.5, ColorUtils.injectAlpha(Color.WHITE, (int) (a * 255)));
            });

            BlurShader.draw(4);
        }

        if (FunctionManager.get(Hud.class).glow.get()) GL.drawRoundedGlowRect(rect, 3.5, 4, ColorUtils.injectAlpha(Colors.getColor(0), (int) (a * 255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (a * 255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (a * 255)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (a * 255)));
            //GL.drawGlow(rect, 8, ColorUtils.injectAlpha(Colors.getColor(0), 105), ColorUtils.injectAlpha(Colors.getColor(90), 105), ColorUtils.injectAlpha(Colors.getColor(180), 105), ColorUtils.injectAlpha(Colors.getColor(270), 105));
        else GL.drawRoundedGradientRect(rect, 3.5, ColorUtils.injectAlpha(Colors.getColor(0), (int) (a * 120)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (a * 120)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (a * 120)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (a * 120)));
        GL.drawRoundedGradientOutline(rect, 3.5, 1d, ColorUtils.injectAlpha(Colors.getColor(0), (int) (a * 255)), ColorUtils.injectAlpha(Colors.getColor(90), (int) (a * 255)), ColorUtils.injectAlpha(Colors.getColor(270), (int) (a * 255)), ColorUtils.injectAlpha(Colors.getColor(180), (int) (a * 255)));
    }

    public void startScale(double scale) {
        GL11.glPushMatrix();
        GL11.glTranslated(rect.getCenteredX(), rect.getCenteredY(), 0);
        GL11.glScaled(scale, scale, scale);
        GL11.glTranslated(-rect.getCenteredX(), -rect.getCenteredY(), 0);
    }

    public void endScale() {
        GL11.glPopMatrix();
    }

    public boolean canUpdate() {
        return BloodyClient.canUpdate();
    }

    public HudValue create(String name, boolean defaultValue) {
        return create(name, defaultValue, null);
    }

    public HudValue create(String name, boolean defaultValue, Runnable callback) {
        HudValue v = new HudValue(name, defaultValue);
        values.add(v);
        v.setCallback(callback);
        return v;
    }

    public float getAlpha() {
        return (float) alpha_anim.getOutput();
    }

    public String toString() {
        return "pos:" + rect.getX() + "," + rect.getY();
    }
}