package com.client.impl.function.movement;

import com.client.event.events.BlockShapeEvent;
import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.jesus.JesusMode;
import com.client.impl.function.movement.jesus.aac.AAC;
import com.client.impl.function.movement.jesus.aac.AACNew;
import com.client.impl.function.movement.jesus.matrix.MatrixZoom;
import com.client.impl.function.movement.jesus.matrix.OldMatrix;
import com.client.impl.function.movement.jesus.ncp.NCP;
import com.client.impl.function.movement.jesus.other.Fly;
import com.client.impl.function.movement.jesus.spartan.Spartan;
import com.client.impl.function.movement.jesus.verus.Verus;
import com.client.impl.function.movement.jesus.vulcan.Vulcan;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.ListSetting;

import java.util.List;

public class Jesus extends Function {
    public final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("NCP", "AAC", "Matrix", "Universal","Verus", "Vulcan", "Spartan", "Fly")).defaultValue("NCP").callback(this::onChangeSpeedMode).build();

    private final ListSetting matrixMode = List().name("Режим Matrix").enName("Matrix Mode").list(
            List.of("Old", "Zoom")).defaultValue("Zoom").visible(() -> mode.get().equals("Matrix")).callback(e -> onChangeSpeedMode(mode.get())).build();

    public final DoubleSetting speed = Double().name("Скорость по Y").enName("Vertical Offset").defaultValue(0.6).min(0.01).max(2).visible(() -> mode.get().equals("Fly") || mode.get().equals("Vulcan")).build();
    public final BooleanSetting onlyMove = Boolean().name("Только при движении").enName("Only Moving").defaultValue(true).visible(() -> mode.get().equals("Fly")).build();

    public Jesus() {
        super("Jesus", Category.MOVEMENT);
    }

    public JesusMode currentJesusMode;

    @Override
    public void onEnable() {
        onChangeSpeedMode(mode.get());
    }

    @Override
    public void onDisable() {
        Timer.setOverride(Timer.OFF);
    }

    @Override
    public void onBlockState(BlockShapeEvent event) {
        currentJesusMode.onBlockState(event);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        currentJesusMode.tick(event);
    }

    @Override
    public void onPacket(PacketEvent.Send event) {
        currentJesusMode.onPacket(event);
    }

    @Override
    public String getHudPrefix() {
        return mode.get();
    }

    public void onChangeSpeedMode(String name) {
        switch (name) {
            case "NCP": currentJesusMode = new NCP(); break;
            case "AAC": currentJesusMode = new AAC(); break;
            case "Universal": currentJesusMode = new AACNew(); break;
            case "Matrix":
                if (matrixMode.get().equals("Old")) {
                    currentJesusMode = new OldMatrix();
                } else {
                    currentJesusMode = new MatrixZoom();
                }
                break;
            case "Verus": currentJesusMode = new Verus(); break;
            case "Vulcan": currentJesusMode = new Vulcan(); break;
            case "Spartan": currentJesusMode = new Spartan(); break;
            default: currentJesusMode = new Fly(); break;
        }

        currentJesusMode.onEnable();
    }
}
