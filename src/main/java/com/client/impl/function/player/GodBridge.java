package com.client.impl.function.player;

import com.client.event.events.TickEvent;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.utils.game.world.BlockUtils;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

public class GodBridge extends Function {
    private final BooleanSetting setClientYaw = Boolean().name("Ставить твой yaw").enName("Use player yaw").defaultValue(false).build();

    public GodBridge() {
        super("God Bridge", Category.PLAYER);
    }

    public Direction[] allowedSides = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP };

    boolean isReady() {
        return mc.player.pitch > 76;
    }

    @Override
    public void tick(TickEvent.Pre event) {
        if (!isReady()) {
            return;
        }

        if (!setClientYaw.get()) mc.player.yaw = mc.player.getMovementDirection().asRotation();

        if (mc.player.pitch > 83) {
            mc.player.pitch = mc.world.getBlockState(mc.player.getBlockPos().down()).isAir() ? 76.5f : 82.5f;
        }

        HitResult hr = mc.crosshairTarget;
        if (hr != null && hr.getType() == HitResult.Type.BLOCK && hr instanceof BlockHitResult result) {
            if (Arrays.stream(allowedSides).anyMatch(direction -> (direction != Direction.UP || !mc.player.isOnGround()) && direction == result.getSide()) && BlockUtils.canPlace(result.getBlockPos().offset(result.getSide()))) {
                mc.execute(() -> {
                    mc.player.swingHand(Hand.MAIN_HAND);
                    mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, result);
                });
            }
        }
    }
}
