package com.client.system.theme;

import com.client.system.setting.settings.theme.ThemeContainer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private static final List<ThemeContainer> THEME_CONTAINERS = new ArrayList<>();

    public static void init() {
        THEME_CONTAINERS.add(new ThemeContainer("Pink Flavour", rgb(128, 0, 128), rgb(255, 192, 203)));
        THEME_CONTAINERS.add(new ThemeContainer("Digital Water", rgb(116, 235, 213), rgb(172, 182, 229)));
        THEME_CONTAINERS.add(new ThemeContainer("Argon", rgb(3, 0, 30), rgb(253, 239, 249)));
        THEME_CONTAINERS.add(new ThemeContainer("Velvet Sun", rgb(225, 238, 195), rgb(240, 80, 83)));
        THEME_CONTAINERS.add(new ThemeContainer("Crimson Tide", rgb(100, 43, 115), rgb(198, 66, 110)));
        THEME_CONTAINERS.add(new ThemeContainer("Telegram", rgb(28, 146, 210), rgb(242, 252, 254)));
        THEME_CONTAINERS.add(new ThemeContainer("Meridian", rgb(40, 60, 134), rgb(69, 162, 71)));
        THEME_CONTAINERS.add(new ThemeContainer("Visions of Grandeur", rgb(0, 0, 70), rgb(28, 181, 224)));
        THEME_CONTAINERS.add(new ThemeContainer("Venice", rgb(97, 144, 232), rgb(167, 191, 232)));
        THEME_CONTAINERS.add(new ThemeContainer("Love", rgb(32, 1, 34), rgb(111, 0, 0)));
        THEME_CONTAINERS.add(new ThemeContainer("Feel", rgb(69, 104, 220), rgb(176, 106, 179)));
        THEME_CONTAINERS.add(new ThemeContainer("Under the Lake", rgb(9, 48, 40), rgb(35, 122, 87)));
        THEME_CONTAINERS.add(new ThemeContainer("What lies Beyond", rgb(240, 242, 240), rgb(0, 12, 64)));
        THEME_CONTAINERS.add(new ThemeContainer("Jupiter", rgb(255, 216, 107), rgb(25, 84, 123)));
        THEME_CONTAINERS.add(new ThemeContainer("Lavender Fields", rgb(139, 99, 176), rgb(242, 242, 242)));
    }

    private static void add(Color first, Color second, String name) {
        THEME_CONTAINERS.add(new ThemeContainer(name, first, second));
    }

    private static Color rgb(int red, int green, int blue) {
        return new Color(red, green, blue, 255);
    }

    public static List<ThemeContainer> getThemeContainers() {
        return THEME_CONTAINERS;
    }
}
