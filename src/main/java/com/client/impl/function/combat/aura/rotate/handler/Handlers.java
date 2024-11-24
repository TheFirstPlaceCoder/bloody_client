package com.client.impl.function.combat.aura.rotate.handler;

import com.client.impl.function.combat.aura.rotate.handler.handlers.FunTimeRotationsHandler;
import com.client.utils.auth.BloodyClassLoader;
import com.client.utils.auth.Loader;

import java.util.ArrayList;
import java.util.List;

public class Handlers {
    private static final List<Handler> FUNCTION_LIST = new ArrayList<>();

    public static void init() {
        register((Handler) BloodyClassLoader.visitHandlerClass("https://bloodyhvh.site/loader/handlers/EmptyHandler.php?hwid=" + Loader.hwid));
        register((Handler) BloodyClassLoader.visitHandlerClass("https://bloodyhvh.site/loader/handlers/HolyWorldRotationsHandler.php?hwid=" + Loader.hwid));
        register((Handler) BloodyClassLoader.visitHandlerClass("https://bloodyhvh.site/loader/handlers/ReallyWorldRotationsHandler.php?hwid=" + Loader.hwid));
        register(new FunTimeRotationsHandler());
    }

    public static <T extends Handler> T get(String name) {
        for (Handler function : FUNCTION_LIST) {
            if (function.name == name) {
                return (T) function;
            }
        }

        return null;
    }

    public static void register(Handler function) {
        FUNCTION_LIST.add(function);
    }
}