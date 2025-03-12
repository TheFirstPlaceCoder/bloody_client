package com.client.impl.function.misc.cheststealer;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.*;

import java.util.List;

public class FakeChestStealer extends Function {
    public FakeChestStealer() {
        super("Chest Stealer", Category.MISC);
        setPremium(true);
    }

    private final KeybindSetting openGui = Keybind().name("Открыть меню").enName("Open Menu").defaultValue(-1).build();
    public final ListSetting bypassClick = List().name("Обход клика").enName("Click Bypass").list(List.of(
            "Обычный", "New"
    )).defaultValue("New").build();
    private final BooleanSetting funtime = Boolean().name("Обход FunTime").enName("FunTime Mode").defaultValue(true).build();
    private final ListSetting sortMode = List().list(List.of("Приоритет", "Только", "Нет")).enName("Sort Mode").name("Сортировка").defaultValue("Только").build();
    private final IntegerSetting delay = Integer().name("Задержка (MS)").enName("Delay (MS)").min(0).max(1000).defaultValue(100).build();
    private final KeybindSetting openShulker = Keybind().name("Открыть мистик").enName("Open Chest").defaultValue(-1).build();
    private final DoubleSetting distance = Double().name("Дистанция").enName("Open Distance").defaultValue(3.3).max(6).min(0).visible(() -> openShulker.get() != -1).build();
}
