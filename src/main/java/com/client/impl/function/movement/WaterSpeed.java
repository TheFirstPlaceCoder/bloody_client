package com.client.impl.function.movement;

import api.interfaces.EventHandler;
import com.client.event.events.PlayerMoveEvent;
import com.client.event.events.PlayerTravelEvent;
import com.client.event.events.TickEvent;
import com.client.interfaces.IVec3d;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.Utils;
import com.client.utils.game.movement.MovementUtils;
import mixin.accessor.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;

import java.util.List;

public class WaterSpeed extends Function {
    public WaterSpeed() {
        super("Water Speed", Category.MOVEMENT);
    }

    private final ListSetting mode = List().name("Режим").list(List.of("FunTime")).defaultValue("FunTime").build();

    private float acceleration = 0f;

    @EventHandler
    private void EventMove(PlayerMoveEvent e) {
        if (mc.player.isSwimming()) {
            mc.player.input.movementSideways = 0;
            double[] dirSpeed = MovementUtils.forward(acceleration / 6.3447f);
            ((IVec3d) e.movement).setX(e.movement.getX() + dirSpeed[0] * 0.11);
            ((IVec3d) e.movement).setZ(e.movement.getZ() + dirSpeed[1] * 0.11);
            e.cancel();

            if(Math.abs(mc.player.yaw - mc.player.prevYaw) > 3) acceleration -= 0.1f;
            else acceleration += 0.1;

            acceleration = (float) Utils.clamp(acceleration, 0f, 1f);
        } else acceleration = 0f;
        if (!MovementUtils.isMoving() || mc.player.horizontalCollision || mc.player.verticalCollision) acceleration = 0f;
    }
}