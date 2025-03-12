package com.client.impl.function.misc.autoseller;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.StringSetting;

import java.util.List;

public class FakeAutoSeller extends Function {
    public FakeAutoSeller() {
        super("Auto Seller", Category.MISC);
        setPremium(true);
    }

    private final ListSetting mode = List().defaultValue("HolyWorld").list(List.of("HolyWorld", "FunTime")).name("Режим").build();
    private final IntegerSetting size = Integer().name("Количество").defaultValue(5).min(1).max(9).build();
    private final IntegerSetting delay = Integer().name("Задержка (MS)").defaultValue(15000).max(30000).min(100).build();
    private final StringSetting sum = String().name("Цена").defaultValue("1000000").build();
    private final BooleanSetting full = Boolean().name("Полная сумма").defaultValue(false).build();
}
