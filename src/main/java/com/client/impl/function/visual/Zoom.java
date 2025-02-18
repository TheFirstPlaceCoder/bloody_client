package com.client.impl.function.visual;

import api.interfaces.EventHandler;
import com.client.event.events.KeybindSettingEvent;
import com.client.event.events.Render3DEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.KeybindSetting;
import com.client.utils.math.animation.AnimationUtils;
import mixin.accessor.GameRendererAccessor;

/**
 * __aaa__
 * 19.05.2024
 * */
public class Zoom extends Function {
    public Zoom() {
        super("Zoom", Category.VISUAL);
    }

    private final KeybindSetting bind = Keybind().name("Бинд").enName("Bind").defaultValue(-1).build();
    private final DoubleSetting zoom = Double().name("Зум").enName("Zoom").defaultValue(2.5).min(1.1).max(10.0).build();
    private final BooleanSetting cinematic = Boolean().name("Плавная камера").enName("Smooth").defaultValue(true).build();

    private float v1, v2;
    private int flag;
    private boolean used;

    @Override
    public void onEnable() {
        v1 = 1F;
        v2 = 1F;
        flag = 1;
    }

    @Override
    public void onDisable() {
        ((GameRendererAccessor) mc.gameRenderer).setZoom(1F);
    }

    @EventHandler
    private void onKeybindSettingEvent(KeybindSettingEvent event) {
        if (!bind.key(event.key, !event.mouse)) return;
        used = true;

        switch (event.action) {
            case PRESS, REPEAT -> {
                v2 = zoom.floatValue();

                if (cinematic.get()) {
                    if (flag == 1) {
                        flag = mc.options.smoothCameraEnabled ? 2 : 3;
                    }

                    mc.options.smoothCameraEnabled = true;
                }
            }

            case RELEASE -> {
                v2 = 1F;

                if (cinematic.get()) {
                    mc.options.smoothCameraEnabled = flag == 2;
                }
            }
        }
    }

    @EventHandler
    private void onRender3DEvent(Render3DEvent event) {
        if (mc.currentScreen != null) {
            v2 = 1F;

            if (cinematic.get()) {
                mc.options.smoothCameraEnabled = flag == 2;
            }
        }

        if (used) {
            ((GameRendererAccessor) mc.gameRenderer).setZoom(v1 = AnimationUtils.fast(v1, v2, 20));
        }
    }
}