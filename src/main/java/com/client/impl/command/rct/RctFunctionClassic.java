package com.client.impl.command.rct;

import api.interfaces.EventHandler;
import api.main.EventUtils;
import com.client.event.events.TickEvent;
import com.client.utils.game.entity.ServerUtils;

import static com.client.system.function.Function.mc;

public class RctFunctionClassic {
    private static RctFunctionClassic rctFunctionClassic;

    private long time = 0;
    public int an;
    public boolean speedrun;

    public static void register() {
        register(ServerUtils.getAnarchyID());
    }

    public static void register(int an) {
        rctFunctionClassic = new RctFunctionClassic();
        rctFunctionClassic.an = ServerUtils.getAnarchyID();
        rctFunctionClassic.speedrun = ServerUtils.getAnarchy().contains("Спидран");
        EventUtils.register(rctFunctionClassic);
    }

    public static void unregister() {
        EventUtils.unregister(rctFunctionClassic);
        rctFunctionClassic = null;
    }

    @EventHandler
    private void onTickEvent(TickEvent.Pre event) {
        String anCommand = "/anarchy " + (speedrun ? "s" : "") + "anarchy";

        if (time == 0) {
            mc.player.sendChatMessage(anCommand + (an == 2 ? 3 : 2));
            time = System.currentTimeMillis() + 500L;
        }

        if (System.currentTimeMillis() > time) {
            mc.player.sendChatMessage(anCommand + an);
            unregister();
        }
    }
}
