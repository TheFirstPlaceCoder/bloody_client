package com.client.impl.function.movement;

import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.math.MathUtils;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class Timer extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Легит", "ХвХ", "Обычный")).defaultValue("Легит").build();
    public final DoubleSetting power = Double().name("Сила").enName("Power").defaultValue(1.7).min(1.1).max(5).build();
    public final IntegerSetting remove = Integer().name("Скорость убывания").enName("Down Speed").defaultValue(7).min(3).max(20).visible(() -> !mode.get().equals("Обычный")).build();

    public Timer() {
        super("Timer", Category.MOVEMENT);
    }

    public int timer;

    public static final float OFF = 1f;
    public static float override = 1F;

    public static void setOverride(float override) {
        Timer.override = override;
    }

    public void update() {
        if (mode.get().equals("Обычный")) return;

        timer = MathHelper.clamp(timer, 0, 100);

        if (isEnabled() && MovementUtils.isMoving()) {
            if (timer > 0) timer -= remove.get();
        } else {
            if (timer >= 100) return;
            if (mode.get().equals("Легит")) {
                if (MovementUtils.getSpeed() > 0) {
                    if (mc.player.age % 16 == 0) {
                        timer += MathUtils.random(3, 6);
                    }
                } else {
                    if (mc.player.age % 4 == 0) {
                        timer += MathUtils.random(2, 5);
                    }
                }
            } else {
                if (MovementUtils.getSpeed() > 0) {
                    if (mc.player.age % 4 == 0) {
                        timer += MathUtils.random(5, 9);
                    }
                } else {
                    if (mc.player.age % 2 == 0) {
                        timer += MathUtils.random(8, 13);
                    }
                }
            }
        }
    }

    public float getPower() {
        if (!canUpdate()) return OFF;
        if (isEnabled() && MovementUtils.isMoving() && (timer > 0 || mode.get().equals("Обычный"))) {
            return power.floatValue();
        }
        if (override != OFF) return override;
        return OFF;
    }
}