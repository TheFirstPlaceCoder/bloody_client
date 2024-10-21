package com.client.clickgui;

import com.client.clickgui.button.SettingButton;
import com.client.impl.function.client.ClickGui;
import com.client.impl.function.client.Hud;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import com.client.utils.render.wisetree.render.render2d.utils.shader.shaders.BlurShader;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionTab implements Impl {
    public FloatRect rect, innerRect;
    public Category category;

    public static final Map<String, Float> SCROLLER_MAP = new HashMap<>();

    private float scroll, targetScroll, amount;

    private final List<FunctionButton> functionButtons = new ArrayList<>();
    
    public FunctionTab(FloatRect rect, Category category) {
        this.rect = rect;
        this.innerRect = new FloatRect(rect.getX() + 5, rect.getY() + 25, rect.getW() - 10, rect.getH() - 30);
        this.category = category;

        if (SCROLLER_MAP.get(category.name()) != null) {
            targetScroll = SCROLLER_MAP.get(category.name());
            scroll = targetScroll;
        } else {
            SCROLLER_MAP.put(category.name(), 0F);
        }

        build();
        GuiScreen.callback.add(this::build);
    }

    private void build() {
        functionButtons.clear();
        float y = innerRect.getY() + 3;

        for (Function function : FunctionManager.getFunctionList(category)) {
            if (!GuiScreen.search.isEmpty() && !function.getName().replace(" ", "").toLowerCase().contains(GuiScreen.search.replace(" ", "").toLowerCase())) continue;
            functionButtons.add(new FunctionButton(new FloatRect(innerRect.getX() + 3, y, innerRect.getW() - 6, 20), innerRect, function));
            y += 23;
        }
    }

    private float getMaxHeight() {
        float h = 0;

        for (FunctionButton functionButton : functionButtons) {
            h += functionButton.rect.getH() + 3;
        }

        return h;
    }

    private void scroll() {
        if (functionButtons.isEmpty() || getMaxHeight() < innerRect.getH()) {
            targetScroll = 0;
        } else {
            FunctionButton first = functionButtons.get(0);
            FunctionButton second = functionButtons.get(functionButtons.size() - 1);

            if (first.rect.getY() > innerRect.getY() + 4) {
                targetScroll = 0;
            } else if (second.rect.getY2() < innerRect.getY2() - 4) {
                targetScroll = -(getMaxHeight() - innerRect.getH() + 3);
            } else if (amount != 0) {
                targetScroll += amount * 20;
                amount = 0;
            }
        }

        scroll = AnimationUtils.fast(scroll, targetScroll);
    }

    @Override
    public void draw(double mx, double my, float alpha) {
        scroll();

        for (Map.Entry<String, Float> stringFloatEntry : SCROLLER_MAP.entrySet()) {
            if (category.name().equals(stringFloatEntry.getKey()))
                stringFloatEntry.setValue(targetScroll);
        }

        //GL.drawRoundedRect(rect, 8, SettingButton.inject(GuiScreen.BACK, alpha));

        HudFunction.drawRectGui(new FloatRect(rect.getX(), rect.getY() - 3, rect.getW(), 20), alpha);
        HudFunction.drawRectGui(new FloatRect(rect.getX().floatValue(), rect.getY() + 21, rect.getW().floatValue(), rect.getH() - 21), alpha);
        //GL.drawRoundedRect(innerRect, 6, SettingButton.inject(GuiScreen.INNER, alpha));
        IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, category.name(), rect.getCenteredX(), rect.getY() + 7f, SettingButton.inject(Color.WHITE, alpha), 12);

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(innerRect.getX(),
                innerRect.getY(),
                innerRect.getW(),
                innerRect.getH()
        );

        float y = innerRect.getY() + 3 + scroll;

        for (FunctionButton functionButton : functionButtons) {
            functionButton.setY(y);
            y += functionButton.rect.getH() + 3;
            functionButton.tick();
            if (functionButton.rect.getY2() < innerRect.getY() || functionButton.rect.getY() > innerRect.getY2()) continue;
            functionButton.draw(mx, my, alpha);
        }

        ScissorUtils.unset();
        ScissorUtils.pop();

        functionButtons.forEach(f -> {
            if (f.open || f.rect.getH().intValue() > (int)f.defaultHeight) {
                f.postRender(mx, my, alpha);
            }
        });

        //GL.drawShadowRect(new FloatRect(innerRect.getX(), innerRect.getY() - 1, innerRect.getW(), 10), GL.Direction.DOWN, SettingButton.inject(GuiScreen.BACK, alpha));
        //GL.drawShadowRect(new FloatRect(innerRect.getX(), innerRect.getY2() - 10, innerRect.getW(), 11), GL.Direction.UP, SettingButton.inject(GuiScreen.BACK, alpha));
    }

    @Override
    public void click(double mx, double my, int button) {
        for (FunctionButton functionButton : functionButtons) {
            functionButton.click(mx, my, button);
        }
    }

    @Override
    public void release(double mx, double my, int button) {
        for (FunctionButton functionButton : functionButtons) {
            functionButton.release(mx, my, button);
        }
    }

    @Override
    public void key(int key) {
        for (FunctionButton functionButton : functionButtons) {
            functionButton.key(key);
        }
    }

    @Override
    public void symbol(char chr) {
        for (FunctionButton functionButton : functionButtons) {
            functionButton.symbol(chr);
        }
    }

    @Override
    public void scroll(double mx, double my, double amount) {
        if (innerRect.intersect(mx, my) && getMaxHeight() > innerRect.getH()) {
            this.amount = (float) amount;
        }
    }

    @Override
    public void close() {
        SCROLLER_MAP.entrySet().forEach(f -> {
            if (f.getKey().equals(category.name()))
                f.setValue(targetScroll);
        });
        for (FunctionButton functionButton : functionButtons) {
            functionButton.close();
        }
    }
}