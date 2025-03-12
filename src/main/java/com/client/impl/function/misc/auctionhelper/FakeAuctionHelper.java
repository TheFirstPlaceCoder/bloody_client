package com.client.impl.function.misc.auctionhelper;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ColorSetting;

import java.awt.*;

public class FakeAuctionHelper extends Function {
    public final ColorSetting minItem = Color().name("Самый дешевый").defaultValue(Color.GREEN).build();
    public final ColorSetting bestItem = Color().name("Самый выгодный").defaultValue(Color.RED).build();

    public FakeAuctionHelper() {
        super("Auction Helper", Category.PLAYER);
        setPremium(true);
    }
}
