package com.client.impl.function.movement.jesus.vulcan;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Timer;
import com.client.impl.function.movement.jesus.JesusMode;
import com.client.interfaces.IVec3d;

public class Vulcan extends JesusMode {
    @Override
    public void tick(TickEvent.Pre event) {
        if (mc.player.isInsideWaterOrBubbleColumn()) {
            // One tick speed-up for extra speed
            Timer.setOverride(1.125f);

            ((IVec3d) mc.player.getVelocity()).setY(settings.speed.get());
        } else {
            Timer.setOverride(1.0f);
        }
    }
}
