package com.client.impl.function.misc;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;

import java.util.List;

public class PassHider extends Function {
    private final MultiBooleanSetting settings = MultiBoolean().name("Работать на").enName("Settings").defaultValue(List.of(
            new MultiBooleanValue(true, "/login"),
            new MultiBooleanValue(true, "/register"),
            new MultiBooleanValue(true, "/an"),
            new MultiBooleanValue(true, "/tpa")
    )).build();

    public PassHider() {
        super("Pass Hider", Category.MISC);
    }

    public boolean shouldBlur(String string) {
        return isEnabled() && (((string.contains("/l") || string.contains("/login") || string.contains("/д") || string.contains("/дщпшт")) && settings.get("/login"))
                || ((string.contains("/reg") || string.contains("/register") || string.contains("/куп") || string.contains("/купшыеук")) && settings.get("/register"))
                || ((string.contains("/an") || string.contains("/anarchy") || string.contains("/фт") || string.contains("/фтфксрн")) && settings.get("/an"))
                || ((string.contains("/tpa") || string.contains("/езф")) && settings.get("/an")));
    }
}