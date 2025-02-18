package com.client.impl.function.movement;

import com.client.event.events.PlayerMoveEvent;
import com.client.event.events.PlayerTravelEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.impl.function.movement.speedmodes.aac.AAC_438;
import com.client.impl.function.movement.speedmodes.grim.GrimCollide;
import com.client.impl.function.movement.speedmodes.intave.Intave;
import com.client.impl.function.movement.speedmodes.matrix.Matrix_670;
import com.client.impl.function.movement.speedmodes.matrix.OldMatrix;
import com.client.impl.function.movement.speedmodes.ncp.NCP;
import com.client.impl.function.movement.speedmodes.ncp.NCPHop;
import com.client.impl.function.movement.speedmodes.spartan.SpartanYPort;
import com.client.impl.function.movement.speedmodes.verus.VerusFast;
import com.client.impl.function.movement.speedmodes.verus.VerusHop;
import com.client.impl.function.movement.speedmodes.verus.VerusYPort;
import com.client.impl.function.movement.speedmodes.vulcan.VulcanHop;
import com.client.impl.function.movement.speedmodes.vulcan.VulcanLowHop;
import com.client.impl.function.movement.speedmodes.vulcan.Vulcan_286;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;

import java.util.List;

public class Speed extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(
            List.of("AAA", "Grim", "NCP", "AAC", "Matrix", "Vulcan",  "Verus", "Spartan", "Intave")).defaultValue("Grim").callback(this::onChangeSpeedMode).build();

    private final ListSetting ncpMode = List().name("Режим NCP").enName("NCP Mode").list(
            List.of("Hop", "Обычный")).defaultValue("Hop").visible(() -> mode.get().equals("NCP")).callback(e -> onChangeSpeedMode(mode.get())).build();

    private final ListSetting matrixMode = List().name("Режим Matrix").enName("Matrix Mode").list(
            List.of("Old", "6.7.0")).defaultValue("Old").visible(() -> mode.get().equals("Matrix")).callback(e -> onChangeSpeedMode(mode.get())).build();

    private final ListSetting vulcanMode = List().name("Режим Vulcan").enName("Vulcan Mode").list(
            List.of("2.8.6", "Hop", "LowHop")).defaultValue("Hop").visible(() -> mode.get().equals("Vulcan")).callback(e -> onChangeSpeedMode(mode.get())).build();

    private final ListSetting verusMode = List().name("Режим Verus").enName("Verus Mode").list(
            List.of("Hop", "Fast", "Y-Port")).defaultValue("Hop").visible(() -> mode.get().equals("Verus")).callback(e -> onChangeSpeedMode(mode.get())).build();

    public final DoubleSetting expand = Double().name("Оффсет").enName("Box Offset").defaultValue(1.0).min(0).max(1).visible(() -> mode.get().equals("Grim")).build();
    public final IntegerSetting speed = Integer().name("Скорость от игроков").enName("Speed From Players").defaultValue(7).min(0).max(15).visible(() -> mode.get().equals("Grim")).build();
    public final IntegerSetting speedAnimal = Integer().name("Скорость от других").enName("Other Speed").defaultValue(14).min(0).max(20).visible(() -> mode.get().equals("Grim")).build();
    public final BooleanSetting armorStands = Boolean().name("Армор стенды").enName("Armor Stands").defaultValue(true).visible(() -> mode.get().equals("Grim")).build();
    public final BooleanSetting others = Boolean().name("Другие сущности").enName("Other Entities").defaultValue(true).visible(() -> mode.get().equals("Grim")).build();

    public Speed() {
        super("Speed", Category.MOVEMENT);
    }

    public SpeedMode currentSpeedMode;

    @Override
    public void onEnable() {
        Timer.setOverride(Timer.OFF);
        onChangeSpeedMode(mode.get());
    }

    @Override
    public void onDisable() {
        Timer.setOverride(Timer.OFF);
        mc.player.flyingSpeed = 0.02f;
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
        switch (name) {
            case "Grim": currentSpeedMode = new GrimCollide(); break;
            case "NCP":
                switch (ncpMode.get()) {
                    case "Hop" -> currentSpeedMode = new NCPHop();
                    default -> currentSpeedMode = new NCP();
                }
                break;
            case "AAC": currentSpeedMode = new AAC_438(); break;
            case "Matrix":
                switch (matrixMode.get()) {
                    case "Old" -> currentSpeedMode = new OldMatrix();
                    default -> currentSpeedMode = new Matrix_670();
                }
                break;
            case "Vulcan":
                switch (vulcanMode.get()) {
                    case "Hop" -> currentSpeedMode = new VulcanHop();
                    case "LowHop" -> currentSpeedMode = new VulcanLowHop();
                    default -> currentSpeedMode = new Vulcan_286();
                }
                break;
            case "Spartan": currentSpeedMode = new SpartanYPort(); break;
            case "Intave": currentSpeedMode = new Intave(); break;
            case "Verus":
                switch (verusMode.get()) {
                    case "Hop" -> currentSpeedMode = new VerusHop();
                    case "Fast" -> currentSpeedMode = new VerusFast();
                    default -> currentSpeedMode = new VerusYPort();
                }
        }

        currentSpeedMode.onEnable();
    }
}