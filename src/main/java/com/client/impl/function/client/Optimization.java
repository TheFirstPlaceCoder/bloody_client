package com.client.impl.function.client;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.optimization.ConfigVariables;
import com.google.common.collect.Lists;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Optimization extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Дистанция", "Количество")).defaultValue("Дистанция").build();

    private final IntegerSetting range = Integer().name("Дистанция до игроков").enName("Players Distance").defaultValue(18).min(0).max(40).visible(() -> mode.get().equals("Дистанция")).build();
    private final IntegerSetting count = Integer().name("Количество игроков").enName("Players Count").defaultValue(10).min(0).max(30).visible(() -> !mode.get().equals("Дистанция")).build();
    private final IntegerSetting rangeItem = Integer().name("Дистанция до предметов").enName("Items Distance").defaultValue(5).min(0).max(8).visible(() -> mode.get().equals("Дистанция")).build();
    private final IntegerSetting countItem = Integer().name("Количество предметов").enName("Items Count").defaultValue(10).min(0).max(50).visible(() -> !mode.get().equals("Дистанция")).build();

    public final BooleanSetting rayTrace = Boolean().name("Оптимизация рендера сущностей").enName("Render Optimization").defaultValue(false).build();
    private final IntegerSetting delay = Integer().name("Задержка").enName("Delay").defaultValue(10).min(0).max(100).visible(rayTrace::get).build();

    public Optimization() {
        super("Optimization", Category.CLIENT);
    }

    public List<AbstractClientPlayerEntity> playerEntities = new ArrayList<>();
    public List<ItemEntity> itemEntities = new ArrayList<>();

    public List<AbstractClientPlayerEntity> getPlayerEntities() {
        return playerEntities;
    }

    public List<ItemEntity> getItemEntities() {
        return itemEntities;
    }

    @Override
    public void onEnable() {
        playerEntities.clear();
        itemEntities.clear();
    }

    @Override
    public void onDisable() {
        playerEntities.clear();
        itemEntities.clear();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        ConfigVariables.sleepDelay = delay.get();
        CompletableFuture<Void> sorting = CompletableFuture.runAsync(() -> {
            playerEntities.clear();

            List<AbstractClientPlayerEntity> players = new ArrayList<>();
            players.addAll(mc.world.getPlayers());
            players.sort(Comparator.comparingDouble(mc.player::distanceTo));

            if (mode.get().equals("Дистанция")) players.removeIf(e -> mc.player.distanceTo(e) > range.get());
            else players = players.subList(0, Math.min(players.size(), count.get()));

            playerEntities.addAll(players);
        });

        sorting.join();

        CompletableFuture<Void> sorting2 = CompletableFuture.runAsync(() -> {
            itemEntities.clear();

            List<ItemEntity> items = getItems();
            items.sort(Comparator.comparingDouble(mc.player::distanceTo));

            if (mode.get().equals("Дистанция")) items.removeIf(e -> mc.player.distanceTo(e) > rangeItem.get());
            else items = items.subList(0, Math.min(items.size(), countItem.get()));

            itemEntities.addAll(items);
        });

        sorting2.join();
    }

    public List<ItemEntity> getItems() {
        List<ItemEntity> list = new ArrayList<>();
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof ItemEntity a) list.add(a);
        }

        return list;
    }
}
