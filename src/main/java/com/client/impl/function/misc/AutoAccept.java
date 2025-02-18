package com.client.impl.function.misc;

import com.client.event.events.PacketEvent;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class AutoAccept extends Function {
    private final BooleanSetting onlyFriends = Boolean().name("Только от друзей").enName("Only Friends").defaultValue(true).build();
    private final BooleanSetting nickname = Boolean().name("Учитывать никнейм").enName("Include Nickname").defaultValue(false).build();

    public AutoAccept() {
        super("Auto Accept", Category.MISC);
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof GameMessageS2CPacket packet) {
            String message = packet.getMessage().getString();

            if (message.toLowerCase().contains("телепортироваться") || message.toLowerCase().contains("teleport")) {
                String name = parse(message);
                if (onlyFriends.get() && !FriendManager.isFriend(name)) return;

                accept(name);
            }
        }
    }

    private void accept(String name) {
        String m = "/tpaccept";

        if (nickname.get()) {
            m = m.concat(" ".concat(name));
        }

        mc.player.sendChatMessage(m);
    }

    private String parse(String message) {
        for (PlayerListEntry playerListEntry : mc.getNetworkHandler().getPlayerList()) {
            if (message.contains(playerListEntry.getProfile().getName())) {
                return playerListEntry.getProfile().getName();
            }
        }

        return "";
    }
}
