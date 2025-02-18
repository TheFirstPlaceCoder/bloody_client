package com.client.impl.function.player;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import net.minecraft.client.gui.screen.DeathScreen;

/**
 * __aaa__
 * 21.05.2024
 * */
public class AutoRespawn extends Function {
    public AutoRespawn() {
        super("Auto Respawn", Category.PLAYER);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.currentScreen instanceof DeathScreen) {
            mc.player.requestRespawn();
        }
    }
}
