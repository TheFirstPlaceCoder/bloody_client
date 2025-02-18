package com.client.impl.function.misc;

import com.client.event.events.TickEvent;
import com.client.interfaces.IMinecraftClient;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.math.Timer;

import java.util.List;

/**
 * __aaa__
 * 15.01.2024
 * */
public class AutoClicker extends Function {
    public AutoClicker() {
        super("Auto Clicker", Category.MISC);
    }

    private final ListSetting button = List().name("Кнопка").enName("Button").defaultValue("ЛКМ").list(List.of("ПКМ", "ЛКМ", "Обе")).build();
    private final BooleanSetting checkTarget = Boolean().name("Проверять таргет").enName("Check Target").visible(() -> !button.get().equals("ПКМ")).defaultValue(false).build();
    private final ListSetting mode =  List().name("Режим").enName("Mode").defaultValue("Нажать").list(List.of("Зажать", "Нажать")).build();
    private final IntegerSetting delay = Integer().name("Задержа").enName("Delay").defaultValue(16).min(0).max(100).visible(() -> mode.get().equals("Нажать")).build();

    private final Timer timer = new Timer();

    @Override
    public void onEnable() {
        timer.reset();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        timer.tick();
        if (checkTarget.get() && mc.targetedEntity == null) return;
        switch (mode.get()) {
            case "Нажать": {
                switch (button.get()) {
                    case "ПКМ": {
                        timer.resetIfPassed$Task(delay.get(), () -> ((IMinecraftClient) mc).rightClick());
                        break;
                    }
                    case "ЛКМ": {
                        timer.resetIfPassed$Task(delay.get(), () -> ((IMinecraftClient) mc).attack());
                        break;
                    }
                    case "Обе": {
                        timer.resetIfPassed$Task(delay.get(), () -> {
                            ((IMinecraftClient) mc).rightClick();
                            ((IMinecraftClient) mc).attack();
                        });
                        break;
                    }
                }
                break;
            }

            case "Зажать": {
                switch (button.get()) {
                    case "ПКМ": ((IMinecraftClient) mc).rightClick(); break;
                    case "ЛКМ": ((IMinecraftClient) mc).attack(); break;
                    case "Обе": {
                        ((IMinecraftClient) mc).rightClick();
                        ((IMinecraftClient) mc).attack();
                        break;
                    }
                }
                break;
            }
        }
    }
}