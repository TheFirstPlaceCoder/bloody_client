package com.client.impl.function.client;

import com.client.event.events.TickEvent;
import com.client.system.companion.CompanionRegistry;
import com.client.system.companion.DumboOctopusEntity;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.*;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Companion extends Function {
    private final ListSetting color = List().name("Цвет").list(List.of("Желтый", "Оранжевый", "Светлый", "Фиолетовый")).defaultValue("Желтый").build();
    public final IntegerSetting teleportDistance = Integer().name("Дистанция для тп").min(20).max(100).defaultValue(50).build();
    public final BooleanSetting glow = Boolean().name("Glow").defaultValue(true).build();
    public final ColorSetting glowColor = Color().name("Цвет").defaultValue(new Color(255, 255, 255, 40)).visible(glow::get).build();

    public Companion() {
        super("Companion", Category.CLIENT);
    }

    public static DumboOctopusEntity entity;
    public int yaw = 50;
    public int yawDelay = 30;
    public int delayBetween = 20;
    public int moveTicks = 30;
    public float speed = 0.02f;
    public double verSpeed = 0.05;

    @Override
    public void onEnable() {
        entity = new DumboOctopusEntity(CompanionRegistry.DUMBO_OCTOPUS.get(), mc.world);
        entity.setPosition(mc.player.getX(), mc.player.getY() + 1, mc.player.getZ());
        entity.setAiDisabled(false);

        mc.world.addEntity(999, entity);
    }

    @Override
    public void onDisable() {
        if (entity != null) {
            entity.kill();
            entity.remove();
            mc.world.removeEntity(entity.getEntityId());
            entity = null;
        }
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (entity != null) {
            entity.setVariant(getVariant());
            entity.setWorld(mc.player.getEntityWorld());
            CompletableFuture<Void> a = CompletableFuture.runAsync(entity::tickMovement);
            a.join();
        }
    }

    public int getVariant() {
        return switch (color.get()) {
            case "Желтый" -> 0;
            case "Оранжевый" -> 1;
            case "Светлый" -> 2;
            default -> 3;
        };
    }
}