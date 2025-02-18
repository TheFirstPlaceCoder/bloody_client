package mixin;

import com.client.impl.function.visual.CameraTweaks;
import com.client.impl.function.visual.Freecam;
import com.client.system.function.FunctionManager;
import com.client.utils.auth.Loader;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract double clipToSpace(double desiredCameraDistance);

    @Shadow private boolean thirdPerson;
    @Unique private float tickDelta;

    @Unique private Freecam freecam;
    @Unique private CameraTweaks cameraTweaks;

    @Inject(method = "update", at = @At("HEAD"))
    private void onUpdateHead(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        this.tickDelta = tickDelta;
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;moveBy(DDD)V", ordinal = 0))
    private void modifyCameraDistance(Args args) {
        if (freecam == null) freecam = FunctionManager.get(Freecam.class);
        if (cameraTweaks == null) cameraTweaks = FunctionManager.get(CameraTweaks.class);

        args.set(0, -clipToSpace(cameraTweaks.getDistance()));
        if (!Loader.unHook && freecam.isEnabled()) {
            args.set(0, -clipToSpace(0));
        }
    }

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    private void onClipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> info) {
        if (cameraTweaks == null) cameraTweaks = FunctionManager.get(CameraTweaks.class);

        if (cameraTweaks.clip()) {
            info.setReturnValue(desiredCameraDistance);
        }
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdateTail(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        if (freecam == null) freecam = FunctionManager.get(Freecam.class);

        if (!Loader.unHook && freecam.isEnabled()) {
            this.thirdPerson = true;
        }
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void onUpdateSetPosArgs(Args args) {
        if (freecam == null) freecam = FunctionManager.get(Freecam.class);

        if (freecam.isEnabled()) {
            args.set(0, freecam.getX(tickDelta));
            args.set(1, freecam.getY(tickDelta));
            args.set(2, freecam.getZ(tickDelta));
        }
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void onUpdateSetRotationArgs(Args args) {
        if (freecam == null) freecam = FunctionManager.get(Freecam.class);

        if (freecam.isEnabled()) {
            args.set(0, (float) freecam.getYaw(tickDelta));
            args.set(1, (float) freecam.getPitch(tickDelta));
        }
    }
}