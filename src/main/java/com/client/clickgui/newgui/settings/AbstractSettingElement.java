package com.client.clickgui.newgui.settings;

import com.client.clickgui.Impl;
import com.client.system.setting.api.AbstractSettings;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;

import java.awt.*;
import java.util.List;

public abstract class AbstractSettingElement implements Impl {
    public AbstractSettings<?> setting;
    public FloatRect rect;
    public float alpha = 1f;
    public float add;

    public AbstractSettingElement(AbstractSettings<?> setting, FloatRect rect) {
        this.setting = setting;
        this.rect = rect;
    }

    public String parse(String in, double w, int font, int size) {
        if (IFont.getWidth(font, in, size) < w) return in;

        List<String> words = List.of(in.split(" "));
        StringBuilder f = new StringBuilder();
        double tempW = 0;

        for (String word : words) {
            word += " ";
            tempW += IFont.getWidth(font, word, size);
            if (tempW >= w) {
                tempW = 0;
                f.append("\n");
            }
            f.append(word);
        }

        return f.toString();
    }

    public static Color inject(Color color, float alpha) {
        return inject(color, alpha, color.getAlpha());
    }

    public static Color inject(Color color, float alpha, int range) {
        return ColorUtils.injectAlpha(color, (int) (alpha * range));
    }

    public float getAdd() {
        return add;
    }

    public float getH() {
        return rect.getH();
    }

    public float getY2() {
        return rect.getY() + getH();
    }

    public Color withAlpha(Color color) {
        return ColorUtils.injectAlpha(color, (int) (alpha * 255));
    }
}