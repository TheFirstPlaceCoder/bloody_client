package com.client.impl.function.combat;

import com.client.event.events.GameEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.concurrent.CopyOnWriteArrayList;

public class AntiBot extends Function {
    private final BooleanSetting remove = Boolean().name("Удалять").enName("Remove").defaultValue(true).build();

    public AntiBot() {
        super("Anti Bot", Category.COMBAT);
    }

    private static final CopyOnWriteArrayList<PlayerEntity> bots = Lists.newCopyOnWriteArrayList();

    @Override
    public void onEnable() {
        bots.clear();
    }

    @Override
    public void onDisable() {
        bots.clear();
    }

    @Override
    public void onGameLeftEvent(GameEvent.Left event) {
        bots.clear();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        for (PlayerEntity entity : mc.world.getPlayers()) {
            if (!entity.getUuid().equals(PlayerEntity.getOfflinePlayerUuid(entity.getName().getString()))) {
                if (!bots.contains(entity)) {
                    bots.add(entity);
                }
            }
        }

        if (remove.get()) {
            try {
                mc.world.getPlayers().removeIf(bots::contains);
            } catch (Exception ignored) {
            }
        }
    }

    public static boolean isBot(PlayerEntity entity) {
        return bots.contains(entity);
    }
}
