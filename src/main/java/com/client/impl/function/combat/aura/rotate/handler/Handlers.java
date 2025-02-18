package com.client.impl.function.combat.aura.rotate.handler;

import com.client.impl.function.combat.aura.rotate.handler.handlers.FunTimeRotationsHandler;
import com.client.utils.auth.BloodyClassLoader;
import com.client.utils.auth.Loader;
import com.client.utils.auth.enums.ClassType;

import java.util.ArrayList;
import java.util.List;

public class Handlers {
    private static final List<Handler> FUNCTION_LIST = new ArrayList<>();

    public static void init() {
        register(new FunTimeRotationsHandler());
//        register(new EmptyHandler());
//        register(new HolyWorldRotationsHandler());
//        register(new ReallyWorldRotationsHandler());
//        register(new CustomInterpolateHandler());
//        register(new VulcanGrimHandler());
//        register(new CustomLinearHandler());

        String emptyHandlerUrl = "https://bloodyhvh.site/webclasses/handlers/EmptyHandler.php?hwid=";
        String holyWorldRotationsHandlerUrl = "https://bloodyhvh.site/webclasses/handlers/HolyWorldRotationsHandler.php?hwid=";
        String reallyWorldRotationsHandlerUrl = "https://bloodyhvh.site/webclasses/handlers/ReallyWorldRotationsHandler.php?hwid=";
        String customInterpolateHandlerUrl = "https://bloodyhvh.site/webclasses/handlers/CustomInterpolateHandler.php?hwid=";
        String vulcanGrimHandlerUrl = "https://bloodyhvh.site/webclasses/handlers/VulcanGrimHandler.php?hwid=";
        String customLinearHandlerUrl = "https://bloodyhvh.site/webclasses/handlers/CustomLinearHandler.php?hwid=";

        register((Handler) BloodyClassLoader.visit(emptyHandlerUrl + Loader.hwid, ClassType.Handler));
        register((Handler) BloodyClassLoader.visit(holyWorldRotationsHandlerUrl + Loader.hwid, ClassType.Handler));
        register((Handler) BloodyClassLoader.visit(reallyWorldRotationsHandlerUrl + Loader.hwid, ClassType.Handler));
        register((Handler) BloodyClassLoader.visit(customInterpolateHandlerUrl + Loader.hwid, ClassType.Handler));
        register((Handler) BloodyClassLoader.visit(vulcanGrimHandlerUrl + Loader.hwid, ClassType.Handler));
        register((Handler) BloodyClassLoader.visit(customLinearHandlerUrl + Loader.hwid, ClassType.Handler));
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