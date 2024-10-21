package com.client.impl.function.movement;

import com.client.event.events.PlayerMoveEvent;
import com.client.event.events.PlayerTravelEvent;
import com.client.event.events.TickEvent;
import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.interfaces.IVec3d;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.movement.MovementUtils;
import com.client.utils.math.MsTimer;
import com.client.utils.misc.FunctionUtils;
import com.client.utils.misc.speedo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.vehicle.BoatEntity;

import java.util.List;

public class Speed extends Function {
    private final ListSetting mode = List().name("Режим").list(
            List.of("HolyWorld", "ReallyWorld", "FunTime", "NCP", "FunTime 2")).defaultValue("HolyWorld").build();

    public Speed() {
        super("Speed", Category.MOVEMENT);
    }

    MsTimer timer = new MsTimer();
    boolean boosting;
    public float currentPlayerSpeed = 0;
    float prevForward = 0;
    int stage, ticks;
    double baseSpeed;

    @Override
    public void onEnable() {
        FunctionUtils.isHwSpeed = hw();
        Timer.setOverride(Timer.OFF);
        timer.reset();
        boosting = false;
        currentPlayerSpeed = 0;
        stage = 1;
        ticks = 0;
        baseSpeed = 0.2873D;
        if (mode.get().equals("FunTime 2")) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "Режим бустит только от полублоков!", 4000), NotificationManager.NotifType.Info);
        }
    }

    @Override
    public void onDisable() {
        FunctionUtils.isHwSpeed = false;
        Timer.setOverride(Timer.OFF);
    }

    float prevPitch;

    @Override
    public void tick(TickEvent.Post e) {
        if (mode.get().equals("NCP")) currentPlayerSpeed = (float) Math.hypot(mc.player.getX() - mc.player.prevX, mc.player.getZ() - mc.player.prevZ);
    }

    public boolean hw() {
        return isEnabled() && mode.get().equals("HolyWorld");
    }

    @Override
    public void onPlayerTravelEvent(PlayerTravelEvent e) {
        if ((mode.get().equals("ReallyWorld")) && !e.pre && MovementUtils.isMoving()) {
            int collisions = 0;

            for (Entity ent : mc.world.getEntities()) {
                if (ent != mc.player && (ent instanceof LivingEntity || ent instanceof ArmorStandEntity || ent instanceof BoatEntity) && mc.player.getBoundingBox().expand(1.1F).intersects(ent.getBoundingBox())) {
                    collisions++;
                }
            }

            double[] addXZ = MovementUtils.forward(RotationHandler.serverYaw, (0.08F * collisions) * 1.18999F);
            mc.player.addVelocity(addXZ[0], 0.0, addXZ[1]);
        }
    }

    @Override
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        switch (mode.get()) {
            case "FunTime" -> {
                if (MovementUtils.isMoving() && mc.player.isOnGround() && mc.player.isTouchingWater()) {
                    mc.player.jump();
                }

                if (mc.player.isOnGround()) {
                    Timer.setOverride(0.821F);
                }

                if ((double) mc.player.fallDistance > 0.1D && mc.player.fallDistance < 1.0F) {
                    Timer.setOverride(1.0F + (1.0F - (float) Math.floorMod(2L, 2L)));
                }

                if (mc.player.fallDistance >= 1.0F) {
                    Timer.setOverride(0.91F);
                }
            }

            case "HolyWorld" -> {
                if (MovementUtils.isMoving() && !MovementUtils.isInLiquid() && FunctionUtils.playerSpeed < getMaxSpeed().getSpeed()) {

                    float const_ = (float) ((!mc.player.isOnGround() || mc.options.keyJump.isPressed() ? 1.0003F : 1.1009F) * getMaxSpeed().getFactor());

                    ((IVec3d) event.movement).set(
                            mc.player.getVelocity().getX() * const_,
                            mc.player.getVelocity().getY(),
                            mc.player.getVelocity().getZ() * const_
                    );

                    event.cancel();
                }
            }

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

            case "FunTime 2" -> {
                FtSlab();
            }

            default -> {}
        }
    }

    public void FtSlab() {
        if (!mc.player.isTouchingWater() && mc.player.getPos().y % 1.0 == 0.5 && mc.player.isOnGround() && mc.options.keyJump.isPressed()) {
            for(float i = 1; i <= 5; ++i) {
                mc.player.jump();
            }
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

    public speedo getMaxSpeed() {
        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            return switch (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier()) {
                case 0 -> new speedo(9.0, 1.0);
                default ->  new speedo(15.0, 1.02);
                //default -> new spedo(15.0, 1.06);
            };
        } else return new speedo(6.9, 1.0);
    }
}