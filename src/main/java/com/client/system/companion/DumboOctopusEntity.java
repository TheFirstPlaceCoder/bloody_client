package com.client.system.companion;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
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

public class DumboOctopusEntity extends WaterCreatureEntity implements IAnimatable {
    private static final TrackedData<Boolean> RESTING;
    private static final TrackedData<Integer> VARIANT;
    private final AnimationFactory factory = new AnimationFactory(this);
    public int restTimer;

    public DumboOctopusEntity(EntityType<? extends DumboOctopusEntity> entityType, World level) {
        super(entityType, level);
        this.moveControl = new DumboOctopusEntity.DumboOctopusMoveControl();
        this.lookControl = new LookControl(this);
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

    protected void initGoals() {
        this.goalSelector.add(0, new DumboOctopusEntity.RandomSwimmingGoal(this, 1.0D, 40));
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

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient() && this.canMoveVoluntarily()) {
                if (this.isResting()) {
                    if (--this.restTimer <= 0) {
                        this.setResting(false);
                    }

                    this.setVelocity(this.getVelocity().subtract(0.0D, 0.01D, 0.0D));
                } else if (this.random.nextFloat() <= 0.001F) {
                    this.restTimer = this.random.nextInt(200, 601);
                    this.setResting(true);
                }
        }
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

    class DumboOctopusMoveControl extends MoveControl {
        public DumboOctopusMoveControl() {
            super(DumboOctopusEntity.this);
        }

        public void tick() {
            if (!DumboOctopusEntity.this.isResting()) {
                super.tick();
            }
        }
    }

    static class RandomSwimmingGoal extends SwimAroundGoal {
        private final DumboOctopusEntity dumboOctopus;

        public RandomSwimmingGoal(DumboOctopusEntity dumboOctopus, double speedModifier, int interval) {
            super(dumboOctopus, speedModifier, interval);
            this.dumboOctopus = dumboOctopus;
        }

        public boolean canStart() {
            return !this.dumboOctopus.isResting() && super.canStart();
        }
    }
}