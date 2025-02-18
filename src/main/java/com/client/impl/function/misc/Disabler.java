package com.client.impl.function.misc;

import com.client.event.events.PacketEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.misc.disabler.DisablerMode;
import com.client.impl.function.misc.disabler.aac.AAC;
import com.client.impl.function.misc.disabler.aac.AAC_1910;
import com.client.impl.function.misc.disabler.grim.Grim;
import com.client.impl.function.misc.disabler.other.GrimSpectate;
import com.client.impl.function.misc.disabler.other.GrimVulcanTrident;
import com.client.impl.function.misc.disabler.other.Teleport;
import com.client.impl.function.misc.disabler.verus.Verus;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;

import java.util.List;

public class Disabler extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(
            List.of("AAC", "Grim", "Verus", "Grim+Vulcan", "Teleport")).defaultValue("Grim").callback(this::onChangeSpeedMode).build();

    public final ListSetting grimMode = List().name("Режим Grim").enName("Grim Mode").list(
            List.of("Обычный", "Spectate")).defaultValue("Spectate").visible(() -> mode.get().equals("Grim")).callback(e -> onChangeSpeedMode(mode.get())).build();

    public final ListSetting aacMode = List().name("Режим AAC").enName("AAC Mode").list(
            List.of("1.9.10", "Spoof")).defaultValue("Spoof").visible(() -> mode.get().equals("AAC")).callback(e -> onChangeSpeedMode(mode.get())).build();

    public final IntegerSetting grimDelay = Integer().name("Задержа использования трезубца").enName("Trident Delay").defaultValue(0).min(0).max(20).visible(() -> mode.get().equals("Grim+Vulcan")).build();

    public final ListSetting teleportDirection = List().name("Режим Teleport").enName("Teleport Mode").list(
            List.of("Up", "Down", "Horizontal")).defaultValue("Down").visible(() -> mode.get().equals("Teleport")).callback(e -> onChangeSpeedMode(mode.get())).build();

    public final IntegerSetting delay = Integer().name("Задержа").enName("Delay").defaultValue(20).min(2).max(100).visible(() -> mode.get().equals("Teleport")).build();
    public final BooleanSetting mathGround = Boolean().name("Оффсетать на землю").enName("Math Ground").defaultValue(false).visible(() -> mode.get().equals("Teleport")).build();
    public final BooleanSetting groundState = Boolean().name("Позиция на земле").enName("Ground Spoof").defaultValue(false).visible(() -> mode.get().equals("Teleport")).build();

    public Disabler() {
        super("Disabler", Category.MISC);
    }

    public DisablerMode currentDisablerMode;

    @Override
    public void onEnable() {
        this.onChangeSpeedMode(mode.get());
    }

    @Override
    public void onDisable() {
        currentDisablerMode.onDisable();
    }

    @Override
    public void onPacket(PacketEvent.Send e) {
        currentDisablerMode.onPacket(e);
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        currentDisablerMode.onPacket(e);
    }

    @Override
    public void tick(TickEvent.Pre e) {
        currentDisablerMode.tick(e);
    }

    @Override
    public void sendMovementPackets(SendMovementPacketsEvent event) {
        currentDisablerMode.sendMovementPackets(event);
    }

    public void onChangeSpeedMode(String name) {
        if (currentDisablerMode != null) currentDisablerMode.onDisable();

        switch (name) {
            case "AAC":
                switch (aacMode.get()) {
                    case "1.9.10" -> currentDisablerMode = new AAC_1910();
                    default -> currentDisablerMode = new AAC();
                }
                break;
            case "Grim":
                switch (grimMode.get()) {
                    case "Spectate" -> currentDisablerMode = new GrimSpectate();
                    default -> currentDisablerMode = new Grim();
                }
                break;
            case "Verus": currentDisablerMode = new Verus(); break;
            case "Grim+Vulcan": currentDisablerMode = new GrimVulcanTrident(); break;
            default: currentDisablerMode = new Teleport(); break;
        }

        currentDisablerMode.onEnable();
    }
}
