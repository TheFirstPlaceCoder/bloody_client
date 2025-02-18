package com.client.clickgui.newgui;

import com.client.clickgui.Impl;
import com.client.clickgui.newgui.settings.AbstractSettingElement;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.utils.color.ColorUtils;
import com.client.utils.color.Colors;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CategoryElement implements Impl {
    public FloatRect rect, namedRect, innerRect, expandedRect;
    public Category category;
    public float moduleHeight = IFont.getHeight(IFont.MONTSERRAT_BOLD, "Trails", 9) * 1.3f,
            nameHeight = IFont.getHeight(IFont.MONTSERRAT_BOLD, "Visuals", 12);

    private final List<ModuleElement> functionButtons = new ArrayList<>();
    public float modulePadding = 0;
    public boolean held = false;

    public CategoryElement(FloatRect rect, Category category) {
        this.category = category;
        this.rect = rect;
        this.rect.setW(Math.max(rect.getW(), getMaxWidth()));
        this.expandedRect = new FloatRect(rect.getX() - 1, rect.getY() - 1, rect.getW() + 2, rect.getH() + 2);
        this.namedRect = new FloatRect(rect.getX().floatValue(), rect.getY().floatValue(), rect.getW().floatValue(), nameHeight);

        this.innerRect = new FloatRect(rect.getX().floatValue(), rect.getY() + nameHeight + 2.5f, rect.getW().floatValue(), rect.getH() - (nameHeight + 3.5f));

        build();
    }

    private void build() {
        functionButtons.clear();
        float y = innerRect.getY();

        for (Function function : FunctionManager.getFunctionList(category)) {
            functionButtons.add(new ModuleElement(new FloatRect(innerRect.getX().floatValue(), y, innerRect.getW().floatValue(), moduleHeight), function));
            y += moduleHeight + modulePadding;
        }
    }

    private float getMaxHeight() {
        float h = 0;

        for (ModuleElement functionButton : functionButtons) {
            h += functionButton.rect.getH() + modulePadding;
        }

        return h + 6;
    }

    private float getMaxWidth() {
        float w = IFont.getWidth(IFont.MONTSERRAT_BOLD, category.name(), 10);

        for (Function function : FunctionManager.getFunctionList(category)) {
            float nameWidth = IFont.getWidth(IFont.MONTSERRAT_BOLD, function.getName(), 9) + (function.isPremium() ? 3 + moduleHeight / 1.5f : 0);
            if (nameWidth > w) w = nameWidth;
        }

        return w + 20;
    }

    private void setHeights() {
        this.rect.setH(nameHeight + getMaxHeight());
        this.innerRect.setY(rect.getY() + nameHeight + 2.5f);
        this.innerRect.setH(getMaxHeight());
        this.namedRect.setY(rect.getY());
    }

    private void setWidths() {
        float maxWidth = getMaxWidth();
        this.rect.setW(Math.max(rect.getW(), maxWidth));
        this.innerRect.setX(rect.getX());
        this.innerRect.setW(Math.max(rect.getW(), maxWidth));
    }

    @Override
    public void draw(double mx, double my, float a) {
        if (GuiScreen.clickGui.sortMode.get().equals("По размеру")) {
            functionButtons.sort(Comparator.comparingDouble(e -> -IFont.getWidth(IFont.MONTSERRAT_BOLD, e.function.getName(), 9)));
        } else functionButtons.sort(Comparator.comparing(e -> e.function.getName()));

        setWidths();
        setHeights();

        if (!GuiScreen.clickGui.outlineMode.get().equals("Обычная") && !GuiScreen.clickGui.outlineMode.get().equals("Нету"))
            GL.drawRoundedGlowRect(rect, 4,5, AbstractSettingElement.inject(Colors.getColor(0), a), AbstractSettingElement.inject(Colors.getColor(90), a), AbstractSettingElement.inject(Colors.getColor(270), a), AbstractSettingElement.inject(Colors.getColor(180), a));

        GL.drawRoundedRect(rect, 4, AbstractSettingElement.inject(GuiScreen.clickGui.categoryColor.get(), a));

        if (!GuiScreen.clickGui.outlineMode.get().equals("Свечение") && !GuiScreen.clickGui.outlineMode.get().equals("Нету")) {
            int outlineAlpha = (int) (GuiScreen.clickGui.opacity.get() * 2.55);

            GL.drawRoundedGradientOutline(new FloatRect(rect.getX() - 1, rect.getY().floatValue(), rect.getW() + 2, rect.getH().floatValue()), 4, 1, AbstractSettingElement.inject(ColorUtils.injectAlpha(Colors.getColor(0), outlineAlpha), a), AbstractSettingElement.inject(ColorUtils.injectAlpha(Colors.getColor(90), outlineAlpha), a), AbstractSettingElement.inject(ColorUtils.injectAlpha(Colors.getColor(270), outlineAlpha), a), AbstractSettingElement.inject(ColorUtils.injectAlpha(Colors.getColor(180), outlineAlpha), a));
        }

        GL.drawLine(rect.getX() + 5, rect.getY() + nameHeight + 1.5, rect.getCenteredX(), rect.getY() + nameHeight + 1.5, 1, ColorUtils.injectAlpha(Colors.getColor(0), 0), ColorUtils.injectAlpha(Colors.getColor(90), (int) (a * 255)));

        GL.drawLine(rect.getCenteredX(), rect.getY() + nameHeight + 1.5, rect.getX2() - 5, rect.getY() + nameHeight + 1.5, 1, ColorUtils.injectAlpha(Colors.getColor(90), (int) (a * 255)), ColorUtils.injectAlpha(Colors.getColor(180), 0));

        switch (GuiScreen.clickGui.centerMode.get()) {
          case "Лево" -> IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, category.toString(), rect.getX() + 4, rect.getY() + nameHeight / 2, ColorUtils.injectAlpha(GuiScreen.clickGui.categoryTextColor.get(), (int) (a * 255)), 10);
          case "Центр" -> IFont.drawCenteredXY(IFont.MONTSERRAT_BOLD, category.toString(), rect.getCenteredX(), rect.getY() + nameHeight / 2, ColorUtils.injectAlpha(GuiScreen.clickGui.categoryTextColor.get(), (int) (a * 255)), 10);
          default -> IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, category.toString(), rect.getX2() - 4 - IFont.getWidth(IFont.MONTSERRAT_BOLD, category.toString(), 10), rect.getY() + nameHeight / 2, ColorUtils.injectAlpha(GuiScreen.clickGui.categoryTextColor.get(), (int) (a * 255)), 10);
        };

        float y = innerRect.getY();

        for (ModuleElement functionButton : functionButtons) {
            functionButton.rect.setX(innerRect.getX());
            functionButton.rect.setY(y);
            y += functionButton.rect.getH() + modulePadding;
            functionButton.draw(mx, my, a);
        }

        functionButtons.forEach(f -> {
            if (f.open || f.rect.getH().intValue() > (int)f.defaultHeight) {
                f.postRender(mx, my, a);
            }
        });
    }

    @Override
    public void click(double mx, double my, int button) {
        if (namedRect.intersect(mx, my)) {
            held = true;
            GuiScreen.CATEGORY_ELEMENTS.remove(this);
            GuiScreen.CATEGORY_ELEMENTS.add(this);
        }
        else
            for (ModuleElement functionButton : functionButtons) {
                functionButton.click(mx, my, button);
            }
    }

    @Override
    public void release(double mx, double my, int button) {
        if (held) held = false;
        else
            for (ModuleElement functionButton : functionButtons) {
                functionButton.release(mx, my, button);
            }
    }

    @Override
    public void key(int key) {
        for (ModuleElement functionButton : functionButtons) {
            functionButton.key(key);
        }
    }

    @Override
    public void symbol(char chr) {
        for (ModuleElement functionButton : functionButtons) {
            functionButton.symbol(chr);
        }
    }

    @Override
    public void scroll(double mx, double my, double amount) {

    }

    public void dragged(double x, double y, double xDelta, double yDelta, int button) {
        if (held) {
            rect.setX((float) (rect.getX() + xDelta));
            rect.setY((float) (rect.getY() + yDelta));
        }
    }

    @Override
    public void close() {

    }
}
