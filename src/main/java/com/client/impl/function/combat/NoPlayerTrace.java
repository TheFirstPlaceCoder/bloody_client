package com.client.impl.function.combat;

import com.client.event.events.PlayerTraceEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;

public class NoPlayerTrace extends Function {
    public NoPlayerTrace() {
        super("No Player Trace", Category.COMBAT);
    }

    @Override
    public void onPlayerTrace(PlayerTraceEvent event) {
        event.cancel();
    }
}
