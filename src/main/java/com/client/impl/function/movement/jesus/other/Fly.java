package com.client.impl.function.movement.jesus.other;

import com.client.event.events.TickEvent;
import com.client.impl.function.movement.Jesus;
import com.client.impl.function.movement.jesus.JesusMode;
import com.client.interfaces.IVec3d;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.math.MsTimer;
import net.minecraft.util.math.MathHelper;

public class Fly extends JesusMode {
    private final MsTimer timeHelper = new MsTimer();

    @Override
    public void tick(TickEvent.Pre event) {
        if (!mc.player.isTouchingWater()) return;
        if (settings.onlyMove.get()) {
            if (MovementUtils.isMoving()) {
                ((IVec3d) mc.player.getVelocity()).setY(settings.speed.get());
                if (timeHelper.passedMs(200L) && Jesus.mc.player.isSprinting()) {
                    float f = Jesus.mc.player.yaw * ((float) Math.PI / 180);
                    mc.player.addVelocity(-(MathHelper.sin(f) * 0.2f), 0, (double) (MathHelper.cos(f) * 0.2f));
                    MovementUtils.strafe();
                }
            }
        } else {
            ((IVec3d) mc.player.getVelocity()).setY(settings.speed.get());
            if (timeHelper.passedMs(200L) && Jesus.mc.player.isSprinting()) {
                float f = Jesus.mc.player.yaw * ((float) Math.PI / 180);
                mc.player.addVelocity(-(MathHelper.sin(f) * 0.2f), 0, (double) (MathHelper.cos(f) * 0.2f));
                MovementUtils.strafe();
            }
        }
        timeHelper.reset();
    }
}
