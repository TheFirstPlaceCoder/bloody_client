package mixin;

import api.main.EventUtils;
import com.client.event.events.ClipAtLedgeEvent;
import com.client.event.events.PlayerTravelEvent;
import com.client.impl.function.movement.Sprint;
import com.client.system.function.FunctionManager;
import com.client.utils.misc.FunctionUtils;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.client.BloodyClient.mc;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    protected void clipAtLedge(CallbackInfoReturnable<Boolean> info) {
        ClipAtLedgeEvent event = ClipAtLedgeEvent.get();

        EventUtils.post(event);

        if (event.isSet()) info.setReturnValue(event.isClip());
    }

    @Unique private Sprint sprint;

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V", shift = At.Shift.AFTER))
    public void attackAHook(CallbackInfo callbackInfo) {
        if (sprint == null) sprint = FunctionManager.get(Sprint.class);
        if (sprint.keepSprint()) {
            sprint.set();
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravelhookPre(Vec3d movementInput, CallbackInfo ci) {
        if (mc.player == null)
            return;

        PlayerTravelEvent event = PlayerTravelEvent.get(movementInput, true);
        event.post();
        if (event.isCancelled()) {
            mc.player.move(MovementType.SELF, mc.player.getVelocity());
            ci.cancel();
        }
    }


    @Inject(method = "travel", at = @At("RETURN"), cancellable = true)
    private void onTravelhookPost(Vec3d movementInput, CallbackInfo ci) {
        if(mc.player == null)
            return;

        PlayerTravelEvent event = PlayerTravelEvent.get(movementInput, false);
        event.post();
        if (event.isCancelled()) {
            mc.player.move(MovementType.SELF, mc.player.getVelocity());
            ci.cancel();
        }
    }
}