package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.ListSetting;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class SafeWalk extends Function {
    public final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Обычный", "Легитный")).defaultValue("Обычный").build();

    public SafeWalk() {
        super("Safe Walk", Category.MOVEMENT);
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (mode.get().equals("Обычный")) return;

        Box bounding = mc.player.getBoundingBox();
        bounding = bounding.offset(0, -1, 0);
        bounding = bounding.expand(0.3);
        boolean shouldSneak = false;
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                double xScale = x / 3d + .5;
                double zScale = z / 3d + .5;
                BlockPos current = mc.player.getBlockPos().add(x, -1, z);
                BlockState bs = mc.world.getBlockState(current);
                if (bs.isAir() && bounding.contains(new Vec3d(current.getX() + xScale, current.getY() + 1, current.getZ() + zScale))) {
                    shouldSneak = true;
                    break;
                }
            }
        }
        mc.options.keySneak.setPressed(shouldSneak);
    }
}