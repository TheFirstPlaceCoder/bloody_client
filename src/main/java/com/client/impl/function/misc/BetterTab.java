package com.client.impl.function.misc;

import com.client.impl.function.client.Friends;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ColorSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.utils.color.ColorUtils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.*;

public class BetterTab extends Function {
    public final IntegerSetting tabSize = Integer().name("Количество игроков").enName("Players Count").defaultValue(100).min(1).max(200).build();

    public final BooleanSetting self = Boolean().name("Подсвечивать себя").enName("Light Up Yourself").defaultValue(false).build();
    public final ColorSetting selfColor = Color().name("Цвет себя").enName("Color").defaultValue(Color.CYAN).visible(self::get).build();

    public final BooleanSetting friends = Boolean().name("Подсвечивать друзей").enName("Light Up Friends").defaultValue(false).build();

    public BetterTab() {
        super("Better Tab", Category.MISC);
    }

    public Text getPlayerName(PlayerListEntry playerListEntry) {
        Text name;
        Color color = null;

        name = playerListEntry.getDisplayName();
        if (name == null) name = new LiteralText(playerListEntry.getProfile().getName());

        if (playerListEntry.getProfile().getId().toString().equals(mc.player.getGameProfile().getId().toString()) && self.get()) {
            color = selfColor.get();
        } else if (friends.get() && FriendManager.isFriend(playerListEntry.getProfile().getName())) {
            color = FunctionManager.get(Friends.class).friendsColor.get();
        }

        if (color != null) {
            String nameString = name.getString();

            for (Formatting format : Formatting.values()) {
                if (format.isColor()) nameString = nameString.replace(format.toString(), "");
            }

            name = new LiteralText(nameString).setStyle(name.getStyle().withColor(TextColor.fromRgb(ColorUtils.fromRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()))));
        }

        return name;
    }
}
