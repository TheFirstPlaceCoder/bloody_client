package com.client.system.companion;

import com.client.impl.function.client.Companion;
import com.client.system.function.FunctionManager;
import com.client.utils.Utils;
import com.client.utils.game.rotate.Rotations;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Random;

import static com.client.BloodyClient.mc;

public class DumboOctopusEntity extends WaterCreatureEntity implements IAnimatable {
    private static final TrackedData<Boolean> RESTING;
    private static final TrackedData<Integer> VARIANT;
    private final AnimationFactory factory = new AnimationFactory(this);
    public int restTimer;

    public DumboOctopusEntity(EntityType<? extends DumboOctopusEntity> entityType, World level) {
        super(entityType, level);
        this.moveControl = new FlightMoveControl(this, 0, true);
        this.lookControl = new LookControl(this);
        setNoGravity(true);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 14.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.06D);
    }

    public boolean canImmediatelyDespawn(double distance) {
        return !this.hasCustomName();
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(RESTING, false);
        this.dataTracker.startTracking(VARIANT, 0);
    }

    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putBoolean("Resting", this.isResting());
        compound.putInt("Variant", this.getVariant());
    }

    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setResting(compound.getBoolean("Resting"));
        this.setVariant(compound.getInt("Variant"));
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.5F;
    }

    public void tickMovement() {
        travel();
        if (!this.world.isClient() && this.canMoveVoluntarily()) {
                if (this.isResting()) {
                    if (--this.restTimer <= 0) {
                        this.setResting(false);
                    }

                    this.setVelocity(this.getVelocity().subtract(
                            0.0D, 0.01D, 0.0D));
                } else if (this.random.nextFloat() <= 0.001F) {
                    this.restTimer = this.random.nextInt(200, 601);
                    this.setResting(true);
                }
        }
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
    }

    private static final double RADIUS = 3.5;
    public Companion companion;

    boolean shouldMove = false, shouldReturn = false;
    int delayBetween, delayAfter, moveTicks;

    private void travel() {
        if (mc.player == null) return;

        if (companion == null) companion = FunctionManager.get(Companion.class);

        if (shouldMove) {
            if (delayBetween > 0) {
                delayBetween--;
                return;
            }
        }
        else delayAfter--;

        if (delayAfter <= 0 && !shouldMove) {

            double horS = Utils.random(-companion.yaw, companion.yaw);

            changeYaw(horS);

            delayBetween = shouldReturn ? 0 : companion.delayBetween;
            moveTicks = shouldReturn ? 5 : companion.moveTicks;
            shouldMove = true;
            return;
        }

        if (shouldMove) {
            updatePosition((shouldReturn ? 3.5f * 0.03f : companion.speed));

            if (moveTicks <= 0) {
                delayAfter = shouldReturn ? 0 : companion.yawDelay;
                shouldMove = false;
            } else moveTicks--;
        }

        checkBoundary(mc.player);
    }

    public void changeYaw(double horS) {
        this.yaw = (mc.player.distanceTo(this) > 1.5 || shouldReturn) ? (float) Rotations.getYawTest(this.yaw, this.getX(), this.getZ(), mc.player.getPos()) : (float) (MathHelper.wrapDegrees(this.yaw) + MathHelper.wrapDegrees(horS));
        this.headYaw = (mc.player.distanceTo(this) > 1.5 || shouldReturn) ? (float) Rotations.getYawTest(this.yaw, this.getX(), this.getZ(), mc.player.getPos()) : (float) (MathHelper.wrapDegrees(this.yaw) + MathHelper.wrapDegrees(horS));
        this.prevHeadYaw = (mc.player.distanceTo(this) > 1.5 || shouldReturn) ? (float) Rotations.getYawTest(this.yaw, this.getX(), this.getZ(), mc.player.getPos()) : (float) (MathHelper.wrapDegrees(this.yaw) + MathHelper.wrapDegrees(horS));
        this.serverHeadYaw = (mc.player.distanceTo(this) > 1.5 || shouldReturn) ? (float) Rotations.getYawTest(this.yaw, this.getX(), this.getZ(), mc.player.getPos()) : (float) (MathHelper.wrapDegrees(this.yaw) + MathHelper.wrapDegrees(horS));
        this.serverYaw = (mc.player.distanceTo(this) > 1.5 || shouldReturn) ? (float) Rotations.getYawTest(this.yaw, this.getX(), this.getZ(), mc.player.getPos()) : (float) (MathHelper.wrapDegrees(this.yaw) + MathHelper.wrapDegrees(horS));
    }

    private void updatePosition(float MOVE_SPEED) {
        // Получаем угол в радианах
        double angleInRadians = Math.toRadians(yaw);

        // Обновляем координаты
        this.setPosition(this.getX() - Math.sin(angleInRadians) * MOVE_SPEED,
                this.getY() + getYOffset(),
                this.getZ() + Math.cos(angleInRadians) * MOVE_SPEED);
    }

    public double getYOffset() {
        if ((shouldReturn && this.getY() > mc.player.getY() + mc.player.getHeight() * 0.85) || (mc.player.getY() + mc.player.getHeight() * 1.5 < this.getY())) return -(companion.verSpeed / 10) * 1.5;

        if (this.getY() < mc.player.getY() + mc.player.getHeight() * 0.15) return (companion.verSpeed / 10) * 1.5;

        return (companion.verSpeed / 10);
    }

    private void checkBoundary(PlayerEntity player) {
        double offsetX = this.getX() - player.getX();
        double offsetY = this.getY() - player.getY();
        double offsetZ = this.getZ() - player.getZ();
        double distanceSquared = (offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ);

        // Проверяем, находится ли компаньон внутри шара
        //setPosition(player.getX(), player.getY() + 0.5, player.getZ());
        shouldReturn = Math.sqrt(distanceSquared) > RADIUS;
    }

    protected float changeAngle(float from, float to, float max) {
        float f = MathHelper.subtractAngles(from, to);
        float g = MathHelper.clamp(f, -max, max);
        return from + g;
    }

    protected EntityNavigation createNavigation(World level) {
        return new SwimNavigation(this, level);
    }

    public void travel(Vec3d speed) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(this.getMovementSpeed(), speed);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9D));
        } else {
            super.travel(speed);
        }

    }

    public EntityData initialize(ServerWorldAccess levelAccessor, LocalDifficulty difficultyInstance, SpawnReason mobSpawnType, EntityData spawnGroupData, NbtCompound p_146750_) {
        this.setVariant(this.random.nextInt(0, 4));
        return spawnGroupData;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        //if (this.isResting()) {
        //    event.getController().setAnimation((new AnimationBuilder()).addAnimation("dumbo_octopus_idle", true));
        //} else if (this.isTouchingWater()) {
            event.getController().setAnimation((new AnimationBuilder()).addAnimation("dumbo_octopus_swim", true));
        //} else {
        //    event.getController().setAnimation((new AnimationBuilder()).addAnimation("dumbo_octopus_on_land", true));
        //}

        return PlayState.CONTINUE;
    }

    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 4.0F, this::predicate));
    }

    public AnimationFactory getFactory() {
        return this.factory;
    }

    public boolean isResting() {
        return (Boolean)this.dataTracker.get(RESTING);
    }

    public void setResting(boolean resting) {
        this.dataTracker.set(RESTING, resting);
    }

    public int getVariant() {
        return (Integer)this.dataTracker.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, MathHelper.clamp(variant, 0, 3));
    }

    static {
        RESTING = DataTracker.registerData(DumboOctopusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        VARIANT = DataTracker.registerData(DumboOctopusEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
}