package com.client.impl.hud;

import com.client.impl.function.client.Hud;
import com.client.impl.function.hud.ModulesHud;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.Pair;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.SmoothStepAnimation;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.Fonts;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.shader.shaders.BlurShader;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class FunctionListHud extends HudFunction {
    public FunctionListHud() {
        super(new FloatRect(5, 20, 50, 50), "FunctionList-Hud");
        for (Function function : FunctionManager.getFunctionList()) {
            SmoothStepAnimation smoothStepAnimation = new SmoothStepAnimation(200, 1);
            smoothStepAnimation.setDirection(function.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
            MAP.put(function, new Pair<>(smoothStepAnimation, smoothStepAnimation.getOutput()));
        }
    }

    private final HashMap<Function, Pair<SmoothStepAnimation, Double>> MAP = new HashMap<>();
    private final int font = IFont.COMFORTAAB, size = 8;
    private List<Function> functions = new ArrayList<>();
    private List<Runnable> runnables = new CopyOnWriteArrayList<>();

    private Hud hud;
    private ModulesHud modulesHud;

    @Override
    public void tick() {
        if (modulesHud == null) modulesHud = FunctionManager.get(ModulesHud.class);
        if (hud == null) hud = FunctionManager.get(Hud.class);
        if (functions.isEmpty()) functions = new ArrayList<>(FunctionManager.getFunctionList().stream().filter(f -> !f.getCategory().equals(Category.HUD)).toList());

        functions.sort(Comparator.comparing(f -> -IFont.getWidth(font, f.getName() + " " + Formatting.GRAY + (!f.getHudPrefix().isEmpty() ? "[" : "") + f.getHudPrefix() + (!f.getHudPrefix().isEmpty() ? "]" : ""), size)));

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
        //rect.setX(rect.getX() + (rect.getW() - (IFont.getWidth(font, functions.get(0).getName() + (!functions.get(0).getHudPrefix().isEmpty() ? " [" : "") + functions.get(0).getHudPrefix() + (!functions.get(0).getHudPrefix().isEmpty() ? "]" : ""), size) + 4)));
        rect.setW(IFont.getWidth(font, functions.get(0).getName() + (!functions.get(0).getHudPrefix().isEmpty() ? " [" : "") + functions.get(0).getHudPrefix() + (!functions.get(0).getHudPrefix().isEmpty() ? "]" : ""), size) + 4);
        rect.setH(height.floatValue());
    }

    @Override
    public void draw(float alpha) {
        if (modulesHud == null) modulesHud = FunctionManager.get(ModulesHud.class);

        boolean dir = rect.getX() >= (float) mc.getWindow().getWidth() / 4;

        AtomicReference<Float> y = new AtomicReference<>(rect.getY());

        int i = 0;
        for (Function function : functions) {
            float anim = MAP.get(function).getB().floatValue();
            if (anim <= 0) continue;

            String text = function.getName() + " " + Formatting.GRAY + (!function.getHudPrefix().isEmpty() ? "[" : "") + function.getHudPrefix() + (!function.getHudPrefix().isEmpty() ? "]" : "");

            if (function.isEnabled()) {
                double w = IFont.getWidth(font, text, size) + (modulesHud.line.get() ? 5 : 0);
                double h = IFont.getHeight(font, text, size) + 2;
                FloatRect back = dir ? new FloatRect(rect.getX2() - w, y.get(), w, h) : new FloatRect(rect.getX(), y.get(), w, h);

                if (hud.blur.get()) {
                    BlurShader.registerRenderCall(() -> {
                        GL.drawRoundedRect(back, 1.5, Color.WHITE);
                    });
                }

                int finalI = i;
                if (modulesHud.drawBackground.get()) runnables.add(() -> GL.drawRoundedGlowRect(back.expand(-1f), 1.5, 3, index(finalI, (int) (255 * anim))));
            }
            i++;
            y.updateAndGet(v -> (v + (IFont.getHeight(font, text, size) + 2) * anim));
        }

        if (hud.blur.get()) BlurShader.draw(4);

        if (!runnables.isEmpty()) {
            runnables.forEach(Runnable::run);
            runnables.clear();
        }

        if (modulesHud.line.get()) {
            float x1 = dir ? rect.getX2() : rect.getX();
            Color color = modulesHud.lineColor.get();
            GL.drawRoundedGlowRect(x1 - 1, rect.getY(), 3, y.get() - rect.getY(), 1, 2, 0.75, color, color, color, color);
        }

        y.set(rect.getY());

        int i2 = 0;
        for (Function function : functions) {
            float anim = MAP.get(function).getB().floatValue();
            if (anim <= 0) continue;

            String text = function.getName() + " " + Formatting.GRAY + (!function.getHudPrefix().isEmpty() ? "[" : "") + function.getHudPrefix() + (!function.getHudPrefix().isEmpty() ? "]" : "");

            if (function.isEnabled()) {
                IFont.drawCenteredY(font, text, dir ? rect.getX2() - (modulesHud.line.get() ? 2.5F : 0F) - IFont.getWidth(font, text, size) : rect.getX() + (modulesHud.line.get() ? 5 : 0), y.get() + ((IFont.getHeight(font, text, size) + 2F) / 2), inject(index(i2, (int) (255 * anim)), alpha), size);
            }

            i2++;
            y.updateAndGet(v -> (v + (IFont.getHeight(font, text, size) + 2) * anim));
        }
    }

    private Color index(int i, int alpha) {
        return ColorUtils.injectAlpha(Colors.getColor(i * modulesHud.steps.get()), alpha);
    }
}