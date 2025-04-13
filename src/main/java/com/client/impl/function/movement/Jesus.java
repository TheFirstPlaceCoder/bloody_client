package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.math.MsTimer;
import com.client.utils.misc.FunctionUtils;
import net.minecraft.block.Blocks;

import java.util.List;

public class Jesus extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("FunTime")).defaultValue("FunTime").build();

    public Jesus() {
        super("Jesus", Category.MOVEMENT);
    }

    private MsTimer time = new MsTimer();

    @Override
    public void tick(TickEvent.Pre e) {
        if (FunctionUtils.playerSpeed >= 5) return;

        if ((mc.world.getBlockState(mc.player.getBlockPos().up()).getBlock() != Blocks.WATER && mc.options.keyJump.isPressed()) || !mc.player.isTouchingWater()) {
            time.reset();
        }

        if (mc.player.isTouchingWater() && time.passedMs(160)) {
            float ySpeed = mc.options.keyJump.isPressed() ? 0.05f : mc.options.keySneak.isPressed() ? -0.05f : !mc.player.isSprinting() ? 0.005f : 0;
            mc.player.input.sneaking = false;

            float acceletion = mc.player.isSprinting() ? 1.02f : 1.15f;
            mc.player.setVelocity(mc.player.getVelocity().x * acceletion, mc.player.getVelocity().y + ySpeed, mc.player.getVelocity().z * acceletion);
        }
    }
}
