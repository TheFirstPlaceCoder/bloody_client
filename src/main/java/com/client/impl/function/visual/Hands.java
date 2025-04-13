package com.client.impl.function.visual;

import com.client.BloodyClient;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.color.ColorUtils;

import java.awt.*;
import java.util.List;

public class Hands extends Function {
    public final IntegerSetting lineWidth = Integer().name("Ширина обводки").enName("Outline Width").defaultValue(5).min(1).max(10).build();
    public final ColorSetting colorSetting = Color().name("Цвет").enName("Color").defaultValue(ColorUtils.injectAlpha(Color.CYAN, 60)).build();

    public Hands() {
        super("Hands", Category.VISUAL);
    }

    @Override
    public void onDisable() {
        BloodyClient.shaderManager.reloadShaders();
    }

    public float getOpacity() {
        return (colorSetting.get().getAlpha() / 255f);
    }
}
