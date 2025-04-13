package com.client.impl.function.movement;

import com.client.event.events.PlayerMoveEvent;
import com.client.event.events.PlayerTravelEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.impl.function.movement.speedmodes.grim.GrimCollide;
import com.client.impl.function.movement.speedmodes.other.FunTimeSnow;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;

import java.util.List;

public class Speed extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(
            List.of("Grim", "FunTime Snow")).defaultValue("Grim").callback(this::onChangeSpeedMode).build();

    public final DoubleSetting expand = Double().name("Оффсет").enName("Box Offset").defaultValue(1.0).min(0).max(1).visible(() -> mode.get().equals("FunTime")).build();
    public final IntegerSetting speed = Integer().name("Скорость от игроков").enName("Speed From Players").defaultValue(7).min(0).max(15).visible(() -> mode.get().equals("FunTime")).build();
    public final BooleanSetting armorStands = Boolean().name("Армор стенды").enName("Armor Stands").defaultValue(true).visible(() -> mode.get().equals("FunTime")).build();
    public final BooleanSetting others = Boolean().name("Другие сущности").enName("Other Entities").defaultValue(true).visible(() -> mode.get().equals("FunTime")).build();

    public Speed() {
        super("Speed", Category.MOVEMENT);
    }

    public SpeedMode currentSpeedMode;

    @Override
    public void onEnable() {
        onChangeSpeedMode(mode.get());
    }

    @Override
    public void onDisable() {
        if (currentSpeedMode != null) currentSpeedMode.onDisable();
    }

    @Override
    public void tick(TickEvent.Post e) {
        currentSpeedMode.tick(e);
    }

    @Override
    public void tick(TickEvent.Pre e) {
        currentSpeedMode.tick(e);
    }

    @Override
    public void onPlayerTravelEvent(PlayerTravelEvent e) {
        currentSpeedMode.onTravel(e);
    }

    @Override
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        currentSpeedMode.onMove(event);
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        currentSpeedMode.sendMovementPackets(event);
    }

    @Override
    public String getHudPrefix() {
        return mode.get();
    }

    public void onChangeSpeedMode(String name) {
        if (currentSpeedMode != null) currentSpeedMode.onDisable();

        switch (name) {
            case "Grim": currentSpeedMode = new GrimCollide(); break;
            case "FunTime Snow": currentSpeedMode = new FunTimeSnow(); break;
        }

        currentSpeedMode.onEnable();
    }
}