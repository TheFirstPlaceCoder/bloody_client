package com.client.impl.function.movement;

import com.client.event.events.AttackEntityEvent;
import com.client.event.events.PacketEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.movement.velocity.VelocityMode;
import com.client.impl.function.movement.velocity.aac.AAC;
import com.client.impl.function.movement.velocity.grim.GrimPosition;
import com.client.impl.function.movement.velocity.grim.NewGrim;
import com.client.impl.function.movement.velocity.grim.OldGrim;
import com.client.impl.function.movement.velocity.grim.StandartGrim;
import com.client.impl.function.movement.velocity.intave.Intave;
import com.client.impl.function.movement.velocity.matrix.MatrixReduce;
import com.client.impl.function.movement.velocity.matrix.OldMatrix;
import com.client.impl.function.movement.velocity.other.FunTime;
import com.client.impl.function.movement.velocity.other.Jump;
import com.client.impl.function.movement.velocity.other.Vanilla;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;

import java.util.List;

public class Velocity extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("AAC", "Matrix", "Grim", "Intave", "FunTime", "Прыжок", "Ванильный")).defaultValue("Ванильный").callback(this::onChangeSpeedMode).build();

    public final DoubleSetting horGroundOffset = Double().name("horGroundOffset").enName("horGroundOffset").defaultValue(0.62).min(0).max(1).c(2).visible(() -> mode.get().equals("FunTime")).build();
    public final DoubleSetting horAirOffset = Double().name("horAirOffset").enName("horAirOffset").defaultValue(0.62).min(0).max(1).c(2).visible(() -> mode.get().equals("FunTime")).build();

    private final ListSetting modeGrim = List().name("Режим Grim").enName("Grim Mode").list(List.of("Old", "Standart", "New", "Teleport")).defaultValue("New").visible(() -> mode.get().equals("Grim")).callback(e -> onChangeSpeedMode(mode.get())).build();
    public final IntegerSetting delay = Integer().name("delay").defaultValue(4).min(0).max(10).visible(() -> mode.get().equals("Grim") && modeGrim.get().equals("New")).build();
    public final IntegerSetting repeats = Integer().name("Повторения").enName("Repeats").defaultValue(4).min(1).max(5).visible(() -> mode.get().equals("Grim") && modeGrim.get().equals("New")).build();

    private final ListSetting modeMatrix = List().name("Режим Matrix").enName("Matrix Mode").list(List.of("Old", "Reduce")).defaultValue("Reduce").visible(() -> mode.get().equals("Matrix")).callback(e -> onChangeSpeedMode(mode.get())).build();

    public final DoubleSetting reducing = Double().name("Множитель").enName("Velocity Reduce").defaultValue(0.62).min(0).max(1).c(2).visible(() -> mode.get().equals("AAC") || mode.get().equals("Intave")).build();

    public final BooleanSetting beforeJump = Boolean().name("Перед прыжком").enName("Reset Before Jump").defaultValue(true).visible(() -> mode.get().equals("Прыжок")).build();

    public final BooleanSetting pauseInFluids = Boolean().name("Пауза в жидкостях").enName("Liquid Pause").defaultValue(true).build();
    public final BooleanSetting fire = Boolean().name("Пауза в огне").enName("Fire Pause").defaultValue(true).build();

    public Velocity() {
        super("Velocity", Category.MOVEMENT);
    }

    public VelocityMode currentVelocityMode;

    @Override
    public void onEnable() {
        this.onChangeSpeedMode(mode.get());
    }

    @Override
    public void onPacket(PacketEvent.Receive e) {
        if (mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava()) && pauseInFluids.get())
            return;

        if (mc.player != null && mc.player.isOnFire() && fire.get() && (mc.player.hurtTime > 0)) {
            return;
        }

        currentVelocityMode.onPacket(e);
    }

    @Override
    public void onPacket(PacketEvent.Send e) {
        if (mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava()) && pauseInFluids.get())
            return;

        if (mc.player != null && mc.player.isOnFire() && fire.get() && (mc.player.hurtTime > 0)) {
            return;
        }

        currentVelocityMode.onPacket(e);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava()) && pauseInFluids.get())
            return;

        if (mc.player != null && mc.player.isOnFire() && fire.get() && (mc.player.hurtTime > 0)) {
            return;
        }

        currentVelocityMode.tick(event);
    }

    @Override
    public void onAttackEntityEvent(AttackEntityEvent.Pre event) {
        currentVelocityMode.onAttack(event);
    }

    public void onChangeSpeedMode(String name) {
        switch (name) {
            case "AAC": currentVelocityMode = new AAC(); break;
            case "Matrix":
                switch (modeMatrix.get()) {
                    case "Old" -> currentVelocityMode = new OldMatrix();
                    default -> currentVelocityMode = new MatrixReduce();
                }
                break;
            case "Grim":
                switch (modeGrim.get()) {
                    case "Old" -> currentVelocityMode = new OldGrim();
                    case "Standart" -> currentVelocityMode = new StandartGrim();
                    case "New" -> currentVelocityMode = new NewGrim();
                    default -> currentVelocityMode = new GrimPosition();
                }
                break;
            case "Intave": currentVelocityMode = new Intave(); break;
            case "Прыжок": currentVelocityMode = new Jump(); break;
            case "FunTime": currentVelocityMode = new FunTime(); break;
            default: currentVelocityMode = new Vanilla(); break;
        }

        currentVelocityMode.onEnable();
    }

    @Override
    public String getHudPrefix() {
        return mode.get();
    }
}