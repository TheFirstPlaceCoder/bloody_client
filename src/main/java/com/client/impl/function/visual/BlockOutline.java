package com.client.impl.function.visual;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.color.Colors;

import java.awt.*;
import java.util.List;

public class BlockOutline extends Function {
    public final ListSetting color = List().name("Режим цвета").list(List.of("Клиентский", "Статичный")).defaultValue("Клиентский").build();
    public final ColorSetting colorSetting = Color().name("Цвет").defaultValue(Color.CYAN).visible(() -> color.get().equals("Статичный")).build();

    public BlockOutline() {
        super("Block Outline", Category.VISUAL);
    }

    public Color getFogColor() {
        if (color.get().equals("Статичный")) {
            return colorSetting.get();
        } else {
            return Colors.getColor(0);
        }
    }
}
