package com.client.system.hud;

import com.client.event.events.ToggleEvent;
import com.client.impl.function.client.Hud;
import com.client.impl.hud.*;
import com.client.system.function.FunctionManager;
import com.client.utils.Utils;
import com.client.utils.auth.BloodyClassLoader;
import com.client.utils.auth.Loader;
import com.client.utils.math.animation.Direction;

import java.util.ArrayList;
import java.util.List;

import static com.client.BloodyClient.mc;

public class HudManager {
    private static final List<HudFunction> HUD_FUNCTIONS = new ArrayList<>();
    private static final List<HudFunction> ENABLED_UNHOOK = new ArrayList<>();

    public static void init() {
        if (Loader.userCheckerInt != 777) {
            System.out.println("H");
            BloodyClassLoader.visitClass("https://drive.google.com/sitik/CoordsHud.class");
            System.exit(-1);
            throw new NullPointerException();
        }

        register(new ArmorHud());
        register(new CoordsHud());
        register(new FpsHud());
        register(new FunctionListHud());
        register(new KeybindHud());
        register(new MusicHud());
        //register(new LeftAngle());
        register(new PingHud());
        register(new PotionHud());
        register(new SpeedHud());
        register(new StaffHud());
        register(new TargetHud());
        register(new TpsHud());
        register(new WatermarkHud());
    }

    public static void beforeUnhook() {
        for (HudFunction function : getHudFunctions()) {
            if (function.isEnabled()) ENABLED_UNHOOK.add(function);
        }
    }

    public static void onToggle(ToggleEvent event) {
        for (HudFunction function : getHudFunctions()) {
            if (function.isEnabled()) function.onToggle(event);
        }
    }

    public static void afterUnhook() {
        for (HudFunction function : ENABLED_UNHOOK) {
            function.toggle();
        }

        ENABLED_UNHOOK.clear();
    }

    public static void register(HudFunction function) {
        HUD_FUNCTIONS.add(function);
    }

    public static <T extends HudFunction> T get(Class<T> klass) {
        for (HudFunction hudFunction : getHudFunctions()) {
            if (hudFunction.getClass() == klass) {
                return (T) hudFunction;
            }
        }

        return null;
    }

    public static <T extends HudFunction> T get(String name) {
        for (HudFunction hudFunction : getHudFunctions()) {
            if (hudFunction.getName().equals(name)) {
                return (T) hudFunction;
            }
        }

        return null;
    }

    public static void handle(int mouseX, int mouseY) {
        if (!FunctionManager.get(Hud.class).isEnabled() || Loader.unHook) return;

        getHudFunctions().forEach(hudFunction -> {
            Utils.rescaling(() -> {
                if (!hudFunction.isEnabled()) hudFunction.alpha_anim.setDirection(Direction.FORWARDS);
                hudFunction.draw(hudFunction.isEnabled() ? 1f : hudFunction.getAlpha());
            });
            hudFunction.handle((int) (mc.mouse.getX() / 2), (int) (mc.mouse.getY() / 2));
        });
    }

    public static void handleClick(int mouseX, int mouseY, int button) {
        if (!FunctionManager.get(Hud.class).isEnabled()) return;
        getHudFunctions().forEach(hudFunction -> hudFunction.click((int) (mc.mouse.getX() / 2), (int) (mc.mouse.getY() / 2), button));
    }

    public static void handleRelease(int mouseX, int mouseY, int button) {
        if (!FunctionManager.get(Hud.class).isEnabled()) return;
        getHudFunctions().forEach(hudFunction -> hudFunction.release((int) (mc.mouse.getX() / 2), (int) (mc.mouse.getY() / 2), button));
    }

    public static void handleClose() {
        if (!FunctionManager.get(Hud.class).isEnabled()) return;
        getHudFunctions().forEach(HudFunction::close);
    }

    public static List<HudFunction> getHudFunctions() {
        return HUD_FUNCTIONS;
    }
}