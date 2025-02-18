package com.client.impl.function.misc;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;

/**
 * __aaa__
 * 21.05.2024
 * */
public class BetterChat extends Function {
    public BetterChat() {
        super("Better Chat", Category.MISC);
    }

    public final BooleanSetting moreHistory = Boolean().name("Больше сообщений (Дропает FPS)").enName("More History (Drops FPS)").defaultValue(true).build();
    public final IntegerSetting count = Integer().name("Количество сообщений").enName("Message Count").max(500).min(1).defaultValue(100).visible(moreHistory::get).build();

    public final BooleanSetting formatcodes = Boolean().name("Разрешение символа параграфа").enName("Enable paragraph symbol").defaultValue(true).build();
    public final BooleanSetting keephistory = Boolean().name("Сохрянть историю").enName("Save History").defaultValue(true).build();
    public final BooleanSetting time = Boolean().name("Время").enName("Time").defaultValue(true).build();
    public final BooleanSetting animation = Boolean().name("Анимация").enName("Animation").defaultValue(true).build();

    public boolean getMoreHistory() {
        return isEnabled() && moreHistory.get();
    }

    public boolean getTime() {
        return isEnabled() && time.get();
    }

    public boolean getAnimation() {
        return isEnabled() && animation.get();
    }

    public boolean getKeepHistory() {
        return isEnabled() && keephistory.get();
    }

    public boolean getFormatCodes() {
        return isEnabled() && formatcodes.get();
    }
}