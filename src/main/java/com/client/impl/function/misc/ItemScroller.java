package com.client.impl.function.misc;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;

public class ItemScroller extends Function {
    public ItemScroller() {
        super("Item Scroller", Category.MISC);
    }

    public final IntegerSetting delay = Integer().name("Задержка (MS)").defaultValue(50).max(300).min(0).build();

    public long time;
}