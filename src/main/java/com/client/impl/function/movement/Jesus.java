package com.client.impl.function.movement;

import com.client.event.events.TickEvent;
import com.client.interfaces.IVec3d;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.utils.game.entity.ServerUtils;
import com.client.utils.game.movement.MovementUtils;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Jesus extends Function {

    public Jesus() {
        super("Jesus", Category.MOVEMENT);
    }

    @Override
    public void tick(TickEvent.Post event) {
        Entity player = mc.player.getRootVehicle();

        if (player.isSneaking() || player.fallDistance > 3f) return;

        double cf = 1.1100000143051147;

        if (isSubmerged(player.getPos().add(0, 0.3, 0))) {
            player.setVelocity(player.getVelocity().x * cf, 0.08, player.getVelocity().z * cf);
        } else if (isSubmerged(player.getPos().add(0, 0.1, 0))) {
            player.setVelocity(player.getVelocity().x * cf, 0.05, player.getVelocity().z * cf);
        } else if (isSubmerged(player.getPos().add(0, 0.05, 0))) {
            player.setVelocity(player.getVelocity().x * cf, 0.01, player.getVelocity().z * cf);
        } else if (isSubmerged(player.getPos())) {
            player.setVelocity(player.getVelocity().x * cf, -0.005, player.getVelocity().z * cf);
            player.setOnGround(true);
        }
    }

    private boolean isSubmerged(Vec3d pos) {
        BlockPos bp = new BlockPos(pos);
        FluidState state = mc.world.getFluidState(bp);

        return !state.isEmpty() && pos.y - bp.getY() <= state.getHeight();
    }
}
