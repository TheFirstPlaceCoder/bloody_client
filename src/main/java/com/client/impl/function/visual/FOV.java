package com.client.impl.function.visual;

import com.client.event.events.Render3DEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;

public class FOV extends Function {
    private final IntegerSetting value = Integer().name("Значение").enName("Value").defaultValue(100).min(1).max(179).build();

    public FOV() {
        super("FOV", Category.VISUAL);
    }

    private double fov;

    @Override
    public void onEnable() {
        fov = mc.options.fov;
        mc.options.fov = value.get();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (value.get() != mc.options.fov) {
            mc.options.fov = value.get();
        }
    }

    @Override
    public void onDisable() {
        mc.options.fov = fov;
    }
}