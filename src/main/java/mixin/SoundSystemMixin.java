package mixin;

import com.client.event.events.SoundEvent;
import com.client.utils.files.SoundManager;
import com.client.utils.misc.CustomSoundInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {
    @Shadow protected abstract float getSoundVolume(@Nullable SoundCategory category);

    @Inject(
            method = "play(Lnet/minecraft/client/sound/SoundInstance;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onSoundPlayed(SoundInstance sound, CallbackInfo ci) {
        SoundEvent event = SoundEvent.get(sound, sound.getSoundSet(MinecraftClient.getInstance().getSoundManager()));
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "getAdjustedVolume(Lnet/minecraft/client/sound/SoundInstance;)F",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getVolume(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(MathHelper.clamp(sound.getVolume() * (sound instanceof CustomSoundInstance ? 1 : this.getSoundVolume(sound.getCategory())), 0.0F, 1.0F));
    }
}