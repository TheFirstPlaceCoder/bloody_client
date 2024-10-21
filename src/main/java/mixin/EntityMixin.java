package mixin;

import api.main.EventUtils;
import com.client.event.events.BoundingBoxEvent;
import com.client.event.events.ChangeSprintEvent;
import com.client.event.events.GlintRenderEvent;
import com.client.event.events.InvisibleEvent;
import com.client.impl.function.combat.HitBox;
import com.client.impl.function.combat.aura.rotate.RotationHandler;
import com.client.impl.function.movement.NoPush;
import com.client.impl.function.visual.Shaders;
import com.client.impl.function.visual.Freecam;
import com.client.system.function.FunctionManager;
import com.client.utils.Utils;
import com.client.utils.render.Outlines;
import com.client.utils.render.ScoreboardEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.UUID;

import static com.client.system.function.Function.mc;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow protected abstract void setFlag(int index, boolean value);
    @Shadow
    private Box entityBounds;

    @Shadow public abstract void tick();

    @Shadow public abstract boolean equals(Object o);

    @Shadow private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        return null;
    }

    @Shadow public float yaw;

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow public abstract Vec3d getVelocity();

    @Shadow public abstract EntityType<?> getType();

    @Shadow public abstract UUID getUuid();

    @Shadow public abstract Text getName();

    @Shadow
    public World world;

    boolean updating = false;
    AbstractTeam team;

    @Inject(
            at = {@At("TAIL")},
            method = {"<init>"}
    )
    private void init(CallbackInfo info) {
        ScoreboardEvent.subscribe(this::UpdateTeam);
    }

    @Shadow
    @Nullable
    public abstract AbstractTeam getScoreboardTeam();

    void UpdateTeam() {
        this.updating = true;
        this.team = this.getScoreboardTeam();
        this.updating = false;
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"getScoreboardTeam"},
            cancellable = true
    )
    void getScoreboardTeam(CallbackInfoReturnable<AbstractTeam> cir) {
        if (!this.updating) {
            cir.setReturnValue(this.team);
            cir.cancel();
        }

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

    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
    private void onGetTargetingMargin(CallbackInfoReturnable<Float> info) {
        double v = FunctionManager.get(HitBox.class).getEntityValue((Entity) (Object) this);
        if (v != 0) info.setReturnValue((float) v);
    }

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void changeLook(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if (this.equals(mc.player)) {
            Freecam freecam = FunctionManager.get(Freecam.class);
            if (freecam.isEnabled()) {
                freecam.changeLookDirection(cursorDeltaX * 0.15, cursorDeltaY * 0.15);
                ci.cancel();
            }
        }
    }

    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    private void setSprinting(boolean sprinting, CallbackInfo ci) {
        ChangeSprintEvent event = new ChangeSprintEvent(sprinting);
        event.post();
        if (event.isCancelled() && this.equals(mc.player)) {
            setFlag(3, event.set);
            ci.cancel();
        }
    }

    @Inject(method = "updateVelocity", at = @At("HEAD"), cancellable = true)
    private void updateVelocity(float speed, Vec3d movementInput, CallbackInfo ci) {
        ci.cancel();
        if (this.equals(mc.player) && RotationHandler.checkMoveFix()) {
            Vec3d vector3d = getAbsoluteMotion(movementInput, speed, RotationHandler.serverYaw);
            this.setVelocity(this.getVelocity().add(vector3d));
            return;
        }
        Vec3d vec3d = movementInputToVelocity(movementInput, speed, this.yaw);
        this.setVelocity(this.getVelocity().add(vec3d));
    }

    @Unique
    private Vec3d getAbsoluteMotion(Vec3d relative, float p_213299_1_, float facing) {
        double d0 = relative.lengthSquared();
        if (d0 < 1.0E-7D) {
            return Vec3d.ZERO;
        } else {
            Vec3d vector3d = (d0 > 1.0D ? relative.normalize() : relative).multiply((double) p_213299_1_);
            float f = MathHelper.sin(facing * ((float) Math.PI / 180F));
            float f1 = MathHelper.cos(facing * ((float) Math.PI / 180F));
            return new Vec3d(vector3d.x * (double) f1 - vector3d.z * (double) f, vector3d.y, vector3d.z * (double) f1 + vector3d.x * (double) f);
        }
    }

    @Inject(method = "setInvisible", at = @At("HEAD"), cancellable = true)
    private void isInvisible(boolean invisible, CallbackInfo ci) {
        InvisibleEvent event = new InvisibleEvent();
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
            setFlag(5, false);
        }
    }

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void isGlowing(CallbackInfoReturnable<Boolean> info) {
        GlintRenderEvent event = new GlintRenderEvent();
        event.post();
        if (event.isCancelled()) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void onGetTeamColorValue(CallbackInfoReturnable<Integer> info) {
        if (Outlines.renderingOutlines) {
            info.setReturnValue(Utils.fromRGBA(FunctionManager.get(Shaders.class).getColor((Entity) (Object) this)));
        }
    }

    @ModifyArgs(method = "pushAwayFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void pushAwayFromHook(Args args) {
        NoPush noPush = FunctionManager.get(NoPush.class);

        //Condition '...' is always 'false' is a lie!!! do not delete
        if ((Object) this == MinecraftClient.getInstance().player && noPush.isEnabled() && noPush.entity.get()) {
            args.set(0, 0.);
            args.set(1, 0.);
            args.set(2, 0.);
        }
    }

    @Redirect(method = "updateMovementInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;getVelocity(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d updateMovementInFluid(FluidState state, BlockView world, BlockPos pos) {
        Vec3d vec = state.getVelocity(world, pos);
        NoPush noPush = FunctionManager.get(NoPush.class);
        if (noPush.isEnabled() && noPush.liquids.get()) {
            vec = vec.multiply(0f, 0f, 0f);
        }
        return vec;
    }
}