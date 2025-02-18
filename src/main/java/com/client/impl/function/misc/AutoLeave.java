package com.client.impl.function.misc;

import com.client.event.events.TickEvent;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.entity.EntityUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;

import java.util.List;

public class AutoLeave extends Function {
    public AutoLeave() {
        super("Auto Leave", Category.MISC);
    }

    private final ListSetting mode = List().name("Тип").enName("Mode").list(List.of("Отключение", "Домой", "Спавн", "Хаб")).defaultValue("Отключение").build();
    private final IntegerSetting range = Integer().name("Дистанция").enName("Distance").defaultValue(120).min(0).max(150).build();
    private final BooleanSetting alsoNude = Boolean().name("Ливать от голых").enName("Include Naked").defaultValue(true).build();

    @Override
    public void tick(TickEvent.Pre event) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player.isDead()) continue;
            if (player == mc.player) continue;
            if (FriendManager.isFriend(player)) continue;
            if (mc.player.distanceTo(player) > range.get()) continue;
            if (EntityUtils.isBot(player)) continue;
            if (!alsoNude.get() && !player.getArmorItems().iterator().hasNext()) continue;

            leave();
        }
    }

    private void leave() {
        switch (mode.get()) {
            case "Отключение" -> mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.of("§7§l［§6§lAuto Leave§7§l］ §c Рядом " + "игрок!")));
            case "Домой" -> mc.player.sendChatMessage("/home");
            case "Спавн" -> mc.player.sendChatMessage("/spawn");
            default -> mc.player.sendChatMessage("/hub");
        }

        this.toggle();
    }
}