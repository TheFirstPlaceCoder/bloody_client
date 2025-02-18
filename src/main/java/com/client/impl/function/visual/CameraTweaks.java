package com.client.impl.function.visual;

import api.interfaces.EventHandler;
import com.client.event.events.ChangePerspectiveEvent;
import com.client.event.events.MouseScrollEvent;
import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.utils.auth.Loader;
import net.minecraft.client.option.Perspective;

public class CameraTweaks extends Function {
    public final BooleanSetting noCameraClip = Boolean().name("Камера клип").enName("Camera Clip").defaultValue(true).build();
    public final DoubleSetting cameraDistance = Double().name("Дистанция до игрока").enName("F5 Player Distance").defaultValue(4.0).min(0).max(10).build();
    public final BooleanSetting zoomMouse = Boolean().name("Приближение колесиком").enName("Mouse Wheel Add").defaultValue(true).build();
    public final DoubleSetting distanceScroll = Double().name("Сила приближения").enName("Add Power").defaultValue(1.0).min(0.1).max(5).visible(zoomMouse::get).build();

    public CameraTweaks() {
        super("Camera Tweaks", Category.VISUAL);
    }

    public double distance;

    @Override
    public void onEnable() {
        distance = cameraDistance.get();
    }

    @EventHandler
    private void onPerspectiveChanged(ChangePerspectiveEvent event) {
        distance = cameraDistance.get();
    }

    @EventHandler
    private void onMouseScroll(MouseScrollEvent event) {
        if (mc.options.getPerspective() == Perspective.FIRST_PERSON || mc.currentScreen != null) return;
        if (zoomMouse.get()) {
            distance += event.value * 0.25 * (distanceScroll.get() * distance);

            event.cancel();
        }
    }

    public boolean clip() {
        return !Loader.unHook && isEnabled() && noCameraClip.get();
    }

    public double getDistance() {
        return !Loader.unHook && isEnabled() ? distance : 4;
    }
}