package com.client.impl.function.client;

import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.system.textures.DownloadImage;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.Identifier;

import java.util.List;

public class CustomCape extends Function {
    public final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Street", "Relax", "Cosmos")).defaultValue("Street").callback(e -> loadedCapeTexture = false).build();
    public final BooleanSetting glint = Boolean().name("Переливание").enName("Glint").defaultValue(true).build();
    public final BooleanSetting friends = Boolean().name("Друзья").enName("Friends").defaultValue(true).build();

    public CustomCape() {
        super("Custom Cape", Category.CLIENT);
        setPremium(true);
    }

    @Override
    public void onEnable() {
        loadedCapeTexture = false;
    }

    @Override
    public void onDisable() {
        loadedCapeTexture = false;
    }

    public boolean loadedCapeTexture = false;

    public Identifier getCapeTexture(GameProfile profile) {
        if (mc.world == null || mc.player == null) return null;
        if (profile != mc.player.getGameProfile() && (!friends.get() || !FriendManager.isFriend(profile.getName()))) return null;
        return switch (mode.get()) {
            case "Street" -> DownloadImage.getIdentifier(DownloadImage.CAPE_1);
            case "Relax" -> DownloadImage.getIdentifier(DownloadImage.CAPE_2);
            default -> DownloadImage.getIdentifier(DownloadImage.CAPE_3);
        };
    }
}
