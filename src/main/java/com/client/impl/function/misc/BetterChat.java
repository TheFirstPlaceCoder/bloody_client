package com.client.impl.function.misc;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;

/**
 * __aaa__
 * 21.05.2024
 * */
public class BetterChat extends Function {
    public BetterChat() {
        super("Better Chat", Category.MISC);
    }

    public final BooleanSetting keephistory = Boolean().name("Сохрянть историю").defaultValue(true).build();
    public final BooleanSetting time = Boolean().name("Время").defaultValue(true).build();
    public final BooleanSetting animation = Boolean().name("Анимация").defaultValue(true).build();

    public boolean getTime() {
        return isEnabled() && time.get();
    }

    public boolean getAnimation() {
        return isEnabled() && animation.get();
    }

    public boolean getKeepHistory() {
        return isEnabled() && keephistory.get();
    }
}