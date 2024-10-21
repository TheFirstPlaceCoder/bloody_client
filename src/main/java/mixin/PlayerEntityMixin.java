package mixin;

import com.client.event.events.PlayerTravelEvent;
import com.client.impl.function.movement.Sprint;
import com.client.system.function.FunctionManager;
import com.client.utils.misc.FunctionUtils;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.client.BloodyClient.mc;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V", shift = At.Shift.AFTER))
    public void attackAHook(CallbackInfo callbackInfo) {
        if (FunctionManager.get(Sprint.class).keepSprint()) {
            FunctionManager.get(Sprint.class).set();
        }
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", ordinal = 0), index = 4)
    public SoundEvent attackSoundHook(SoundEvent soundEvent) {
        if (FunctionUtils.soundEvent != null) {
            return FunctionUtils.soundEvent;
        }

        return soundEvent;
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", ordinal = 1), index = 4)
    public SoundEvent attackSoundHook1(SoundEvent soundEvent) {
        if (FunctionUtils.soundEvent != null) {
            return FunctionUtils.soundEvent;
        }

        return soundEvent;
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", ordinal = 2), index = 4)
    public SoundEvent attackSoundHook2(SoundEvent soundEvent) {
        if (FunctionUtils.soundEvent != null) {
            return FunctionUtils.soundEvent;
        }

        return soundEvent;
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", ordinal = 0), index = 4)
    public SoundEvent attackSoundHook3(SoundEvent soundEvent) {
        if (FunctionUtils.soundEvent != null) {
            return FunctionUtils.soundEvent;
        }

        return soundEvent;
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", ordinal = 3), index = 4)
    public SoundEvent attackSoundHook4(SoundEvent soundEvent) {
        if (FunctionUtils.soundEvent != null) {
            return FunctionUtils.soundEvent;
        }

        return soundEvent;
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", ordinal = 4), index = 4)
    public SoundEvent attackSoundHook5(SoundEvent soundEvent) {
        if (FunctionUtils.soundEvent != null) {
            return FunctionUtils.soundEvent;
        }

        return soundEvent;
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", ordinal = 5), index = 4)
    public SoundEvent attackSoundHook6(SoundEvent soundEvent) {
        if (FunctionUtils.soundEvent != null) {
            return FunctionUtils.soundEvent;
        }

        return soundEvent;
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