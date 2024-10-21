package com.client.impl.function.misc;

import com.client.BloodyClient;
import com.client.impl.function.combat.HitBox;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.StringSetting;
import net.minecraft.client.network.PlayerListEntry;

/**
 * __aaa__
 * 21.05.2024
 * */
public class NameProtect extends Function {
    public NameProtect() {
        super("Name Protect", Category.MISC);
    }

    private final StringSetting namer = String().name("Имя").defaultValue("bloody-client").build();
    private final BooleanSetting friends = Boolean().name("Применять на друзей").defaultValue(true).build();
    private final BooleanSetting all = Boolean().name("Применять на всех").defaultValue(false).build();

    public String replace(String string) {
        if (!isEnabled() || string == null) return string;

        if (friends.get()) {
            for (String friend : FriendManager.getFriends()) {
                string = string.replace(friend, getProtectedName());
            }
        }

        if (all.get() && !mc.isInSingleplayer() && BloodyClient.canUpdate()) {
            for (PlayerListEntry playerListEntry : mc.getNetworkHandler().getPlayerList()) {
                string = string.replace(playerListEntry.getProfile().getName(), getProtectedName());
            }
        }

        return string.replace(mc.getSession().getUsername(), getProtectedName());
    }

    private String getProtectedName() {
        return namer.get();
    }
}