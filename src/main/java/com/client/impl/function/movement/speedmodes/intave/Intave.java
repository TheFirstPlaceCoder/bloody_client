package com.client.impl.function.movement.speedmodes.intave;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Timer;
import com.client.impl.function.movement.speedmodes.SpeedMode;
import com.client.utils.game.movement.MovementUtils;

public class Intave extends SpeedMode {
    @Override
    public void tick(TickEvent.Pre event) {
        Timer.setOverride(Timer.OFF);

        if (!MovementUtils.isMoving() || mc.player.isInsideWaterOrBubbleColumn() || mc.player.isHoldingOntoLadder() || mc.player.isRiding())
            return;

        if (mc.player.isOnGround())
            mc.player.jump();
        else {
            if (mc.player.fallDistance <= 0.1)
                Timer.setOverride(1.7f);
            else if (mc.player.fallDistance < 1.3)
                Timer.setOverride(0.8f);
            else Timer.setOverride(Timer.OFF);
        }
    }
}
