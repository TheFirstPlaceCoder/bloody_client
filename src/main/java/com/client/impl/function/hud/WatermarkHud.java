package com.client.impl.function.hud;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;

import java.util.List;

public class WatermarkHud extends Function {
    private final MultiBooleanSetting listSetting = MultiBoolean().name("Элементы").enName("Elements").defaultValue(List.of(
            new MultiBooleanValue(true, "Имя"),
            new MultiBooleanValue(true, "UID"),
            new MultiBooleanValue(true, "Группа"),
            new MultiBooleanValue(true, "Премиум"),
            new MultiBooleanValue(true, "IP")
    )).build();

    public WatermarkHud() {
        super("Watermark", Category.HUD);
    }

    public boolean getAccountName() {
        return isEnabled() && listSetting.get("Имя");
    }

    public boolean getUid() {
        return isEnabled() && listSetting.get("UID");
    }

    public boolean getGroup() {
        return isEnabled() && listSetting.get("Группа");
    }

    public boolean getPremium() {
        return isEnabled() && listSetting.get("Премиум");
    }

    public boolean getIP() {
        return isEnabled() && listSetting.get("IP");
    }
}
