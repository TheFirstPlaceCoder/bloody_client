package com.client.impl.function.client;

import api.interfaces.EventHandler;
import com.client.event.events.AttackEntityEvent;
import com.client.event.events.PacketEvent;
import com.client.system.friend.FriendManager;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.ColorSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

import java.awt.*;

public class Friends extends Function {
    public Friends() {
        super("Friends", Category.CLIENT);
    }

    public ColorSetting friendsColor = Color().name("Цвет").enName("Color").defaultValue(Color.GREEN).build();
    public BooleanSetting dontAttack = Boolean().name("Не атаковать").enName("Don't Attack").defaultValue(true).build();

    @EventHandler
    private void onPacketEvent(PacketEvent.Send event) {
        if (dontAttack.get() && event.packet instanceof PlayerInteractEntityC2SPacket packet) {
            Entity entity = packet.getEntity(mc.world);
            if (entity instanceof PlayerEntity && packet.getType().equals(PlayerInteractEntityC2SPacket.InteractionType.ATTACK) && FriendManager.isFriend(entity)) {
                event.cancel();
            }
        }
    }
}