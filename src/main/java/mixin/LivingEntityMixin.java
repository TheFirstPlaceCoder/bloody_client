package mixin;

import com.client.event.events.PlayerJumpEvent;
import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.impl.function.movement.LiquidMovement;
import com.client.impl.function.visual.SwingAnimation;
import com.client.interfaces.ILivingEntity;
import com.client.system.function.FunctionManager;
import com.client.utils.auth.Loader;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.client.BloodyClient.mc;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    @Shadow
    public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow @Nullable
    public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    @Shadow protected abstract float getJumpVelocity();

    @Shadow @Nullable public abstract StatusEffectInstance removeStatusEffectInternal(@Nullable StatusEffect type);

    @Shadow protected double serverYaw;

    @Shadow protected double serverPitch;

    @Shadow protected double serverHeadYaw;
    @Unique private final List<Pair<Long, Vec3d>> move = new ArrayList<>();

    @Override
    public void setServerYaw(float yaw) {
        serverYaw = yaw;
    }

    @Override
    public void setServerPitch(float pitch) {
        serverPitch = pitch;
    }

    @Override
    public void setServerHeadYaw(float yaw) {
        serverHeadYaw = yaw;
    }

    @Override
    public List<Pair<Long, Vec3d>> getMove() {
        return move;
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"isInsideWall"},
            cancellable = true
    )
    protected void isInsideWall(CallbackInfoReturnable<Boolean> cir) {
        if (this.world.isClient) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Redirect(
            method = {"travel"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;isTouchingWater()Z"
            )
    )
    private boolean travelIsTouchingWaterProxy(LivingEntity self) {
        return Loader.isDev() && self.isTouchingWater() && FunctionManager.get(LiquidMovement.class).shouldTrue() ? false : self.isTouchingWater();
    }

    @Redirect(
            method = {"tickMovement"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;isInLava()Z"
            )
    )
    private boolean tickMovementIsInLavaProxy(LivingEntity self) {
        if (Loader.isDev() && self.isInLava() && FunctionManager.get(LiquidMovement.class).shouldTrue()) {
            return false;
        } else {
            return self.isInLava();
        }
    }

    @Redirect(
            method = {"tickMovement"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;isTouchingWater()Z"
            )
    )
    private boolean tickIsTouchingWaterProxy(LivingEntity self) {
        return Loader.isDev() && self.isTouchingWater() && FunctionManager.get(LiquidMovement.class).shouldTrue() ? false : self.isTouchingWater();
    }

    @Redirect(
            method = {"travel"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;isInLava()Z"
            )
    )
    private boolean travelIsInLavaProxy(LivingEntity self) {
        if (Loader.isDev() && self.isInLava() && FunctionManager.get(LiquidMovement.class).shouldTrue()) {
            return false;
        } else {
            return self.isInLava();
        }
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void travel(Vec3d movementInput, CallbackInfo ci) {
        move.removeIf(s -> System.currentTimeMillis() > s.getLeft() + 100L);
        move.add(new Pair<>(System.currentTimeMillis() + 340L, Renderer3D.getSmoothPos(this)));
    }

    @Inject(method = "getHandSwingDuration", at = @At("HEAD"), cancellable = true)
    private void getHandSwingDuration(CallbackInfoReturnable<Integer> cir) {
        SwingAnimation module = FunctionManager.get(SwingAnimation.class);
        if (module.animation() && this.equals(mc.player)) {
            cir.setReturnValue(module.swingPower.get());
        }
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void jump(CallbackInfo ci) {
        ci.cancel();

        PlayerJumpEvent event = new PlayerJumpEvent();
        event.entity = this;
        event.post();

        float f = this.getJumpVelocity();
        if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            f += 0.1F * (float) (this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1);
        }

        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x, f, vec3d.z);
        if (this.equals(mc.player) && RotationHandler.checkMoveFix()) {
            if (this.isSprinting()) {
                float f1 = RotationHandler.serverYaw * ((float) Math.PI / 180F);
                this.setVelocity(this.getVelocity().add(-MathHelper.sin(f1) * 0.2F, 0.0D, MathHelper.cos(f1) * 0.2F));
            }

            this.velocityDirty = true;
            return;
        }
        if (this.isSprinting()) {
            float g = this.yaw * 0.017453292F;
            this.setVelocity(this.getVelocity().add(-MathHelper.sin(g) * 0.2F, 0.0, MathHelper.cos(g) * 0.2F));
        }

        this.velocityDirty = true;
    }
}
