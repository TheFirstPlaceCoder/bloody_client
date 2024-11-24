package com.client.impl.function.movement;

import api.interfaces.EventHandler;
import com.client.event.events.BlockCollisionEvent;
import com.client.event.events.PlayerMoveEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.interfaces.IVec3d;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.Utils;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.misc.FunctionUtils;
import mixin.accessor.EntityAccessor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class TestFly extends Function {
    public final IntegerSetting delay = Integer().name("Delay").min(1).max(20).defaultValue(1).build();
    public final DoubleSetting acceler = Double().name("acceler").defaultValue(0.5).min(0.15).max(1).build();

    public TestFly() {
        super("Test Fly", Category.MOVEMENT);
    }

    private float acceleration = 0f;

    @Override
    public void onDisable() {
        Timer.setOverride(Timer.OFF);
    }

    @EventHandler
    private void EventMove(PlayerMoveEvent e) {
        if (mc.player.isSwimming()) {
            mc.player.input.movementSideways = 0;
            double[] dirSpeed = MovementUtils.forward(acceleration / 6.3447f);
            ((IVec3d) e.movement).setX(e.movement.getX() + dirSpeed[0] * 0.11);
            ((IVec3d) e.movement).setZ(e.movement.getZ() + dirSpeed[1] * 0.11);
            e.cancel();

            if(Math.abs(mc.player.yaw - mc.player.prevYaw) > 3) acceleration -= 0.1f;
            else acceleration += acceler.get() / 10;

            acceleration = (float) Utils.clamp(acceleration, 0f, 1f);
        } else acceleration = 0f;
        if (!MovementUtils.isMoving() || mc.player.horizontalCollision || mc.player.verticalCollision) acceleration = 0f;
    }
}