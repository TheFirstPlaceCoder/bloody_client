package com.client.system.macro;

import api.interfaces.EventHandler;
import com.client.event.events.KeyEvent;
import com.client.event.events.TickEvent;
import com.client.utils.auth.Loader;
import com.client.utils.misc.InputUtils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.client.BloodyClient.mc;

public class Macros {
    public static List<Macro> macros = new CopyOnWriteArrayList<>();

    @EventHandler
    public void onKey(KeyEvent event) {
        if (event.action == InputUtils.Action.PRESS && (mc.currentScreen == null || mc.currentScreen instanceof InventoryScreen))
            macros.stream().filter(e -> event.key == e.button).forEach(Macro::runCommand);
    }

    public static List<Macro> getMacros() {
        return macros;
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

    public static void save(BufferedWriter writer) {
        try {
            writer.write("macroses{\n");
            for (Macro macro : getMacros()) {
                writer.write(macro.name + ":" + macro.command + ":" + macro.button + "\n");
            }
            writer.write("}\n");
        } catch (IOException ignore) {
        }
    }

    public static void load(List<String> strings) {
        clear();
        boolean target = false;
        for (String string : strings) {
            if (string.startsWith("}") && target)
                break;
            if (target) {
                String name = string.split(":")[0], command = string.split(":")[1], button = string.split(":")[2];

                add(new Macro(name, command, Integer.parseInt(button)));
            } else {
                if (string.startsWith("macroses")) {
                    target = true;
                }
            }
        }
    }
}