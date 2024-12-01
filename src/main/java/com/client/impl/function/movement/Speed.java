package com.client.impl.function.movement;

import com.client.event.events.PlayerMoveEvent;
import com.client.event.events.PlayerTravelEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.interfaces.IVec3d;
import com.client.system.companion.DumboOctopusEntity;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.BooleanSetting;
import com.client.system.setting.settings.DoubleSetting;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.math.MsTimer;
import com.client.utils.misc.FunctionUtils;
import com.client.utils.misc.speedo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;

import java.util.List;

public class Speed extends Function {
    private final ListSetting mode = List().name("Режим").list(
            List.of("FunTime", "NCP")).defaultValue("FunTime").build();

    public final DoubleSetting expand = Double().name("Оффсет").defaultValue(1.0).min(0).max(1).visible(() -> mode.get().equals("FunTime")).build();
    public final IntegerSetting speed = Integer().name("Скорость от игроков").defaultValue(7).min(0).max(15).visible(() -> mode.get().equals("FunTime")).build();
    public final IntegerSetting speedAnimal = Integer().name("Скорость от других").defaultValue(14).min(0).max(20).visible(() -> mode.get().equals("FunTime")).build();
    public final BooleanSetting armorStands = Boolean().name("Армор стенды").defaultValue(true).visible(() -> mode.get().equals("FunTime")).build();
    public final BooleanSetting others = Boolean().name("Другие сущности").defaultValue(true).visible(() -> mode.get().equals("FunTime")).build();

    public Speed() {
        super("Speed", Category.MOVEMENT);
    }

    public float currentPlayerSpeed = 0;
    float prevForward = 0;
    int stage, ticks;
    double baseSpeed;

    @Override
    public void onEnable() {
        Timer.setOverride(Timer.OFF);
        currentPlayerSpeed = 0;
        stage = 1;
        ticks = 0;
        baseSpeed = 0.2873D;
    }

    @Override
    public void onDisable() {
        Timer.setOverride(Timer.OFF);
    }

    @Override
    public void tick(TickEvent.Post e) {
        if (mode.get().equals("NCP")) currentPlayerSpeed = (float) Math.hypot(mc.player.getX() - mc.player.prevX, mc.player.getZ() - mc.player.prevZ);
    }

    @Override
    public void onPlayerTravelEvent(PlayerTravelEvent e) {
        if (mode.get().equals("FunTime") && !e.pre && MovementUtils.isMoving()) {
            int collisions = 0;
            int otherCollisions = 0;
            for (Entity ent : mc.world.getEntities())
                if (ent != mc.player && !(ent instanceof DumboOctopusEntity) && (ent instanceof PlayerEntity || (ent instanceof LivingEntity && !(ent instanceof ArmorStandEntity) && others.get()) || (ent instanceof ArmorStandEntity && armorStands.get())) && mc.player.getBoundingBox().expand(expand.get()).intersects(ent.getBoundingBox())) {
                    if (ent instanceof PlayerEntity) collisions++;
                    else otherCollisions++;
                }

            double[] motion = MovementUtils.forward((collisions > 0 ? speed.get() / 100d : speedAnimal.get() / 100d) * (collisions + otherCollisions));
            mc.player.addVelocity(motion[0], 0.0, motion[1]);
        }
    }

    @Override
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        switch (mode.get()) {
            case "NCP" -> {
                if (MovementUtils.isMoving()) {
                    float currentSpeed = mc.player.input.movementForward <= 0 && prevForward > 0 ? currentPlayerSpeed * 0.66f : currentPlayerSpeed;
                    if (stage == 1 && mc.player.isOnGround()) {
                        mc.player.setVelocity(mc.player.getVelocity().x, MovementUtils.getJumpSpeed(), mc.player.getVelocity().z);
                        ((IVec3d) event.movement).setY(MovementUtils.getJumpSpeed());
                        baseSpeed *= 2.149;
                        stage = 2;
                    } else if (stage == 2) {
                        baseSpeed = currentSpeed - (0.66 * (currentSpeed - getBaseMoveSpeed()));
                        stage = 3;
                    } else {
                        if ((mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0, mc.player.getVelocity().getY(), 0.0)).iterator().hasNext() || mc.player.verticalCollision))
                            stage = 1;
                        baseSpeed = currentSpeed - currentSpeed / 159.0D;
                    }

                    baseSpeed = Math.max(baseSpeed, getBaseMoveSpeed());

                    double ncpSpeed = mc.player.input.movementForward < 1 ? 0.465 : 0.576;
                    double ncpBypassSpeed = mc.player.input.movementForward < 1 ? 0.44 : 0.57;

                    if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
                        double amplifier = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
                        ncpSpeed *= 1 + (0.2 * (amplifier + 1));
                        ncpBypassSpeed *= 1 + (0.2 * (amplifier + 1));
                    }

                    if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
                        double amplifier = mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
                        ncpSpeed /= 1 + (0.2 * (amplifier + 1));
                        ncpBypassSpeed /= 1 + (0.2 * (amplifier + 1));
                    }

                    baseSpeed = Math.min(baseSpeed, ticks > 25 ? ncpSpeed : ncpBypassSpeed);

                    if (ticks++ > 50)
                        ticks = 0;

                    MovementUtils.modifyEventSpeed(event, baseSpeed);
                    prevForward = mc.player.input.movementForward;
                } else {
                    ((IVec3d) event.movement).setX(0);
                    ((IVec3d) event.movement).setZ(0);
                }

                event.cancel();
            }

            default -> {}
        }
    }

    public static double getBaseMoveSpeed() {
        int n;
        double d = 0.2873;

        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            n = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
            d *= 1.0 + 0.2 * (n + 1);
        }
        if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            n = mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
            d /= 1.0 + 0.2 * (n + 1);
        }
        if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
            n = mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
            d /= 1.0 + (0.2 * (n + 1));
        }
        return d;
    }
}