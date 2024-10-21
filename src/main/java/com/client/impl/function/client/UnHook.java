package com.client.impl.function.client;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.StringSetting;

public class UnHook extends Function {
    public final StringSetting enableCommand = String().name("Команда включения").defaultValue("unhook on").build();
    public final StringSetting disableCommand = String().name("Команда выключения").defaultValue("unhook off").build();

    public UnHook() {
        super("Un Hook", Category.CLIENT);
    }
}