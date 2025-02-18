package com.client.impl.function.hud;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;

import java.util.List;

public class InfoHud extends Function {
    private final MultiBooleanSetting listSetting = MultiBoolean().name("Элементы").enName("Elements").defaultValue(List.of(
            new MultiBooleanValue(false, "Скорость"),
            new MultiBooleanValue(false, "Пинг"),
            new MultiBooleanValue(false, "Координаты"),
            new MultiBooleanValue(false, "ТПС")
    )).build();

    public InfoHud() {
        super("Info", Category.HUD);
    }

    public boolean getSpeed() {
        return isEnabled() && listSetting.get("Скорость");
    }

    public boolean getPing() {
        return isEnabled() && listSetting.get("Пинг");
    }

    public boolean getCoords() {
        return isEnabled() && listSetting.get("Координаты");
    }

    public boolean getTps() {
        return isEnabled() && listSetting.get("ТПС");
    }
}