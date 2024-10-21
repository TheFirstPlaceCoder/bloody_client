package com.client.system.macro;

import api.interfaces.EventHandler;
import com.client.event.events.KeyEvent;
import com.client.utils.misc.InputUtils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

import java.util.ArrayList;
import java.util.List;

import static com.client.BloodyClient.mc;

public class Macros {
    public static List<Macro> macros = new ArrayList<>();

    @EventHandler
    public void onKey(KeyEvent event) {
        if (event.action == InputUtils.Action.PRESS && (mc.currentScreen == null || mc.currentScreen instanceof InventoryScreen))
            macros.stream().filter(e -> event.key == e.button).forEach(Macro::runCommand);
    }

    public static void add(Macro macro) {
        macros.add(macro);
    }

    public static boolean contains(String name) {
        return macros.stream().anyMatch(e -> e.name.equals(name));
    }

    public static void remove(String name) {
        macros.removeIf(e -> e.name.equals(name));
    }

    public static void clear() {
        macros.clear();
    }
}