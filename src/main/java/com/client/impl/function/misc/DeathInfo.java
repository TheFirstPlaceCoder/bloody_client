package com.client.impl.function.misc;

import com.client.event.events.PacketEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.utils.game.chat.ChatUtils;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class DeathInfo extends Function {
    public DeathInfo() {
        super("Death Info", Category.MISC);
    }

    @Override
    public void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof HealthUpdateS2CPacket packet) {
            if (packet.getHealth() <= 0) {
                MutableText text = new LiteralText("");
                text.append("Координаты смерти: " + Formatting.WHITE + mc.player.getBlockPos().toShortString());
                ChatUtils.sendMsg("Death Info", text);
            }
        }
    }
}
