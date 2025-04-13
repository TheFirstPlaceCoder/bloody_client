package com.client.impl.function.combat.aura.rotate.handler;

import com.client.impl.function.combat.aura.rotate.handler.handlers.*;
import com.client.impl.function.combat.aura.rotate.handler.handlers.grim.GrimRotationsHandler;
import com.client.impl.function.combat.aura.rotate.handler.handlers.matrix.MatrixRotationsHandler;
import com.client.impl.function.combat.aura.rotate.handler.handlers.vulcan.VulcanHandler;

import java.util.ArrayList;
import java.util.List;

public class Handlers {
    private static final List<Handler> FUNCTION_LIST = new ArrayList<>();

    public static void init() {
        register(new FunTimeRotationsHandler());
        register(new GrimRotationsHandler());
        register(new MatrixRotationsHandler());
        register(new VulcanHandler());
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