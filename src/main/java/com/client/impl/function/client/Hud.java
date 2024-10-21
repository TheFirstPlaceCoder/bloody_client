package com.client.impl.function.client;

import com.client.event.events.TickEvent;
import com.client.impl.hud.*;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.hud.HudManager;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;

import java.util.List;

public class Hud extends Function {
    public final BooleanSetting glow = Boolean().name("Свечение").defaultValue(true).build();
    public final BooleanSetting blur = Boolean().name("Блюр").defaultValue(true).build();

    private final MultiBooleanSetting listSetting = MultiBoolean().name("Элементы").defaultValue(List.of(
            new MultiBooleanValue(true, "Вотермарка"),
            new MultiBooleanValue(false, "Эффекты"),
            new MultiBooleanValue(false, "Кейбинды"),
            new MultiBooleanValue(false, "Стафф"),
            new MultiBooleanValue(false, "Броня"),
            new MultiBooleanValue(false, "Скорость"),
            new MultiBooleanValue(false, "Пинг"),
            new MultiBooleanValue(false, "Координаты"),
            new MultiBooleanValue(false, "ФПС"),
            new MultiBooleanValue(false, "ТПС"),
            new MultiBooleanValue(false, "Модули"),
            new MultiBooleanValue(false, "Таргет"),
            new MultiBooleanValue(false, "Музыка"),
            new MultiBooleanValue(false, "Хот-бар")
    )).build();

    @Override
    public void tick(TickEvent.Pre event) {
        HudManager.get(WatermarkHud.class).setEnabled(listSetting.get(0));
        HudManager.get(PotionHud.class).setEnabled(listSetting.get(1));
        HudManager.get(KeybindHud.class).setEnabled(listSetting.get(2));
        HudManager.get(StaffHud.class).setEnabled(listSetting.get(3));
        HudManager.get(ArmorHud.class).setEnabled(listSetting.get(4));
        HudManager.get(SpeedHud.class).setEnabled(listSetting.get(5));
        HudManager.get(PingHud.class).setEnabled(listSetting.get(6));
        HudManager.get(CoordsHud.class).setEnabled(listSetting.get(7));
        HudManager.get(FpsHud.class).setEnabled(listSetting.get(8));
        HudManager.get(TpsHud.class).setEnabled(listSetting.get(9));
        HudManager.get(FunctionListHud.class).setEnabled(listSetting.get(10));
        HudManager.get(TargetHud.class).setEnabled(listSetting.get(11));
        HudManager.get(MusicHud.class).setEnabled(listSetting.get(12));
    }

    public Hud() {
        super("Hud", Category.CLIENT);
    }

    public boolean drawHotbar() {
        return listSetting.get("Хот-бар") && isEnabled();
    }
}
