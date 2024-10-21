package com.client.impl.hud;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.system.hud.setting.HudValue;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.Pair;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.SmoothStepAnimation;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.shader.shaders.GaussianBloomShader;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import com.google.common.util.concurrent.AtomicDouble;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;

public class FunctionListHud extends HudFunction {
    public FunctionListHud() {
        super(new FloatRect(5, 20, 50, 50), "FunctionList-Hud");
        for (Function function : FunctionManager.getFunctionList()) {
            SmoothStepAnimation smoothStepAnimation = new SmoothStepAnimation(200, 1);
            smoothStepAnimation.setDirection(function.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
            MAP.put(function, new Pair<>(smoothStepAnimation, smoothStepAnimation.getOutput()));
        }
    }

    private final HudValue textGlow = create("Блюр текста", false);
    private final HudValue quadGlow = create("Блюр фона", false);

    private final HashMap<Function, Pair<SmoothStepAnimation, Double>> MAP = new HashMap<>();
    private final int font = IFont.COMFORTAAB, size = 8;

    @Override
    public void draw(float alpha) {
        List<Function> functions = new ArrayList<>(FunctionManager.getFunctionList().stream().filter(f -> !f.getCategory().equals(Category.VISUAL)).toList());
        functions.sort(Comparator.comparing(f -> -IFont.getWidth(font, f.getName(), size)));

        if (functions.isEmpty()) {
            rect.setW(50F);
            rect.setH(50F);
            return;
        }

        for (Map.Entry<Function, Pair<SmoothStepAnimation, Double>> entry : MAP.entrySet()) {
            for (Function function : functions) {
                if (entry.getKey().equals(function)) {
                    entry.getValue().getA().setDirection(function.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
                }
                entry.getValue().setB(entry.getValue().getA().getOutput());
            }
        }

        AtomicDouble height = new AtomicDouble(0);

        functions.forEach(f -> height.addAndGet((IFont.getHeight(font, f.getName(), size) + 2) * MAP.get(f).getB()));
        rect.setW(IFont.getWidth(font, functions.get(0).getName(), size) + 4);
        rect.setH(height.floatValue());

        boolean dir = rect.getX() >= (float) mc.getWindow().getWidth() / 4;

        float y = rect.getY();

        for (Function function : functions) {
            float anim = MAP.get(function).getB().floatValue();

            if (anim <= 0) continue;

            if (function.isEnabled()) {
                double w = IFont.getWidth(font, function.getName(), size) + 4F;
                double h = IFont.getHeight(font, function.getName(), size) + 2F;
                FloatRect back = dir ? new FloatRect(rect.getX2() - w, y, w, h) : new FloatRect(rect.getX(), y, w, h);
                GaussianBloomShader.registerRenderCall(() -> GL.drawQuad(back,
                        inject(index(functions.indexOf(function), functions.size(), dir ? 0 : (int) (145 * anim)), alpha),
                        inject(index(functions.indexOf(function) + 1, functions.size(), dir ? 0 : (int) (145 * anim)), alpha),
                        inject(index(functions.indexOf(function) + 1, functions.size(), dir ? (int) (145 * anim) : 0), alpha),
                        inject(index(functions.indexOf(function), functions.size(), dir ? (int) (145 * anim) : 0), alpha)
                ));
                GL.drawQuad(back,
                        inject(index(functions.indexOf(function), functions.size(), dir ? 0 : (int) (55 * anim)), alpha),
                        inject(index(functions.indexOf(function) + 1, functions.size(), dir ? 0 : (int) (55 * anim)), alpha),
                        inject(index(functions.indexOf(function) + 1, functions.size(), dir ? (int) (55 * anim) : 0), alpha),
                        inject(index(functions.indexOf(function), functions.size(), dir ? (int) (55 * anim) : 0), alpha)
                );
            }
            y += (IFont.getHeight(font, function.getName(), size) + 2) * anim;
        }

        if (quadGlow.get()) {
            GaussianBloomShader.draw(2, 1.2f, false, 1);
        } else {
            GaussianBloomShader.free();
        }

        if (quadGlow.get()) {
            GaussianBloomShader.registerRenderCall(() -> line(functions, rect.getY(), dir, alpha));
            line(functions, rect.getY(), dir, alpha);
            GaussianBloomShader.draw(2, 1.2f, false, 1);
        } else {
            line(functions, rect.getY(), dir, alpha);
        }

        y = rect.getY();

        for (Function function : functions) {
            float anim = MAP.get(function).getB().floatValue();
            if (anim <= 0) continue;

            if (function.isEnabled()) {
                float finalY = y;
                GaussianBloomShader.registerRenderCall(() -> IFont.drawCenteredY(font, function.getName(), dir ? rect.getX2() - 2.5F - IFont.getWidth(font, function.getName(), size) : rect.getX() + 2.5F, finalY + ((IFont.getHeight(font, function.getName(), size) + 2F) / 2), inject(index(functions.indexOf(function), functions.size(), (int) (255 * anim)), alpha), size));
                IFont.drawCenteredY(font, function.getName(), dir ? rect.getX2() - 2.5F - IFont.getWidth(font, function.getName(), size) : rect.getX() + 2.5F, y + ((IFont.getHeight(font, function.getName(), size) + 2F) / 2), inject(index(functions.indexOf(function), functions.size(), (int) (255 * anim)), alpha), size);
            }

            y += (IFont.getHeight(font, function.getName(), size) + 2) * anim;
        }

        if (textGlow.get()) {
            GaussianBloomShader.draw(2, 1.2f, false, 1);
        } else {
            GaussianBloomShader.free();
        }
    }

    private void line(List<Function> functions, float y, boolean dir, float alpha) {
        GL.prepare();
        GL11.glLineWidth(2F);
        Renderer3D.begin(GL11.GL_LINE_STRIP);
        for (Function function : functions) {
            float anim = MAP.get(function).getB().floatValue();
            if (anim > 0) {
                Renderer3D.color(inject(index(functions.indexOf(function), functions.size(), 145), alpha));
                GL11.glVertex2d(dir ? rect.getX2() : rect.getX(), y);
                y += (IFont.getHeight(font, function.getName(), size) + 2) * anim;
            }
        }
        Renderer3D.color(inject(index(functions.size(), functions.size(), 145), alpha));
        GL11.glVertex2d(dir ? rect.getX2() : rect.getX(), rect.getY2());
        Renderer3D.end();
        GL.end();
        if (dir) {
            GL.drawLine(rect.getX2(), rect.getY(), rect.getX2() - IFont.getWidth(font, functions.get(0).getName(), size), rect.getY(), 2F, index(0, functions.size(), (int) (145 * alpha)), index(0, 0));
            GL.drawLine(rect.getX2(), rect.getY2(), rect.getX2() - IFont.getWidth(font, functions.get(functions.size() - 1).getName(), size), rect.getY2(), 2F, index(functions.size(), functions.size(), (int) (145 * alpha)), index(functions.size() - 1, 0));
        } else {
            GL.drawLine(rect.getX(), rect.getY(), rect.getX() + IFont.getWidth(font, functions.get(0).getName(), size), rect.getY(), 2F, index(0, (int) (145 * alpha)), index(0, functions.size(), 0));
            GL.drawLine(rect.getX(), rect.getY2(), rect.getX() + IFont.getWidth(font, functions.get(functions.size() - 1).getName(), size), rect.getY2(), 2F, index(functions.size(), functions.size(), (int) (145 * alpha)), index(functions.size(), functions.size(), 0));
        }
    }

    private Color index(int i, int maxSize, int alpha) {
        return ColorUtils.injectAlpha(Colors.getColor((360 / maxSize) * i), alpha);
    }

    private Color index(int i, int alpha) {
        return ColorUtils.injectAlpha(Colors.getColor(i * 16), alpha);
    }
}